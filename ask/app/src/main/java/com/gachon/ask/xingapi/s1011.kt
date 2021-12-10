package com.gachon.ask.xingapi

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.ebest.api.rm.ResourceManager
import com.gachon.ask.datamngr.API_DEFINE
import com.gachon.ask.R
import com.gachon.ask.util.Auth
import com.gachon.ask.util.Firestore
import com.gachon.ask.util.model.Stock
import com.gachon.ask.util.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

class s1011 : Fragment() {

    var userStock = ArrayList<Stock>();
    internal var m_nHandle = -1
    internal var handler: ProcMessageHandler? = null
    lateinit internal var manager: SocketManager

    internal var m_strcts_expcode = ""  // 연속처리시 사용

    lateinit var m_editAccPwd: EditText
    lateinit var m_combo_acc: Spinner

    internal inner class ProcMessageHandler : Handler() {

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // 서버에서 보내는 시스템 ERROR
                API_DEFINE.RECEIVE_SYSTEMERROR -> {
                    val pMsg = msg.obj as MsgPacket
                    Toast.makeText(activity?.applicationContext, pMsg.strMessageData, Toast.LENGTH_SHORT).show()
                }

                // TR데이타
                API_DEFINE.RECEIVE_DATA -> {
                    val lpDp = msg.obj as DataPacket

                    processT0424(lpDp.pData!!)
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
                }

                // TR메세지
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(activity?.applicationContext, lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData, Toast.LENGTH_SHORT).show()
                }
                // 접속종료 또는 재연결
                API_DEFINE.RECEIVE_DISCONNECT ,  API_DEFINE.RECEIVE_RECONNECT->{
                    mainView.onMessage(msg);
                }
            }
        }
    }

    lateinit var m_gridView: GridView
    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1011_item01)
    lateinit var mainView: MainView
    lateinit var root: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.activity_s1011, container, false)
        mainView = (activity as MainView)
        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()

        // 그리드 초기화
        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter
        // 버튼
        root.findViewById<Button>(R.id.btn_query).setOnClickListener {
            // 조회버튼 클
            requestT0424()
        }

        m_editAccPwd = root!!.findViewById(R.id.editAccPwd) as EditText

        // 계좌 콤보박스
        m_combo_acc = root!!.findViewById(R.id.combo_acc) as Spinner
        // 계좌를 가져와서 spinner 목록에서 보여주기 위함
        var items = getAccountList();
        val myAdapter = ArrayAdapter(root!!.context, R.layout.spinneritem, items)
        m_combo_acc.adapter = myAdapter

        // 계좌콤보 리스너
        m_combo_acc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var t = (parent.getChildAt(0) as TextView)
                //t.setTextColor(Color.BLUE)
                //(parent.getChildAt(0) as TextView).textSize = 10f
                t.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    ResourceManager.calcFontSize(t.textSize.toInt())
                )

                var atrAccount = m_combo_acc.getItemAtPosition(position) as String
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {
                    }
                    1 -> {
                    }
                    //...
                    else -> {
                    }
                }
            }
        }

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

    //계좌확인
    fun getAccountList() : java.util.ArrayList<String> {
        var temp : java.util.ArrayList<String> = arrayListOf();

        if(manager!!.isConnect() == false) return arrayListOf();
        var tempList = manager!!.getAccountList();
        var tempSize = manager!!.getAccountCount() as Int;

        for( i in 0.. tempSize-1){
            temp.add(tempList?.get(i)?.get(0) as String);   // 계좌번호
//            temp.add(tempList?.get(i)?.get(1) as String);   // 계좌명
//            temp.add(tempList?.get(i)?.get(2) as String);   // 상품유형코드
//            temp.add(tempList?.get(i)?.get(3) as String);   // 관리지점번호
        }

        return temp;

    }

    private fun requestT0424() {

        var strAccount = m_combo_acc.selectedItem as String?

        if(strAccount!!.isEmpty()) return

        var strAccPwd :String? = m_editAccPwd.text.toString()

        if(strAccPwd!!.isEmpty()) return

        // 레이블 데이터 삭제
        (root!!.findViewById(R.id.txtView_sunamt)   as TextView).text    = ""
        (root!!.findViewById(R.id.txtView_dtsunik)  as TextView).text    = ""
        (root!!.findViewById(R.id.txtView_mamt)     as TextView).text    = ""
        (root!!.findViewById(R.id.txtView_sunamt1)  as TextView).text    = ""

        // 그리드 데이터 삭제
        m_adapter.resetItems()
        m_adapter.notifyDataSetChanged()

        var strInBlock =
            CommonFunction.makeSpace(strAccount,11) + " " +   // 계좌번호
            CommonFunction.makeSpace(strAccPwd , 8) + " " +   // 비밀번호
            CommonFunction.makeSpace(   "1", 1) + " " +   // 단가구분 1 : 평균단가 2 : BEP단가
            CommonFunction.makeSpace(   "0", 1) + " " +   // 체결구분 0 : 결제기준 2 : 체결기준(잔고가 0이 아닌 종목)
            CommonFunction.makeSpace(   "0", 1) + " " +   // 단일가구분 0 : 정규장 2 : 시간외단일가
            CommonFunction.makeSpace(   "1", 1) + " " +   // 제비용미포함여부 0 : 제비용미포함 1 : 제비용포함
            CommonFunction.makeSpace(    "",22) + " "

        var nRqID = 0
        if ( m_strcts_expcode != "" )
            // 연속 조회
            nRqID = manager.requestData(m_nHandle, "t0424", strInBlock, true, "", 30)
        else
            // 그냥 조회
            nRqID = manager.requestData(m_nHandle, "t0424", strInBlock, false, "", 30)

    }

    private fun processT0424(pData: ByteArray) {

        val OutBlockName = arrayOf("t0424OutBlock", "t0424OutBlock1")
        val OutBlockOccursInfo = booleanArrayOf(false, true)
        val OutBlockLenInfo = arrayOf(
            intArrayOf(18, 18, 18, 18, 22, 18, 18),
            intArrayOf(12, 10, 18, 18, 18, 18, 18, 8, 18, 18, 18, 18, 18, 18, 18, 18, 10, 8, 20, 1, 1, 10, 8, 18, 18, 10, 10, 10, 10))

        val map = manager!!.getDataFromByte(pData, OutBlockName, OutBlockOccursInfo, OutBlockLenInfo, true, "", "B")
        if (map == null) return

        val s1 = map[OutBlockName[0]] as Array<Array<String>>?
        val s2 = map[OutBlockName[1]] as Array<Array<String>>?

        if(s1 == null) return

        val sunamt   = manager!!.getCommaValue(s1!![0][0])
        val dtsunik  = manager!!.getCommaValue(s1!![0][1])
        val mamt     = manager!!.getCommaValue(s1!![0][2])
        val sunamt1  = manager!!.getCommaValue(s1!![0][3])
        val ctxcode  = manager!!.getCommaValue(s1!![0][4])
        val tappamt  = manager!!.getCommaValue(s1!![0][5])
        val tdtsunik = manager!!.getCommaValue(s1!![0][6])

        val temp_sunamt = sunamt

        // ------------ 자산관련 부분 ------------
        (root!!.findViewById(R.id.txtView_sunamt)   as TextView).text    = sunamt
        (root!!.findViewById(R.id.txtView_dtsunik)  as TextView).text    = dtsunik
        (root!!.findViewById(R.id.txtView_mamt)     as TextView).text    = mamt
        (root!!.findViewById(R.id.txtView_sunamt1)  as TextView).text    = sunamt1
        Log.d("s1011.kt", "총 자산 : "+sunamt)

        // ASK 유저의 총 자산 데이터에 삽입, fireStore update
        updateUserMoney(temp_sunamt.replace(",","").toInt())

        // ------------ 체결내역 부분 ------------
        if (s2 != null) {
            for (i in 0..s2.size - 1) { // 거래한 종목 수만큼 반복..?

                val data_record: List<Triple<TableGrid.TYPE, Any?, Int>> = listOf(
                    Triple(TableGrid.TYPE.STRING,                           s2[i][18]               , R.id.textView1),  // 종목명
                    Triple(TableGrid.TYPE.STRING,                           s2[i][1]                , R.id.textView2),  // 잔고구분
                    Triple(TableGrid.TYPE.STRING,                           s2[i][2]                , R.id.textView3),  // 잔고수량
                    Triple(TableGrid.TYPE.STRING, manager!!.getCommaValue(  s2[i][4])               , R.id.textView4),  // 평균단가
                    Triple(TableGrid.TYPE.STRING, manager!!.getCommaValue(  s2[i][5])               , R.id.textView5),  // 매입금액
                    Triple(TableGrid.TYPE.STRING, manager!!.getCommaValue(  s2[i][25], 2) , R.id.textView6)   // 수익률
                )
                Log.d("s1011.kt","종목명 : "+data_record[0].second.toString()+", 잔고수량 :  "+data_record[2].second.toString()+", 수익률 : "+data_record[5].second.toString())
                // ASK 유저의 체결 내역을 삽입, fireStore update
                updateUserStock(
                    data_record[0].second.toString(),
                    data_record[5].second.toString(),
                    data_record[2].second.toString()
                )
                m_adapter.addItem(data_record)
            }
        }
        m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.
    }

    // ASK 유저의 계정에 자산 동기화시키는 함수
    private fun updateUserMoney(userMoney:Int){
        Firestore.updateUserMoney(Auth.getCurrentUser().uid, userMoney).addOnSuccessListener(object : OnSuccessListener<Void?> {
            override fun onSuccess(unused: Void?) {
               //Toast.makeText(context, "자산이 성공적으로 저장됨",Toast.LENGTH_LONG).show()
            }
        })
    }

    // ASK 유저의 체결 내역을 삽입, fireStore update
    private fun updateUserStock(stockName:String, stockYield:String, stockNum:String){
        val stock = Stock(stockName,stockYield,stockNum)
        userStock.add(stock) // 지속적으로 업데이트
        Firestore.updateUserStock(Auth.getCurrentUser().uid, userStock).addOnSuccessListener(object : OnSuccessListener<Void?> {
            override fun onSuccess(unused: Void?) {
                //Toast.makeText(context, "보유 주식 저장됨",Toast.LENGTH_LONG).show()
            }
        })
    }
}