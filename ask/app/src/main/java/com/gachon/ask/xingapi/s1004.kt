package com.gachon.ask.xingapi

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.gachon.ask.datamngr.API_DEFINE
import com.gachon.ask.R
import com.gachon.ask.util.Firestore

class s1004 : Fragment() {


    internal var m_nHandle = -1
    internal var handler: ProcMessageHandler? = null
    lateinit internal var manager: SocketManager

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
                    if (lpDp.strTRCode == "t1101") {
                        processT1101(lpDp.pData!!)
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
                    if (lpRp.strBCCode == "H1_") {
                        processH1_(lpRp.strKeyCode, lpRp.pData)
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

    lateinit var m_gridView: GridView
    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1004_item02)
    lateinit var mainView: MainView
    lateinit var root: View
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.activity_s1004, container, false)
        mainView = (activity as MainView)
        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()


        root.findViewById<Button>(R.id.btn_query).setOnClickListener {
            requestT1101()
        }
        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter

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

    private var m_shcode = ""
    private fun requestT1101() {
        val edit = root.findViewById<EditText>(R.id.editText4)
        var shcode = edit.text.toString()

        Firestore.getMockCode().addOnSuccessListener { documentSnapshot ->
            val mockMap = documentSnapshot.data
            Log.d("s1001_DM", "mockMap.get(삼성전자) : " + mockMap!![shcode].toString())
            if (mockMap[shcode] != null) {
                shcode = mockMap!![shcode].toString()
            }
            Log.d("s1001_DM", "temp값 테스트(함수 안) : " + shcode)

            if (shcode.length < 6) {
                Toast.makeText(
                    activity?.applicationContext,
                    "종목코드를 확인해 주십시오.",
                    Toast.LENGTH_SHORT
                ).show()
                // return
            }
            if (m_shcode.length > 0) {
                var bOK = manager.deleteRealData(m_nHandle, "H1_", m_shcode, 6)
            }
            m_shcode = shcode
            manager.requestData(m_nHandle, "t1101", m_shcode, false, "", 30)
        }
    }

    private fun processT1101(pData: ByteArray) {

        /* 방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
         var map = manager!!.getOutBlockDataFromByte("t1101", "t1101OutBlock", pData!!)
        var pArray = manager.getAttributeFromByte("t1101", "t1101OutBlock", pData) // attribute
         */

        /* 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우  */
        val bAttributeInData = true
        var map = manager.getDataFromByte(pData, TRCODE.n1101col, bAttributeInData)
        var pArray = manager.getAttributeFromByte(pData, TRCODE.n1101col) // attribute


        /* 데이터 처리 */
        m_adapter.resetItems()
        m_adapter.notifyDataSetChanged()

        if (map != null) {
            for (i in 0..map.size - 1) {

                /*
                실데이터는 getOutBlockDataFromByte 에서 불러온 정보를 이용
                대비구분을 위한 데이터 getAttributeFromByte 에서 불러온 정보를 이용
                map의 index번호는 (TR 구조체의 인덱스넘버를 직접입력 또는 TRCODE.kt에서 선언된 enum class의 ordinal 값 사용 )
                */
                val arrayData: ArrayList<String> = ArrayList()
                TRCODE.T1101.values().forEach {
                    val temp = map[i]?.get(it.ordinal).toString()
                    arrayData.add(temp)
                }

                val price = manager.getCommaValue(arrayData.get(TRCODE.T1101.PRICE.ordinal))

                var _priceidx = 0
                val templist: ArrayList<Pair<String, String>> = ArrayList()
                for (idx in 0..9) {
                    //매수호가
                    val sub_bidho = (7 + 1) + ((9 - idx) * 6)
                    val sub_bidho_data = manager.getCommaValue(arrayData.get(sub_bidho))

                    //매수호가 수량
                    val sub_bidrem = (7 + 3) + ((9 - idx) * 6)
                    val sub_bidrem_data = manager.getCommaValue(arrayData.get(sub_bidrem))

                    val _pair: Pair<String, String> = Pair(sub_bidho_data, sub_bidrem_data)
                    templist.add(_pair)
                }

                for (idx in 0..9) {
                    //매도호가
                    val sub_offerho = (7 + 0) + (idx * 6)
                    val sub_offerho_data = manager.getCommaValue(arrayData.get(sub_offerho))

                    //매도호가 수량
                    val sub_offerrem = (7 + 2) + (idx * 6)
                    val sub_offerrem_data = manager.getCommaValue(arrayData.get(sub_offerrem))

                    val _pair: Pair<String, String> = Pair(sub_offerho_data, sub_offerrem_data)
                    templist.add(_pair)

                }
                templist.reverse()
                _priceidx = templist.size - _priceidx

                for (idx in 0..templist.size - 1) {

                    val style = Bundle()
                    style.putInt(TableGrid.GRAVITY, Gravity.CENTER)

                    val _value = templist.get(idx).first

                    var amount_l = templist.get(idx).second
                    var amount_r = amount_l
                    if ((idx - 9) <= 0) {
                        style.putInt(TableGrid.BACKGROUND, Color.rgb(230, 241, 255))
                        amount_r = "" //String.format("매도호가 %d",(idx+1))
                    } else {
                        style.putInt(TableGrid.BACKGROUND, Color.rgb(252, 232, 232))
                        amount_l = "" //String.format("매수호가 %d",(idx-9))
                    }

                    val data_record: List<Triple<TableGrid.TYPE, Any?, Int>> = listOf(
                        Triple(TableGrid.TYPE.STRING, amount_l, R.id.txt_left),
                        Triple(TableGrid.TYPE.STRING, _value, R.id.txt_center),
                        Triple(TableGrid.TYPE.STRING, amount_r, R.id.txt_right)
                    )

                    m_adapter.addItem(data_record)


                    if (price.equals(_value)) {
                        style.putInt(TableGrid.BACKGROUND, Color.rgb(255, 235, 60))
                    }

                    m_adapter.setCellStyle(idx, 0, null)
                    m_adapter.setCellStyle(idx, 1, style)
                    m_adapter.setCellStyle(idx, 2, null)

                }
                m_adapter.notifyDataSetChanged()


                val shcode = arrayData.get(TRCODE.T1101.SHCODE.ordinal)
                var bOK = manager.addRealData(m_nHandle, "H1_", shcode, 6)
                val name = arrayData.get(TRCODE.T1101.HNAME.ordinal)
            }
        }
    }


    private fun processH1_(strKeyCode: String?, pData: ByteArray?) {
        /*
        방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        var strArray = manager!!.getOutBlockDataFromByte("H1_", "OutBlock", pData!!)
        var pArray = manager.getAttributeFromByte("H1_", "OutBlock", pData) // attribute
         */


        /* 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우  */
        val bAttributeInData = true
        var strArray = manager.getDataFromByte(pData!!, TRCODE.nH1_col, bAttributeInData)
        var pArray = manager.getAttributeFromByte(pData!!, TRCODE.nH1_col) // attribute


        /* 데이터 처리 */
        if (strArray != null) {
            for (i in 0..strArray.size - 1) {

                val arrayData: ArrayList<String> = ArrayList()
                TRCODE.H1_.values().forEach {
                    val temp = strArray[i]?.get(it.ordinal).toString()
                    arrayData.add(temp)
                }


            }
        }

    }


}