package com.gachon.ask.xingapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import android.os.*
import com.ebest.api.*

import com.gachon.ask.datamngr.DataMngr
import com.gachon.ask.datamngr.RealDataMngr
import com.gachon.ask.datamngr.RealHandler

import com.gachon.ask.R
import com.gachon.ask.datamngr.API_DEFINE

class s1001_DM : Fragment() {

    internal var handler: ProcMessageHandler? = null

    var m_S3_ = RealDataMngr()
    var m_K3_ = RealDataMngr()

    var m_RealHanler : RealHandler? = RealHandler()


    lateinit internal var manager: SocketManager
    internal var m_nHandle = -1
    internal var m_strJongmokCode = ""
    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1001_item01)
    lateinit var m_gridView: GridView
    lateinit var mainView: MainView
    lateinit var root: View


    var m_strNextKey = ""
    var m_bNextQuery = false
    lateinit internal var m_buttonNext:Button


    internal inner class ProcMessageHandler : Handler() {
        /* RECEIVE 결과값 */
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            //if( msg.what == API_DEFINE.RECEIVE_REALDATA)
                if( m_RealHanler!!.procMsgHandler(msg.what, msg.obj) == true)
                    return

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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* 레이아웃 호출 및 메인 액티비티 연결 */
          root = inflater.inflate(R.layout.activity_s1001, container, false)
        mainView = (activity as MainView)

        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()


        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter
        m_adapter.setMaxCount(-1);

        root.findViewById<Button>(R.id.button).setOnClickListener {
            OnButtonQueryClicked()
        }

        m_buttonNext = root.findViewById<Button>(R.id.button2);
        m_buttonNext.setOnClickListener {
            OnButtonNextQueryClicked()
        }

        m_nHandle = manager.setHandler(mainView, handler as Handler)

        //==========================================================================================//
        // 화면에서 사용될 실시간 Instance 얻어오기
        m_S3_ =  RealDataMngr.getInstance(manager, "S3_")!!
        m_K3_ =  RealDataMngr.getInstance(manager, "K3_")!!

        //==========================================================================================//
        // 실시간을 핸들러에 등록
        m_RealHanler!!.addRealDataMngr(m_S3_)
        m_RealHanler!!.addRealDataMngr(m_K3_)

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

        // 현재 수신받고 있는 종목의 실시간정보를 삭제한다.
        if ( m_strJongmokCode.length > 0 ) {
            m_S3_.removeItems(manager, m_nHandle, m_strJongmokCode)
            m_K3_.removeItems(manager, m_nHandle, m_strJongmokCode)
        }

        m_RealHanler!!.removeRealDataMngr(m_S3_)
        m_RealHanler!!.removeRealDataMngr(m_K3_)

        /* 해당 화면을 사용하지 않을떄 핸들값 삭제 */
        manager.deleteHandler(m_nHandle)
    }

    fun OnButtonQueryClicked() {

        queryT1301(false)
    }

    fun OnButtonNextQueryClicked(){

        queryT1301(true)
    }

    fun queryT1301(bNext : Boolean = false){

        val temp = root.findViewById<EditText>(R.id.editText2).text.toString()
        if (temp.length < 6) {
            Toast.makeText(
                activity?.applicationContext,
                "종목코드를 확인해 주십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if( bNext == true ) {
            if( m_strNextKey.length <= 0 ) return

            if( m_strJongmokCode.length > 0 && m_strJongmokCode != temp ){
                Toast.makeText( activity?.applicationContext, "최초 조회한 종목코드와 다릅니다.", Toast.LENGTH_LONG ).show()
                return
            }
        }
        else {
            m_adapter.items.clear()
            m_adapter.notifyDataSetChanged()

            // 현재 수신받고 있는 종목의 실시간정보를 삭제한다.
            if (m_strJongmokCode.length > 0) {
                m_S3_.removeItems(manager, m_nHandle, m_strJongmokCode)
                m_K3_.removeItems(manager, m_nHandle, m_strJongmokCode)
            }

            m_strJongmokCode = temp
        }

//================================================================================================//
// 데이터 메니저를 사용하는 방법
//================================================================================================//
        var t1301 = DataMngr.getInstance(manager, "t1301")!!

        //------------------------------------------------------------------------------------------
        // 입력
        t1301.writeFieldData( "t1301InBlock", "shcode   ", m_strJongmokCode  )
        t1301.writeFieldData( "t1301InBlock", "cvolume  ", ""  )
        t1301.writeFieldData( "t1301InBlock", "starttime", ""  )
        t1301.writeFieldData( "t1301InBlock", "endtime  ", ""  )
        t1301.writeFieldData( "t1301InBlock", "cts_time ", if(bNext) m_strNextKey else  ""  )

        //------------------------------------------------------------------------------------------
        //   전송
        //   TR 전송 제한으로 인해 조회시 시간을 입력받아 초당전송 제한에 걸리는 문제를 최소화 하기 위해 request에 기능 추가
        //   nLastSec -> -1:즉시전송, 0:초당전송시간이 지난 후에 전송, < 0 : nLastSec초가 지난 후에 전송
        //   fun request( sm : SocketManager, nHandler : Int, bNext: Boolean = false, strContinueKey: String = "", nLaterSec : Int = -1, nTimeOut: Int = 30 ) : Int
        var nRqID = t1301.request( manager, m_nHandle, bNext )
        if( nRqID < 0 ) {
            Toast.makeText( activity?.applicationContext, "TR전송실패(" + nRqID + ")", Toast.LENGTH_LONG ).show()
            return
        }

        m_buttonNext.setEnabled(false)

        //------------------------------------------------------------------------------------------
        // 수신
        t1301.setOnRecvListener( object : DataMngr.OnRecvListener {
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

                if( dm == t1301 ){
                    // 그리드 데이터
                    var nCount = dm.getBlockCount( "t1301OutBlock1" )
                    for( i in 0 .. nCount-1 ) {

                        val data_record: List<Triple<TableGrid.TYPE, Any?, Int>> = listOf(

                            Triple( TableGrid.TYPE.STRING, manager.getTimeFormat(dm.readFieldData    ( "t1301OutBlock1","chetime", i)), R.id.view1  ),
                            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(dm.readFieldData    ( "t1301OutBlock1","price"  , i)), R.id.view2  ),
                            Triple( TableGrid.TYPE.DAEBI ,                       dm.readFieldAttrData( "t1301OutBlock1","price"  , i ), R.id.view2  ),
                            Triple( TableGrid.TYPE.ICON,                         dm.readFieldData    ( "t1301OutBlock1","sign"   , i ), R.id.view3_1),
                            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(dm.readFieldData    ( "t1301OutBlock1","change" , i)), R.id.view3_2),
 //                           Triple( TableGrid.TYPE.DAEBI ,                       dm.readFieldAttrData( "t1301OutBlock1","change" , i ), R.id.view3_2),
                            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(dm.readFieldData    ( "t1301OutBlock1","cvolume"   , i)), R.id.view4  ),
 //                           Triple( TableGrid.TYPE.DAEBI ,                       dm.readFieldAttrData( "t1301OutBlock1","cvolume"   , i ), R.id.view4  ),
                            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(dm.readFieldData    ( "t1301OutBlock1","volume" , i)), R.id.view5  )
                        )
                        m_adapter.addItem(data_record)
                    }
                    m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.

                    if( dm.isContinue() == true ){
                        // 연속키
                        m_strNextKey = dm.readFieldData( "t1301OutBlock","cts_time").trim()
                        if (m_strNextKey.length > 0) m_buttonNext.setEnabled(true)
                        else                         m_buttonNext.setEnabled(false)
                    }

                    if( bNext == false ){
                        // 실시간을 등록한다.
//================================================================================================//
// 실시간 데이터 메니저를 사용하는 방법
//================================================================================================//
                        m_S3_.addItems(manager, m_nHandle, m_strJongmokCode)
                        m_S3_.setOnRecvListener( object : RealDataMngr.OnRecvListener {

                            override fun onData( rdm : RealDataMngr) {
                                if( rdm == m_S3_ ) {
                                    processReal_S3_K3_(rdm)
                                }
                            }
                        })

                        m_K3_.addItems(manager, m_nHandle, m_strJongmokCode)
                        m_K3_.setOnRecvListener( object : RealDataMngr.OnRecvListener {

                            override fun onData( rdm : RealDataMngr) {
                                if( rdm == m_K3_ ) {
                                    processReal_S3_K3_(rdm)
                                }
                            }
                        })
                    }
                }
            }
        } )
    }


    fun processReal_S3_K3_(rdm : RealDataMngr){

        /* 데이터 처리 */
        val data_record: List<Triple<TableGrid.TYPE, Any, Int>> = listOf(
            Triple( TableGrid.TYPE.STRING, manager.getTimeFormat(rdm.readFieldData    ( "chetime")), R.id.view1  ),
            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(rdm.readFieldData    ( "price"  )), R.id.view2  ),
            Triple( TableGrid.TYPE.DAEBI ,                       rdm.readFieldAttrData( "price"   ), R.id.view2  ),
            Triple( TableGrid.TYPE.ICON,                         rdm.readFieldData    ( "sign"    ), R.id.view3_1),
            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(rdm.readFieldData    ( "change" )), R.id.view3_2),
  //          Triple( TableGrid.TYPE.DAEBI ,                       rdm.readFieldAttrData( "change"  ), R.id.view3_2),
            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(rdm.readFieldData    ( "cvolume"   )), R.id.view4  ),
 //           Triple( TableGrid.TYPE.DAEBI ,                       rdm.readFieldAttrData( "cvol"    ), R.id.view4  ),
            Triple( TableGrid.TYPE.STRING, manager.getCommaValue(rdm.readFieldData    ( "volume" )), R.id.view5  )
            )

        m_adapter.addItem(0, data_record)
        m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.
    }


}

