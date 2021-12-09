package com.gachon.ask.datamngr

import android.content.Context
import com.ebest.api.DataPacket
import com.ebest.api.MsgPacket
import com.ebest.api.ReleasePacket
import com.ebest.api.SocketManager
import com.ebest.api.datamngr.ResTRLayout
import kotlin.concurrent.timer

//==================================================================================================
// 조회TR Data Manager
//==================================================================================================
open class DataMngr : ResTRLayout(){

    //==============================================================================================
    // request & receive 정보
    protected        var m_bCont          : Boolean         = false // 연속데이타 여부. false 없음 true 있음
    protected        var m_sContKey       : String          = ""    // 연속데이타Key 헤더타입이 B 인경우 사용
    protected        var m_nRqID          : Int             = -1    // Request ID
    internal         var m_nNextSendTime  : Int             = 0     // 다음 전송은 몇ms후에?, 0은 무제한 초당 전송건수 제한 회피용
    protected        var m_OnRecvListener : OnRecvListener? = null  // Receive Event Handler

    //==============================================================================================
    // Event 를 처리하는 Handler 이다. (Static Class이다)
    //
    // <처리 방식>
    // 1. activity 의 Message Hander() 에서 TR Data 받음
    // 2. 받은 TR Data 를 DataMngr.Handler().procMsgHandler() 로 전송
    // 3. DataMngr.Handler().procMsgHanderl() 에서 Request ID를 이용하여 DataMngr()를 찾음
    // 4. DataMngr Instance 의 Message Handler Call Back 함수 호출
    //==============================================================================================
    class Handler {

        //==========================================================================================
        // 서버로부터 TR이 내려왔을때 이 함수를 호출한다.
        // 이 함수에서 해당 TR로 Event를 호출한다.
        // 메시지를 처리했다면 true 를 return 한다.
        public fun procMsgHandler( context : Context?, nCode: Int, obj: Any ): Boolean {
            when (nCode) {

                API_DEFINE.RECEIVE_DATA          -> return procRecvData( obj as DataPacket )
//                API_DEFINE.RECEIVE_ERROR         -> {
//                    // API 자체에서 보내는 메세지이다.
//                    // 조회응답에 대한 에러는 request 리턴값의 RQID를 봐야 한다.
//                    val strMsg = obj as String
//                    // 여기서 Toast를 띄우던가... 전송한 넘한테 줘야하는데.... 줄 방업이 없네...
//                    // 여기서 처리하지 않고 전송 주체가 처리하려면 override fun handleMessage(msg: Message)에서 자체적으로 처리하면 된다.
//                    // 샘플에서는 받는 주체의 핸들러에서 처리한다.
//                    Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show()
//                }
                API_DEFINE.RECEIVE_MSG,
                API_DEFINE.RECEIVE_SYSTEMERROR   -> return procRecvMsg(
                    nCode == API_DEFINE.RECEIVE_SYSTEMERROR, obj as MsgPacket
                )
                API_DEFINE.RECEIVE_RELEASE       ->{
                    val dp = obj as ReleasePacket
                    return procRecvRelease( dp.nRqID, dp.strTrCode!!  )
                }
            }
            return false
        }

        //==========================================================================================
        // Receive Data 처리
        private fun procRecvData(dp: DataPacket): Boolean {
            //--------------------------------------------------------------------------------------
            // DataMngr Instance 취득
            var dm = getDataMngr( dp.nRqID, dp.strTRCode!! )
            if( dm == null ) {
                return false
            }

            dm.m_bCont = (dp.strCont == '1'.toByte() || dp.strCont == 'y'.toByte() || dp.strCont == 'Y'.toByte())
            dm.m_sContKey = dp.strContKey.trim()

            //--------------------------------------------------------------------------------------
            // Data 및 Event 처리
            if (dp.pData != null) {
                // Data를 DataMngr()에 입력
                dm.writeBlockData(dp.strBlockName!!, dp.pData!!)
                // Event 호출
                if (dm.m_OnRecvListener != null) {
                    dm.m_OnRecvListener!!.onData(dm, dp.strBlockName!!)
                }
            }

            return true
        }

        //==========================================================================================
        // Receive Release 처리
        private fun procRecvRelease( nRqID : Int, sTrCode : String ) : Boolean {
            //--------------------------------------------------------------------------------------
            // DataMngr Instance 취득
            var dm = getDataMngr( nRqID, sTrCode )
            if( dm == null ) {
                return false
            }

            //--------------------------------------------------------------------------------------
            // Event 처리
            if (dm.m_OnRecvListener != null) {
                dm.m_OnRecvListener!!.onComplete( dm )
            }

            //--------------------------------------------------------------------------------------
            // Array에서 DataMngr을 제거
            //      제거하는 시점에서 참조하는 곳이 없어지므로 메모리도 삭제가 될것이다.
            setDataMngr(nRqID, null)

            return true
        }

        //==========================================================================================
        // Receive Message 처리
        private fun procRecvMsg(bCriticalError: Boolean, mp: MsgPacket): Boolean {
            //--------------------------------------------------------------------------------------
            // DataMngr Instance 취득
            var dm = getDataMngr( mp.nRqID, mp.strTRCode!! )
            if( dm == null ) {
                return false
            }

            //--------------------------------------------------------------------------------------
            // Event 처리
            if (dm.m_OnRecvListener != null) {
                dm.m_OnRecvListener!!.onMsg( dm, mp.strMsgCode!!, mp.strMessageData!!, bCriticalError )
            }

            // Critical Error 인 경우에는 Release 가 오지 않음
            //  Release가 안오는 경우는 여기서 Array에 등록된 DataMngr()을 삭제하자
            if( bCriticalError == true ){
                procRecvRelease( mp.nRqID, mp.strTRCode!! )
            }

            return true
        }

    } // class Handler =============================================================================


    //==============================================================================================
    // Static 정의
    companion object {
        //==========================================================================================
        // TR 코드로 DataMngr() 을 구한다.
        fun getInstance( sm : SocketManager, sTrCode : String ) : DataMngr? {
            // RES를 사용하는 경우 TR에 대한 정보를 가져온다
            // RES를 사용하지 않으려면 샘플의 t0425와 같이 정의해주고 여기에 추가해 주면 된다.
            //if( sTrCode == "t0425" ) return t0425()
            var retDataMngr : DataMngr = DataMngr()

            if( retDataMngr.getResTRLayout(sm, sTrCode) == false ) {
                // null 을 return 하는 방법도 있지만 TR명을 잘못입력해서 엉뚱한 값을 받을 수 있으므로
                // 차라리 Exception 을 발생시키자
                throw Exception( sTrCode + "TR 정보가 없습니다." )
                return null
            }

            return retDataMngr
        }

        //==========================================================================================
        // Request ID 를 Index로 하는 DataMngr Instance Array
        // Handler Class 에서 사용된다.
        // ms_arrDataMngr 은 Request ID를 Index로 하는 DataMngr Instance Array 이다.
        // Request ID는 0~255 이므로 256개를 가진 Array 를 생성한다.
        //  - Array에 DataMngr Instance 등록시점 :
        //          DataMngr의 request() 가 호출되면 서버로 전송한 후에 받은 Request ID를 이용하여 ms_arrDataMngr 에 등록한다.
        //  - Array 삭제시점 :
        //          서버에서 Release 를 받으면 ms_arrDataMngr 에서 삭제한다.
        private var ms_arrDataMngr = Array<DataMngr?>(255, { null })

        //==========================================================================================
        // RequestID로 DataMngr Instance 를 등록한다.
        private fun setDataMngr( nRqID : Int, dm : DataMngr? ) : Unit {
            ms_arrDataMngr[nRqID] = dm
        }

        //==========================================================================================
        // RequestID로 등록된 DataMngr Instance 를 구한다.
        private fun getDataMngr( nRqID : Int, sTrCode : String ) : DataMngr? {
            var dm = ms_arrDataMngr[nRqID]
            return if( dm == null || dm.getTrCode() != sTrCode ) null else dm
        }
    } // Companion Object ==========================================================================

    public fun setInfo( sTrCode : String, sDesc : String, cHeaderType : Char, nSendSec : Int, nSendCount : Int, bAttr : Boolean )
    {
        super.setInfo(sTrCode, sDesc, cHeaderType, bAttr)
        //--------------------------------------------------------------------------------------
        // 시간당 전송건수 제한이 있으므로 전송한 후에 다음 전송까지 필요한 최소시간을 구한다.
        if( nSendCount > 0 ) {
            m_nNextSendTime = nSendSec * 1000 / nSendCount
            m_nNextSendTime += 100  // 정확한 시간을 주면 거부날 가능성이 있어서 100 ms 를 더 추가한다
        }
        else {
            m_nNextSendTime = 0     // 무제한이다.
        }
    }



    //==============================================================================================
    // Block 정보 설정
    override fun setBlockInfo( sName : String, nType : Int, bOccurs : Boolean  ) {
        super.setBlockInfo( sName , nType, bOccurs )
    }

    //==============================================================================================
    // Block의 Occurs Count를 구한다.
    override fun getBlockCount( sBlock : String ) : Int = super.getBlockCount(sBlock)

    //==============================================================================================
    // Field 정보 설정
    override fun setFieldInfo( sBlock : String, sField : String, nFieldType : Int, nFieldLen : Int, nFieldLen2 : Int )
            = super.setFieldInfo( sBlock , sField, nFieldType, nFieldLen, nFieldLen2 )
    //==============================================================================================
    // Field Data를 가져온다.
    override fun readFieldData( sBlock : String, sField : String, nIndex : Int ) : String
            = super.readFieldData(sBlock, sField, nIndex)
    //==============================================================================================
    // Attr 속성이 있는 TR의 경우 Field Data의 Attr을 가져온다.
    override fun readFieldAttrData( sBlock : String, sField : String, nIndex : Int ) : String
            = super.readFieldAttrData(sBlock, sField, nIndex)
    //==============================================================================================
    // Field Data를 입력한다.
    override fun writeFieldData( sBlock : String, sField : String, aData : Any, nIndex : Int  ) : Boolean
            = super.writeFieldData(sBlock, sField, aData, nIndex)
    //==============================================================================================

    //==============================================================================================
    // 데이터 조회 이후 연속조회 여부
    public fun isContinue() : Boolean = m_bCont

    //==============================================================================================
    // 데이터 조회 이후 연속조회가 있는 경우 연속키값. 헤더가 B 타입인 경우만 사용
    public fun getContinueKey() : String = m_sContKey
    //==============================================================================================
    // 서버로 전송
    //      nLastSec -> -1:즉시전송, 0:초당전송시간이 지난 후에 전송, < 0 : nLastSec초가 지난 후에 전송
    public fun request( sm : SocketManager, nHandler : Int, bNext: Boolean = false, strContinueKey: String = "", nLaterSec : Int = -1, nTimeOut: Int = 30 ) : Int {
        //--------------------------------------------------------------------------------------
        // 전송할 데이터를 InBlock에서 구한다.
        var sData = ""

        m_arrInBlocks.forEach{
            sData += it.getData()
        }

        //--------------------------------------------------------------------------------------
        // 즉시 전송
        if( nLaterSec < 0 || m_nNextSendTime <= 0 )
        {
            return request( sm, this, nHandler, getTrCode(), sData, bNext, strContinueKey, nTimeOut )
        }

        //--------------------------------------------------------------------------------------
        // 지연 전송
        var dm = this
        var nTime = if( nLaterSec > 0 ) nLaterSec * 1000 else m_nNextSendTime

        // Timer 로 특정 시간 이후에 요청을 보내도록 한다.
        //      이 타이머는 Worker Thread에서 실행된다.
        //      request를 Worker Thread에서 실행시켜도 될까?
        //      Main Thread에서 실행되는 Timer 는 무엇일까?
        timer( initialDelay = nTime.toLong(), period = 60 * 1000L ) {
            this.cancel()
            request( sm, dm, nHandler, getTrCode(), sData, bNext, strContinueKey, nTimeOut )
        }

        return 0
    }

    //==============================================================================================
    // 서버로 전송
    private fun request(sm : SocketManager, dm : DataMngr, nHandler : Int, sTrCode : String, sData : String, bNext: Boolean, strContinueKey: String, nTimeOut: Int ) : Int {
        var nRqID = sm.requestData( nHandler, sTrCode, sData, bNext, strContinueKey, nTimeOut )
        // 전송에 성공했으면 Instance를 Array 에 저장하여 Event를 받을 수 있게 한다.
        if( nRqID >= 0 ) {
            setDataMngr( nRqID, dm )
        }
        return nRqID
    }

    //==============================================================================================
    // 서버로부터 데이터를 받을 이벤트를 등록한다.
    public fun setOnRecvListener( l : OnRecvListener? ) {
        m_OnRecvListener = l
    }

    //==============================================================================================
    // 서버로부터 데이터를 받을 이벤트 interface
    public interface OnRecvListener {
        fun onData      (dm : DataMngr, sBlockName : String )                                     : Unit {}   // Receive Data
        fun onMsg       (dm : DataMngr, sCode : String, sMsg : String, bCriticalError : Boolean ) : Unit {}   // Receive Message
        fun onComplete  ( dm : DataMngr)                                                          : Unit {}   // Receive Data finish
        // 빈 함수를 만들지 않으면 OnRecvListner() 를 만들때 사용하지 않아도 함수를 만들어야 한다.
    }
}
