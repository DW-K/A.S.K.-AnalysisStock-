package com.gachon.ask.datamngr

import com.ebest.api.RealPacket
import com.ebest.api.SocketManager
import com.ebest.api.datamngr.DataMngrUtil
import com.ebest.api.datamngr.Field
import com.ebest.api.datamngr.ResRealLayout
import com.gachon.ask.datamngr.API_DEFINE
import java.lang.Exception

//==================================================================================================
// 실시간TR Data Manager
//==================================================================================================
open class RealDataMngr : ResRealLayout() {

    // 실시간 시세를 처리하는 클래스
    // 사용법은 button의 리시버 등록방법과 같다.
    // 정의한 RealDataMngr를 addRealDataMngr에 등록
    // 단, 다른 점은 반드시 removeRealDataMngr를 해줘야 한다.

    public           var m_arrItems = ArrayList<String>()   // 등록된 종목(=Key)

    //==============================================================================================
    // request & receive 정보
    public           var m_OnRecvListener : OnRecvListener? = null  // Receive Event Handler

    companion object {
        //==========================================================================================
        // TR 코드로 DataMngr() 을 구한다.
        fun getInstance(sm: SocketManager, sTrCode: String): RealDataMngr? {
            // RES를 사용하는 경우 TR에 대한 정보를 가져온다
            // RES를 사용하지 않으려면 샘플의 t0425와 같이 정의해주고 여기에 추가해 주면 된다.
            //if( sTrCode == "t0425" ) return t0425()
            var realDataMngr: RealDataMngr = RealDataMngr()

            if( realDataMngr.getResRealLayout(sm, sTrCode) == false ) {
                // null 을 return 하는 방법도 있지만 TR명을 잘못입력해서 엉뚱한 값을 받을 수 있으므로
                // 차라리 Exception 을 발생시키자
                throw Exception( sTrCode + "TR 정보가 없습니다." )
                return null
            }

            return realDataMngr
            // null 을 return 하는 방법도 있지만 TR명을 잘못입력해서 엉뚱한 값을 받을 수 있으므로
            // 차라리 Exception 을 발생시키자
            //throw Exception( sTrCode + "TR 정보가 없습니다." )
        }
    }

    //==============================================================================================
    // Tr 정보를 설정
    override fun setInfo( sTrCode : String, sDesc : String, nItemLen : Int, bAttr : Boolean  )
            = super.setInfo( sTrCode , sDesc , nItemLen , bAttr )

    //==============================================================================================
    // Field 정보 설정
    override fun setFieldData( sField : String, nFieldType : Int, nFieldLen : Int, nFieldLen2 : Int )
            = super.setFieldData( sField , nFieldType , nFieldLen , nFieldLen2 )

    //==============================================================================================
    // Field Data를 가져온다.
    override fun readFieldData( sField : String ) = super.readFieldData( sField.trim() )
    //==============================================================================================

    //==============================================================================================
    // Field Data를 가져온다.
    override fun readFieldAttrData( sField : String ) = super.readFieldAttrData( sField.trim() )
    //==============================================================================================
    // 종목을 등록한다.
    public fun addItems(sm : SocketManager, nHandle : Int, sItems : String ) : Boolean {
        var sSendItems  = ""
        var arrAddItems = ArrayList<String>()

        //------------------------------------------------------------------------------------------
        // 등록하려는 종목중에서 등록되지 않은 종목만 가져온다.
        var nPos = 0
        while( sItems.length >= nPos + getKeyLength() ) {
            // 종목 한개를 가져온다.
            var sItem = sItems.substring( nPos .. nPos + getKeyLength()-1 )

            // 등록되지 않은 종목만 등록한다.
            if( m_arrItems.binarySearch( sItem ) < 0 && arrAddItems.binarySearch( sItem ) < 0 ) {
                sSendItems += sItem
                arrAddItems.add( sItem )
            }

            nPos += getKeyLength()
        }

        //------------------------------------------------------------------------------------------
        // 등록하려는 종목이 없다면 ... 성공이다.
        if( sSendItems.length == 0 ) {
            return true
        }

        //------------------------------------------------------------------------------------------
        // xingAPI에 종목 등록을 통보한다.
        if( sm.addRealData( nHandle, m_sTrCode, sSendItems, getKeyLength() ) == false ) {
            return false
        }

        //------------------------------------------------------------------------------------------
        // 등록된 종목리스트에 추가한다.
        arrAddItems.forEach { m_arrItems.add( it ) }
        return true
    }

    //==============================================================================================
    // 종목을 해제한다.
    public fun removeItems( sm : SocketManager, nHandle : Int, sItems : String ) : Boolean {
        var sSendItems     = ""
        var arrRemoveItems = ArrayList<String>()

        //------------------------------------------------------------------------------------------
        // 해제하려는 종목중에서 등록된 종목만 가져온다.
        var nPos = 0
        while( sItems.length >= nPos + getKeyLength() ) {
            // 종목 한개를 가져온다.
            var sItem = sItems.substring( nPos .. nPos + getKeyLength()-1 )

            // 등록되어 있는 종목인지 확인한다.
            if( m_arrItems.binarySearch( sItem ) >= 0 )
            {
                sSendItems += sItem
                arrRemoveItems.add( sItem )
            }

            nPos += getKeyLength()
        }

        //------------------------------------------------------------------------------------------
        // 등록하려는 종목이 없다면 ... 성공이다.
        if( sSendItems.length == 0 ) {
            return true
        }

        //------------------------------------------------------------------------------------------
        // xingAPI에 종목 삭제를 통보한다.
        if( sm.deleteRealData( nHandle, m_sTrCode, sItems, getKeyLength() ) == false ) {
            return false
        }

        //------------------------------------------------------------------------------------------
        // 등록된 종목리스트에서 삭제한다.
        arrRemoveItems.forEach { m_arrItems.remove( it ) }

        return true
    }

    //==============================================================================================
    // 서버로부터 데이터를 받을 이벤트를 등록한다.
    public fun setOnRecvListener( l : OnRecvListener? ) {
        m_OnRecvListener = l
    }

    //==============================================================================================
    // 서버로부터 데이터를 받을 이벤트 interface
    public interface OnRecvListener {
        fun onData( rdm : RealDataMngr) : Unit {}   // Receive Data
    }
}

class RealHandler() {
    //==========================================================================================
    // RealDataMngr Instance Array
    private var m_arrRealDataMngr = ArrayList<RealDataMngr>()

    //==========================================================================================
    // 서버로부터 TR이 내려왔을때 이 함수를 호출한다.
    // 이 함수에서 해당 TR로 Event를 호출한다.
    // 데이터를 처리했다면 true 를 return 한다.
    public fun procMsgHandler(nCode: Int, obj: Any): Boolean {
        if (nCode != API_DEFINE.RECEIVE_REALDATA) {
            return false
        }

        var rp = obj as RealPacket

        //--------------------------------------------------------------------------------------
        // Data 및 Event 처리
        var arrRDM = getRealDataMngr(rp.strBCCode!!, rp.strKeyCode!!)
        arrRDM.forEach {
            if (rp.pData != null) {
                // Data를 RealDataMngr()에 입력
                it.writeData(rp.pData!!)
                // Event 호출
                if(it.m_OnRecvListener != null) {
                    it.m_OnRecvListener!!.onData( it )
                }
            }
        }

        return true
    }

    //==========================================================================================
    // 데이터 수신을 받기 위한 RealDataMngr을 등록한다. 버튼 클릭처럼 한 곳에서 처리하기 위해..
    public fun addRealDataMngr(rdm: RealDataMngr): Unit {
        m_arrRealDataMngr.add( rdm )
    }

    //==========================================================================================
    // RealDataMngr을 제거한다.
    public fun removeRealDataMngr( rdm: RealDataMngr ): Unit {
        m_arrRealDataMngr.remove( rdm )
    }

    //==========================================================================================
    // RequestID로 등록된 DataMngr Instance 를 구한다.
    private fun getRealDataMngr(sTrCode: String, sItem: String): ArrayList<RealDataMngr> {
        var arrReturn = ArrayList<RealDataMngr>()
        m_arrRealDataMngr.forEach {
            if( it.m_sTrCode == sTrCode ) {
                if( it.m_arrItems.binarySearch( sItem ) >= 0 )
                {
                    arrReturn.add( it )
                }
            }
        }
        return arrReturn
    }
}