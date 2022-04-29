package com.gachon.ask.xingapi

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.green
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.gachon.ask.datamngr.API_DEFINE
import java.util.*
import kotlin.collections.ArrayList
import com.gachon.ask.R;
import com.gachon.ask.SentimentReportActivity
import com.gachon.ask.databinding.ActivityS1002Binding
import com.gachon.ask.util.Firestore
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import kotlinx.android.synthetic.main.activity_s1002.*

class s1002 : Fragment() {

    internal var m_nHandle = -1
    internal var handler: ProcMessageHandler? = null
    lateinit internal var manager: SocketManager

    data class Candle(
        val createdAt: Long,
        val open: Float,
        val close: Float,
        val shadowHigh: Float,
        val shadowLow: Float
    )

    lateinit var price_chart : CandleStickChart

    internal inner class ProcMessageHandler : Handler() {

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {

                // 퍼미션 에러
                API_DEFINE.RECEIVE_PERMISSIONERROR -> {
                    activity?.finishAffinity();                       // 해당앱의 루트 액티비티를 종료시킨다.
                    System.runFinalization();               // 현재 작업중인 쓰레드가 종료되면 종료 시키라는 명령어
                    System.exit(0);                 // 현재 액티비티를 종료시킨다.
                }

                // INITECH 핸드세이킹 에러
                API_DEFINE.RECEIVE_INITECHERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }


                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // 서버에서 보내는 시스템 ERROR
                API_DEFINE.RECEIVE_SYSTEMERROR -> {
                    val pMsg = msg.obj as MsgPacket
                    Toast.makeText(
                        activity?.applicationContext,
                        pMsg.strMessageData,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // TR데이타
                API_DEFINE.RECEIVE_DATA -> {
                    val lpDp = msg.obj as DataPacket

                    lpDp.strTRCode?.let { Log.e(">>", it) }
                    if (lpDp.strTRCode == "t1305") {
                        processT1305(lpDp.strBlockName!!, lpDp.pData!!)
                    } else if (lpDp.strTRCode == "t8412") {
                        processT8412(lpDp.strBlockName!!, lpDp.pData!!)
                    }
                }

                // TR조회 끝
                API_DEFINE.RECEIVE_RELEASE -> {
                    val lpDp = msg.obj as ReleasePacket

                    lpDp.nRqID
                    lpDp.strTrCode
                }
                // REAL데이타
                API_DEFINE.RECEIVE_REALDATA -> {
                    val lpRp = msg.obj as RealPacket
                    if (lpRp.strBCCode == "") {

                    }
                }


                // TR메세지
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(
                        activity?.applicationContext,
                        lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData,
                        Toast.LENGTH_SHORT
                    ).show()
               }
                // 접속종료 또는 재연결
                API_DEFINE.RECEIVE_DISCONNECT ,  API_DEFINE.RECEIVE_RECONNECT->{
                    mainView.onMessage(msg);
                }
            }
        }
    }

    lateinit var mainView: MainView
    lateinit var root: View

    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1002_item01)
    lateinit var m_gridView: GridView
    //lateinit var m_gridView: TableGrid2

    private var m_dwm = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩 설정
        // mBinding = ActivityS1002Binding.inflate(inflater, container, false)

        root = inflater.inflate(R.layout.activity_s1002, container, false)
        mainView = (activity as MainView)
        price_chart = root.findViewById(R.id.priceChart)

        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter

        //m_gridView = TableGrid2(mainView)
        //val grid_layout = root.findViewById<LinearLayout>(R.id.grid_view)
        //m_gridView.InitTableGrid(7,grid_layout,null,null,null,null)

        //setCandleStickChart()

        //감성분석리포트로 연결
        val button_senti_analysis = root.findViewById<Button>(R.id.button_senti_analysis)
        button_senti_analysis.setOnClickListener {
            val intent = Intent(context, SentimentReportActivity::class.java)
            startActivity(intent)
        }



        root.findViewById<Button>(R.id.button).setOnClickListener {
            requestT1305()
            //requestT8412()
        }

        val arraylst : ArrayList<String> = ArrayList()
        arraylst.add("일")
        arraylst.add("주")
        arraylst.add("월")
        root.findViewById<Spinner>(R.id.spinner).adapter = ArrayAdapter<String>(mainView,R.layout.support_simple_spinner_dropdown_item,arraylst)

        root.findViewById<Spinner>(R.id.spinner).setOnItemSelectedListener( object  : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                m_dwm = p2+1
            }
        })
        root.findViewById<Spinner>(R.id.spinner).setSelection(0)

        val instance = Calendar.getInstance()
        val year = instance.get(Calendar.YEAR).toString()
        var month = (instance.get(Calendar.MONTH)+1).toString()
        var date = instance.get(Calendar.DATE).toString()
        if (month.toInt() < 10) {
            month = "0$month"
        }
        if (date.toInt() < 10) {
            date = "0$date"
        }
        root.findViewById<EditText>(R.id.date_edit).setText(year+month+date)
        root.findViewById<EditText>(R.id.cnt_edit).setText("20")

        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()

        return root
    }
    override fun onResume() {
        super.onResume()
        /* 화면 갱신시 핸들 재연결 */
        m_nHandle = manager.setHandler(mainView, handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        /* 해당 화면을 사용하지 않을떄 핸들값 삭제 */
        manager.deleteHandler(m_nHandle)
    }

    private fun requestT1305()
    {
        m_adapter.items.clear()
        m_adapter.notifyDataSetChanged()

        val edit = root.findViewById<EditText>(R.id.editText)
        /* size 6 */
        var shcode = edit.text.toString()

        Firestore.getMockCode().addOnSuccessListener { documentSnapshot ->
            val mockMap = documentSnapshot.data
            Log.d("s1001_DM", "mockMap.get(삼성전자) : " + mockMap!![shcode].toString())
            if (mockMap[shcode] != null){
                shcode = mockMap!![shcode].toString()
            }
            Log.d("s1001_DM", "temp값 테스트(함수 안) : " + shcode)
            if(shcode.length < 6)
            {
                Toast.makeText(
                    activity?.applicationContext,
                    "종목코드를 확인해 주십시오.",
                    Toast.LENGTH_SHORT
                ).show()
                // return
            }

            /* size 1*/
            val dwmcode = m_dwm.toString()

            val dateed = root.findViewById<EditText>(R.id.date_edit)
            /* size 8*/
            val date = dateed.text.toString()
            if(date.length < 8)
            {
                Toast.makeText(
                    activity?.applicationContext,
                    "날짜를 확인해 주십시오.",
                    Toast.LENGTH_SHORT
                ).show()
                // return
            }

            /* size 4 */
            val idx = "    "

            /* size 4 */
            val cnt = root.findViewById<EditText>(R.id.cnt_edit).text.toString().toInt()

            if( (cnt < 0) or (cnt > 10000))
            {
                Toast.makeText(
                    activity?.applicationContext,
                    "조회수량을 확인해 주십시오.",
                    Toast.LENGTH_SHORT
                ).show()
                // return
            }
            val cnt_string = String.format("%04d",cnt)

            /* inblock 데이터 필드값 사이 빈칸(" ") 필요 */
            val inblock = shcode + " " + dwmcode + " "+ date + " " + idx + " " + cnt_string

            /* TR 요청 */
            manager.requestData(m_nHandle, "t1305", inblock, false, "", 200)
        }

    }
    private fun processT1305(strBlockName: String, pData: ByteArray) {
        if (strBlockName == "t1305OutBlock" == true) {
            val strNextKey = String(pData)
            //Toast.makeText(getApplicationContext(), strBlockName + " " + new String(pData), Toast.LENGTH_SHORT).show();
        } else if (strBlockName == "t1305OutBlock1" == true) {
            processT1305OutBlock1(pData)
        }
    }

    private fun processT1305OutBlock1(pData: ByteArray){

        // 방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        var map = manager!!.getOutBlockDataFromByte("t1305", "t1305OutBlock1", pData!!)
        var pArray = manager.getAttributeFromByte("t1305", "t1305OutBlock1", pData) // attribute
        val candleList = ArrayList<Candle>()

        /*
        // 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우
        val bAttributeInData = true
        var map = manager.getDataFromByte(pData, TRCODE.n1305col, bAttributeInData)
        var pArray = manager.getAttributeFromByte(pData, TRCODE.n1305col) // attribute
        */

        if( map != null) {
            for (i in 0..map.size - 1) {

                /*
                 실데이터는 getOutBlockDataFromByte 에서 불러온 정보를 이용
               대비구분을 위한 데이터 getAttributeFromByte 에서 불러온 정보를 이용
               map의 index번호는 (TR 구조체의 인덱스넘버를 직접입력 또는 TRCODE.kt에서 선언된 enum class의 ordinal 값 사용 )               
               */

                val date = map[i]?.get(TRCODE.T1305.DATE.ordinal)
                val open = manager.getCommaValue(map[i]?.get(TRCODE.T1305.OPEN.ordinal)!!)
                val open_ = pArray?.get(i)?.get(TRCODE.T1305.OPEN.ordinal)!!
                val high = manager.getCommaValue(map[i]?.get(TRCODE.T1305.HIGH.ordinal)!!)
                val high_ = pArray?.get(i)?.get(TRCODE.T1305.HIGH.ordinal)!!
                val low = manager.getCommaValue(map[i]?.get(TRCODE.T1305.LOW.ordinal)!!)
                val low_ = pArray?.get(i)?.get(TRCODE.T1305.LOW.ordinal)!!
                val close = manager.getCommaValue(map[i]?.get(TRCODE.T1305.CLOSE.ordinal)!!)
                val close_ = pArray?.get(i)?.get(TRCODE.T1305.CLOSE.ordinal)!!

                val sign = map[i]?.get(TRCODE.T1305.SIGN.ordinal)
                val change = map[i]?.get(TRCODE.T1305.CHANGE.ordinal)

                val chdegree = map[i]?.get(TRCODE.T1305.CHDEGREE.ordinal)
                val chdegree_ = pArray[i]?.get(TRCODE.T1305.CHDEGREE.ordinal)


                val data_record: List<Triple<TableGrid.TYPE, Any?, Int>> = listOf(
                    Triple(TableGrid.TYPE.STRING,date,R.id.view1),
                    Triple(TableGrid.TYPE.STRING,open,R.id.view2),
                    Triple(TableGrid.TYPE.DAEBI,open_,R.id.view2),
                    Triple(TableGrid.TYPE.STRING,high,R.id.view3),
                    Triple(TableGrid.TYPE.DAEBI,high_,R.id.view3),
                    Triple(TableGrid.TYPE.STRING,low,R.id.view4),
                    Triple(TableGrid.TYPE.DAEBI,low_,R.id.view4),
                    Triple(TableGrid.TYPE.STRING,close,R.id.view5),
                    Triple(TableGrid.TYPE.DAEBI,close_,R.id.view5),
                    Triple(TableGrid.TYPE.DOUBLE,change,R.id.view6),
                    Triple(TableGrid.TYPE.DOUBLE,chdegree,R.id.view7),
                    Triple(TableGrid.TYPE.DAEBI,chdegree_,R.id.view7)
                )
                updateCandle(
                    candleList,
                    data_record[0].second.toString().replace(",","").toLong(),  // 날짜
                    data_record[1].second.toString().replace(",","").toFloat(), // 시가
                    data_record[7].second.toString().replace(",","").toFloat(), // 종가
                    data_record[3].second.toString().replace(",","").toFloat(), // 최고가
                    data_record[5].second.toString().replace(",","").toFloat()  // 최저가
                )
                m_adapter.addItem(data_record)

            }
            var candleSize = candleList.size-1
            // CandleList 원소들 확인
            println("CandleList 원소들 확인")
            for(index in 0..candleSize){
                println(candleList.get(index))
            }

            //initChart()
            setChartData(candleList)

            m_adapter.notifyDataSetChanged()
        }
    }

    // 조회한 데이터 수량에 대한 일봉 데이터를 candleList에 업데이트
    private fun updateCandle(candleList: ArrayList<Candle>, candleDate:Long, candleStart:Float, candleHigh:Float, candleLow:Float, candleClose:Float){
        val candle = Candle(candleDate, candleStart, candleHigh, candleLow, candleClose)
        candleList.add(candle) // 지속적으로 업데이트
    }

    /*
    object DataUtil {
        fun getCSStockData(): List<CSStock> {
            return listOf(
                CSStock(
                    createdAt = 0,
                    open = 222.8F,
                    close = 222.9F,
                    shadowHigh = 224.0F,
                    shadowLow = 222.2F
                ),
                CSStock(
                    createdAt = 1,
                    open = 222.0F,
                    close = 222.2F,
                    shadowHigh = 222.4F,
                    shadowLow = 222.0F
                ),
                CSStock(
                    createdAt = 2,
                    open = 222.2F,
                    close = 221.9F,
                    shadowHigh = 222.5F,
                    shadowLow = 221.5F
                ),
                CSStock(
                    createdAt = 3,
                    open = 222.4F,
                    close = 222.3F,
                    shadowHigh = 223.7F,
                    shadowLow = 222.1F
                ),
                CSStock(
                    createdAt = 4,
                    open = 221.6F,
                    close = 221.9F,
                    shadowHigh = 221.9F,
                    shadowLow = 221.5F
                ),
                CSStock(
                    createdAt = 5,
                    open = 221.8F,
                    close = 224.9F,
                    shadowHigh = 225.0F,
                    shadowLow = 221.0F
                ),
                CSStock(
                    createdAt = 6,
                    open = 225.0F,
                    close = 220.2F,
                    shadowHigh = 225.4F,
                    shadowLow = 219.2F
                ),
                CSStock(
                    createdAt = 7,
                    open = 222.2F,
                    close = 225.9F,
                    shadowHigh = 227.5F,
                    shadowLow = 222.2F
                ),
                CSStock(
                    createdAt = 8,
                    open = 226.0F,
                    close = 228.1F,
                    shadowHigh = 228.1F,
                    shadowLow = 225.1F
                ),
                CSStock(
                    createdAt = 9,
                    open = 227.6F,
                    close = 228.9F,
                    shadowHigh = 230.9F,
                    shadowLow = 226.5F
                ),
                CSStock(
                    createdAt = 10,
                    open = 228.6F,
                    close = 228.6F,
                    shadowHigh = 230.9F,
                    shadowLow = 228.0F
                )
            )
        }
    }*/

    private fun setChartData(candles: ArrayList<Candle>) {
        candles.reverse() // 주가 데이터 역순으로 정렬
        val priceEntries = ArrayList<CandleEntry>()
        var num = 0
        for (candle in candles) {
            // 캔들 차트 entry 생성
            priceEntries.add(
                CandleEntry(
                    num.toFloat(),     // 날짜 데이터를 개수로 변환 작업 (임시)
                    candle.shadowHigh,
                    candle.shadowLow,
                    candle.open,
                    candle.close
                )
            )
            num += 1
        }

        val priceDataSet = CandleDataSet(priceEntries, "음봉,양봉").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            // 심지 부분 설정
            shadowColor = Color.LTGRAY
            shadowWidth = 0.7F
            // 음봉 설정
            decreasingColor = Color.BLUE // open > close (시가 > 종가)인 경우 음봉이므로 파란색
            decreasingPaintStyle = Paint.Style.FILL
            // 양봉 설정
            increasingColor = Color.RED // open < close (시가 < 종가)인 경우 양봉이므로 빨간색
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.BLACK
            setDrawValues(false)
            // 터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }

        /*
        // 격자선 지우고, 깔끔하게 캔들 그래프만 보이도록 설정!
        price_chart.axisLeft.run {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            textColor = Color.TRANSPARENT
        }


        price_chart.axisRight.run {
            isEnabled = false
        }

        // X 축
        price_chart.xAxis.run {
            textColor = Color.TRANSPARENT
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setAvoidFirstLastClipping(true)
        }

        // 범례
        price_chart.legend.run {
            isEnabled = false
        }
        */

        price_chart.apply {
            this.data = CandleData(priceDataSet)
            invalidate()
        }
    }

    fun initChart() {
        price_chart.apply {
            price_chart.description.isEnabled = false
            price_chart.setMaxVisibleValueCount(200)
            price_chart.setPinchZoom(false)
            price_chart.setDrawGridBackground(false)
            // x축 설정
            price_chart.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                // 세로선 표시 여부 설정
                this.setDrawGridLines(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            // 왼쪽 y축 설정
            price_chart.axisLeft.apply {
                textColor = Color.WHITE
                isEnabled = false
            }
            // 오른쪽 y축 설정
            price_chart.axisRight.apply {
                setLabelCount(7, false)
                textColor = Color.WHITE
                // 가로선 표시 여부 설정
                setDrawGridLines(true)
                // 차트의 오른쪽 테두리 라인 설정
                setDrawAxisLine(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            price_chart.legend.isEnabled = false
        }
    }

    /*
    fun setCandleStickChart(){
        // x values
        //val entries = ArrayList<CandleEntry>()
        val xvalue = ArrayList<String>()
        xvalue.add("10:00 AM")
        xvalue.add("11:00 AM")
        xvalue.add("12:00 AM")
        xvalue.add("3:00 PM")
        xvalue.add("5:00 PM")
        xvalue.add("8:00 PM")
        xvalue.add("10:00 PM")
        xvalue.add("12:00 PM")
        // Y axis
        val candlestickentry = ArrayList<CandleEntry>()

        candlestickentry.add(CandleEntry(0f,225.0f,219.84f,224.94f,226.41f))
        candlestickentry.add(CandleEntry(1f,228.0f,222.14f,223.00f,212.41f))
        candlestickentry.add(CandleEntry(2f,226.84f,217.84f,222.9f,229.41f))
        candlestickentry.add(CandleEntry(3f,222.0f,216.12f,214.14f,216.41f))
        candlestickentry.add(CandleEntry(4f,226.56f,212.84f,224.33f,229.41f))
        candlestickentry.add(CandleEntry(5f,221.12f,269.84f,228.14f,216.41f))
        candlestickentry.add(CandleEntry(6f,220.96f,237.84f,224.94f,276.41f))

        val candledataset = CandleDataSet(candlestickentry, "first")
        candledataset.color = Color.rgb(80, 80, 80)
        candledataset.shadowColor = Color.GREEN
        candledataset.shadowWidth = 1f
        candledataset.decreasingColor = Color.RED
        candledataset.decreasingPaintStyle = Paint.Style.FILL

        candledataset.increasingColor = Color.GREEN
        candledataset.increasingPaintStyle = Paint.Style.FILL

        val candledata = CandleData(xvalue, candledataset)
        binding.priceChart.data = candledata
        binding.priceChart.setBackgroundColor(Color.WHITE)
        binding.priceChart.animateXY(3000,3000)

        val xval = price_chart.xAxis
        xval.position = XAxis.XAxisPosition.BOTTOM
        xval.setDrawGridLines(false)

    }*/

    private fun requestT8412()
    {
        /* inblock 데이터 필드값 사이 빈칸(" ") 필요 */
        val inblock =   "052900" + " " +
                        "0001" + " " +
                        "2000" + " " +
                        "0" + " " +
                        "        " + " " +
                        "      " + " " +
                        "99999999" + " " +
                        "      " + " " +
                        "        " + " " +
                        "          " + " " +
                        "Y" + " "

        /* TR 요청 */
        manager.requestData(m_nHandle, "t8412", inblock, false, "", 30)
    }

    private fun processT8412(strBlockName: String, pData: ByteArray) {
        if (strBlockName == "t8412OutBlock" == true) {
            val strNextKey = String(pData)
            val nData = 1
            //Toast.makeText(getApplicationContext(), strBlockName + " " + new String(pData), Toast.LENGTH_SHORT).show();
        } else if (strBlockName == "t8412OutBlock1" == true) {
            //processT1305OutBlock1(pData)
            //var pArray = manager.getAttributeFromByte("t1305", "t1305OutBlock1", pData)
            var pResult = manager.getDecompressData(pData)

            val strNextKey = String(pData)
            val nData = 1


            //val map: Array<Array<String>>
            //val pAttrArray: Array<ByteArray>
            val nColLen = intArrayOf(
                8, 10, 8, 8, 8, 8,12, 12, 13, 6, 1
            )
            val bAttributeInData = true
            val map = manager.getDataFromByte(pResult, nColLen, bAttributeInData)
            if( map != null) {
                val j = 1000
                val strJLast = map[j]?.get(0)
                val strJType = map[j]?.get(10)

                val i = (map?.size)?.minus(1)
                val strLast = map[i]?.get(0)
                val strType = map[i]?.get(10)
                val pAttrArray = manager.getAttributeFromByte(pData, nColLen)
            }

        }

    }
}
