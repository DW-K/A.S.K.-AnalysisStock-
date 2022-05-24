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
import com.gachon.ask.datamngr.DataMngr
import com.gachon.ask.R

class s1012 : Fragment() {

    internal            var m_nHandle = -1
    internal            var handler: ProcMessageHandler? = null
    lateinit internal   var manager: SocketManager

    lateinit            var m_editAccPwd: EditText
    lateinit            var m_combo_acc: Spinner

    internal inner class ProcMessageHandler : Handler() {

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            //---------------------------------------------------------------
            // DataMngr 메시지 처리
            if( DataMngr.Handler().procMsgHandler( activity?.applicationContext, msg.what, msg.obj ) == true ) {
                return
            }

            when(msg.what) {
                API_DEFINE.RECEIVE_ERROR -> {
                    // API 자체에서 보내는 메세지이다
                    // 여기서 Toast를 띄우던가... DataMngr에 위임하던가 하면 된다.
                    // 조회응답에 대한 에러는 request 리턴값의 RQID를 봐야 한다.
                    val strMsg = msg.obj as String
                    Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    lateinit var m_gridView: GridView
    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1012_item01)
    lateinit var mainView: MainView
    lateinit var root: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.activity_s1012, container, false)
        mainView = (activity as MainView)
        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()

        // 그리드 초기화
        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter

        // 버튼
        root.findViewById<Button>(R.id.btn_query).setOnClickListener {
            // 조회버튼 클릭
            requestT0425()
        }

        m_editAccPwd = root!!.findViewById(R.id.editAccPwd) as EditText

        // 계좌 콤보박스
        m_combo_acc = root!!.findViewById(R.id.combo_acc) as Spinner
        var items = getAccountList();
        val myAdapter = ArrayAdapter(root!!.context, R.layout.spinneritem, items)
        m_combo_acc.adapter = myAdapter

        // 계좌콤보 리스너
        m_combo_acc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

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

    private fun requestT0425() {

        var strAccount = m_combo_acc.selectedItem as String?
        if(strAccount!!.isEmpty()) return

        var strAccPwd :String? = m_editAccPwd.text.toString()
        if(strAccPwd!!.isEmpty()) return

        // 레이블 데이터 삭
        (root!!.findViewById(R.id.txtView_tqty)     as TextView).text    = ""
        (root!!.findViewById(R.id.txtView_tcheqty)  as TextView).text    = ""
        (root!!.findViewById(R.id.txtView_tordrem)  as TextView).text    = ""
        (root!!.findViewById(R.id.txtView_tamt)     as TextView).text    = ""

        // 그리드 데이터 삭제
        m_adapter.resetItems()
        m_adapter.notifyDataSetChanged()


        // DataMngr를 사용하는 방법입니다.
        var t0425 = DataMngr.getInstance( manager, "t0425" )!!

        //------------------------------------------------------------------------------------------
        // 입력
        t0425.writeFieldData( "t0425InBlock", "accno    ", strAccount  )
        t0425.writeFieldData( "t0425InBlock", "passwd   ", strAccPwd  )
        t0425.writeFieldData( "t0425InBlock", "expcode  ", ""  )
        t0425.writeFieldData( "t0425InBlock", "chegb    ", "0" )
        t0425.writeFieldData( "t0425InBlock", "medosu   ", "0" )
        t0425.writeFieldData( "t0425InBlock", "sortgb   ", "1" )
        t0425.writeFieldData( "t0425InBlock", "cts_ordno", ""  )

        //------------------------------------------------------------------------------------------
        //   전송
        //   TR 전송 제한으로 인해 조회시 시간을 입력받아 초당전송 제한에 걸리는 문제를 최소화 하기 위해 request에 기능 추가
        //   nLastSec -> -1:즉시전송, 0:초당전송시간이 지난 후에 전송, < 0 : nLastSec초가 지난 후에 전송
        //   fun request( sm : SocketManager, nHandler : Int, bNext: Boolean = false, strContinueKey: String = "", nLaterSec : Int = -1, nTimeOut: Int = 30 ) : Int
        var nRqID = t0425.request( manager, m_nHandle )

        if( nRqID < 0 ) {
            Toast.makeText( activity?.applicationContext, "TR전송실패(" + nRqID + ")", Toast.LENGTH_LONG ).show()
            return
        }

        //------------------------------------------------------------------------------------------
        // 수신
        t0425.setOnRecvListener( object : DataMngr.OnRecvListener {
            /**
             * 조회 응답
             */
            override fun onData(dm : DataMngr, sBlockName : String){

            }
            /**
             * 데이터 메세지
             */
            override fun onMsg(dm : DataMngr, sCode : String, sMsg : String, bCriticalError : Boolean){

            }
            /**
             * 조회한 서비스가 모두 종료되었을 때
             */
            override fun onComplete(dm: DataMngr) {

                val tqty    = dm.readFieldData( "t0425OutBlock", "tqty     " )  // 총주문수량
                val tcheqty = dm.readFieldData( "t0425OutBlock", "tcheqty  " )  // 총체결수량
                val tordrem = dm.readFieldData( "t0425OutBlock", "tordrem  " )  // 총미체결수량
                val cmss    = dm.readFieldData( "t0425OutBlock", "cmss     " )  // 추정수수료
                val tamt    = dm.readFieldData( "t0425OutBlock", "tamt     " )  // 총주문금액
                val tax     = dm.readFieldData( "t0425OutBlock", "tax      " )  // 추정제세금

                (root!!.findViewById(R.id.txtView_tqty)     as TextView).text    = tqty
                (root!!.findViewById(R.id.txtView_tcheqty)  as TextView).text    = tcheqty
                (root!!.findViewById(R.id.txtView_tordrem)  as TextView).text    = tordrem
                (root!!.findViewById(R.id.txtView_tamt)     as TextView).text    = tamt

                var nCount = t0425.getBlockCount( "t0425OutBlock1" )
                for( i in 0 .. nCount-1 ) {

                    val data_record: List<Triple<TableGrid.TYPE, Any?, Int>> = listOf(
                        Triple(TableGrid.TYPE.STRING, dm.readFieldData( "t0425OutBlock1", "ordno     ", i ), R.id.textView1),   // 주문번호
                        Triple(TableGrid.TYPE.STRING, dm.readFieldData( "t0425OutBlock1", "expcode   ", i ), R.id.textView2),   // 종목코드
                        Triple(TableGrid.TYPE.STRING, dm.readFieldData( "t0425OutBlock1", "medosu    ", i ), R.id.textView3),   // 구분
                        Triple(TableGrid.TYPE.STRING, dm.readFieldData( "t0425OutBlock1", "qty       ", i ), R.id.textView4),   // 수량
                        Triple(TableGrid.TYPE.STRING, dm.readFieldData( "t0425OutBlock1", "price     ", i ), R.id.textView5),   // 가격
                        Triple(TableGrid.TYPE.STRING, dm.readFieldData( "t0425OutBlock1", "status    ", i ), R.id.textView6)    // 상태
                    )
                    m_adapter.addItem(data_record)
                }

                m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.
            }
        } )
    }
}