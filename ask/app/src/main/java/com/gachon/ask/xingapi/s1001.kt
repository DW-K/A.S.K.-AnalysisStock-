package com.gachon.ask.xingapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import android.os.*
import android.util.Log
import com.ebest.api.*
import com.gachon.ask.R
import com.gachon.ask.datamngr.API_DEFINE
import com.gachon.ask.util.Auth
import com.gachon.ask.util.CloudStorage
import com.gachon.ask.util.Firestore
import com.gachon.ask.util.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.android.gms.tasks.OnSuccessListener




class s1001 : Fragment() {

    internal var handler: ProcMessageHandler? = null

        internal inner class ProcMessageHandler : Handler() {
        /* RECEIVE 결과값 */
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }
                API_DEFINE.RECEIVE_DATA -> {

                    val lpDp = msg.obj as DataPacket
                    if (lpDp.strTRCode.equals("t1301")) {
                        processT1301(lpDp.strBlockName!!, lpDp.pData!!)
                    }
                }
                // TR조회 끝
                API_DEFINE.RECEIVE_RELEASE -> {
                    val lpDp = msg.obj as ReleasePacket

                    lpDp.nRqID
                    lpDp.strTrCode
                }
                API_DEFINE.RECEIVE_REALDATA -> {
                    val lpRp = msg.obj as RealPacket
                    if (lpRp.strBCCode.equals("S3_") or lpRp.strBCCode.equals("K3_")) {

                        processSK3_(lpRp.strBCCode, lpRp.strKeyCode, lpRp.pData)
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

        // 조회
        root.findViewById<Button>(R.id.button).setOnClickListener {
            OnButtonQueryClicked()
        }

        // 연속조회
        m_buttonNext = root.findViewById<Button>(R.id.button2);
        m_buttonNext.setOnClickListener {
            OnButtonNextQueryClicked()
        }

        m_nHandle = manager.setHandler(mainView, handler as Handler)

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
        var bOK = manager.deleteRealData(m_nHandle, "S3_", m_strJongmokCode, 6)
        bOK = manager.deleteRealData(m_nHandle, "K3_", m_strJongmokCode, 6)

        /* 해당 화면을 사용하지 않을떄 핸들값 삭제 */
        manager.deleteHandler(m_nHandle)
    }

    fun OnButtonQueryClicked() {

        m_adapter.items.clear()
        m_adapter.notifyDataSetChanged()

        val temp = root.findViewById<EditText>(R.id.editText2).text.toString()
        if (temp.length < 6) {
            Toast.makeText(
                activity?.applicationContext,
                "종목코드를 확인해 주십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        // 현재 수신받고 있는 종목의 실시간정보를 삭제한다.
        var bOK = manager.deleteRealData(m_nHandle, "S3_", m_strJongmokCode, 6)
        bOK = manager.deleteRealData(m_nHandle, "K3_", m_strJongmokCode, 6)

        m_strJongmokCode = temp

        var inblock :ArrayList<String> = arrayListOf()
        TRCODE.makeInblock(inblock,0,m_strJongmokCode);
        TRCODE.makeInblock(inblock,1,"",12);
        TRCODE.makeInblock(inblock,2,"",4);
        TRCODE.makeInblock(inblock,3,"",4);
        TRCODE.makeInblock(inblock,4,"",10);
        val ref = TRCODE.makeInblock(inblock);

        var dd  =ref.length;
        var nRqID = manager.requestData(m_nHandle, "t1301", ref, false, "", 30)


        m_bNextQuery = false
        m_buttonNext.setEnabled(false)
    }

    fun OnButtonNextQueryClicked(){

        //m_adapter.items.clear()
        m_adapter.notifyDataSetChanged()

        // 현재 수신받고 있는 종목의 실시간정보를 삭제한다.
        var bOK = manager.deleteRealData(m_nHandle, "S3_", m_strJongmokCode, 6)
        bOK = manager.deleteRealData(m_nHandle, "K3_", m_strJongmokCode, 6)

        var inblock :ArrayList<String> = arrayListOf()
        TRCODE.makeInblock(inblock,0,m_strJongmokCode);
        TRCODE.makeInblock(inblock,1,"",12);
        TRCODE.makeInblock(inblock,2,"",4);
        TRCODE.makeInblock(inblock,3,"",4);
        TRCODE.makeInblock(inblock,4,m_strNextKey,10);
        val ref = TRCODE.makeInblock(inblock);
        var nRqID = manager.requestData(m_nHandle, "t1301", ref, true, m_strNextKey, 30)
        m_bNextQuery = true
    }

    private fun processT1301(strBlockName: String, pData: ByteArray) {

        if (strBlockName == "t1301OutBlock" == true) {

            m_strNextKey = String(pData)
            m_strNextKey = m_strNextKey.trim()
            if (m_strNextKey.length > 0)
                m_buttonNext.setEnabled(true)

        } else if (strBlockName == "t1301OutBlock1" == true) {
            processT1301OutBlock1(pData)
            if (m_bNextQuery == false) {
                var bOK = manager.addRealData(m_nHandle, "S3_", m_strJongmokCode, 6)
                bOK = manager.addRealData(m_nHandle, "K3_", m_strJongmokCode, 6)
            }
        }

    };


    private fun processT1301OutBlock1(pData: ByteArray) {


        // 한개의 column을 읽어온다. 만약 OutBlock데이타가 occurs인 경우는 0번째 데이타를 읽어온다.
        var column = manager!!.getOutBlockColumnDataFromByte("t1301", "t1301OutBlock1", "price1", pData!!)



        //방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        var map = manager!!.getOutBlockDataFromByte("t1301", "t1301OutBlock1", pData!!)
        var pArray  = manager.getAttributeFromByte("t1301", "t1301OutBlock1", pData) // attribute

        /*
        // 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우
        val bAttributeInData = true
        var map = manager.getDataFromByte(pData, TRCODE.n1301col, bAttributeInData)
        var pArray = manager.getAttributeFromByte(pData, TRCODE.n1301col) // attribute
        */


        if (map != null) {

            for (i in 0..map.size - 1) {

                /* outblock 데이터에서 필요한 데이터 확인 */
                /*
               실데이터는 getOutBlockDataFromByte 에서 불러온 정보를 이용
                대비구분을 위한 데이터 getAttributeFromByte 에서 불러온 정보를 이용
                map의 index번호는 (TR 구조체의 인덱스넘버를 직접입력 또는 TRCODE.kt에서 선언된 enum class의 ordinal 값 사용 )

                getTimeFormat : 시간 데이터 출력 포맷 변환
                getCommaValue : 자릿수 구분 콤마값 포맷 변환

                */
                val data_record: List<Triple<TableGrid.TYPE, Any, Int>> = listOf(

                    /*
                    String 타입 데이터 체결시간, 실데이터는 getOutBlockDataFromByte 에서 불러온 정보를 이용 (쿼리에서 0번 또는 TRCODE.kt에서 선언된 T1301.CHETIME.ordinal 위치)
                    */
                    Triple(
                        TableGrid.TYPE.STRING,
                        manager.getTimeFormat(map[i]?.get(TRCODE.T1301.CHETIME.ordinal)!!),
                        R.id.view1
                    ),

                    Triple(
                        TableGrid.TYPE.STRING,
                        manager.getCommaValue(map[i]?.get(TRCODE.T1301.PRICE.ordinal)!!),
                        R.id.view2
                    ),

                    /*
                    DAEBI 타입 데이터 상한가 하한가 정보, getAttributeFromByte 에서 불러온 정보를 이용 (쿼리에서 1번 또는 TRCODE.T1301.PRICE.ordinal 위치)
                    */
                    Triple(
                        TableGrid.TYPE.DAEBI,
                        pArray!![i][TRCODE.T1301.PRICE.ordinal],
                        R.id.view2
                    ),
                    /*
                    ICON 타입 데이터 상한 하한 구분 아이콘, 실데이터는 getOutBlockDataFromByte 에서 불러온 정보를 이용 (쿼리에서 2번 또는 TRCODE.T1301.SIGN.ordinal 위치)
                    */
                    Triple(
                        TableGrid.TYPE.ICON,
                        map[i]?.get(TRCODE.T1301.SIGN.ordinal)!!,
                        R.id.view3_1
                    ),

                    Triple(
                        TableGrid.TYPE.STRING,
                        manager.getCommaValue(map[i]?.get(TRCODE.T1301.CHANGE.ordinal)!!),
                        R.id.view3_2
                    ),
                    Triple(
                        TableGrid.TYPE.DAEBI,
                        pArray[i][TRCODE.T1301.CHANGE.ordinal],
                        R.id.view3_2
                    ),

                    Triple(
                        TableGrid.TYPE.DOUBLE,
                        map[i]?.get(TRCODE.T1301.CVOLUME.ordinal)!!,
                        R.id.view4
                    ),
                    Triple(
                        TableGrid.TYPE.DAEBI,
                        pArray[i][TRCODE.T1301.CVOLUME.ordinal],
                        R.id.view4
                    ),

                    Triple(
                        TableGrid.TYPE.STRING,
                        manager.getCommaValue(map[i]?.get(TRCODE.T1301.VOLUME.ordinal)!!),
                        R.id.view5
                    )
                )

                m_adapter.addItem(data_record)

                //m_adapter.addItem(DataItem(map[i][0]!!, map[i][1]!!, pArray!![i][1], map[i][2]!!, map[i][3]!!, pArray!![i][3], map[i][4]!!, pArray!![i][4], map[i][5]!!, pArray!![i][5]))    //아이템을 추가한다.
            }

            /* JSON 전환시 사용 */
            val json = manager.getJSONValue("t1301", pData)
            val json2 = manager.getJSONValue("t1301", "t1301OutBlock1", pData)
            m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.

        }
    }

    private fun processSK3_(strTrCode: String?, strKeyCode: String?, pData: ByteArray?) {

        //방법 1. res 폴더에 TR정보가 담긴 *.res 파일이 있는 경우
        var strArray = manager!!.getOutBlockDataFromByte(strTrCode!!, "OutBlock", pData!!)
        var pArray = manager.getAttributeFromByte(strTrCode, "OutBlock", pData) // attribute


        /*
        // 방법2. 프로젝트의 TR정보가 담긴 소스(.kt, .java ... *현재 프로젝트의 TRCODE.kt )를 사용하는 경우
        val bAttributeInData = true
        var strArray = manager.getDataFromByte(pData!!, TRCODE.nS_K_3col, bAttributeInData)
        var pArray = manager.getAttributeFromByte(pData!!, TRCODE.nS_K_3col) // attribute
        */

        /* 데이터 처리 */
        val data_record: List<Triple<TableGrid.TYPE, Any, Int>> = listOf(
            Triple(
                TableGrid.TYPE.STRING,
                manager.getTimeFormat(strArray?.get(0)?.get(TRCODE.S_K_3_.CHETIME.ordinal)!!),
                R.id.view1
            ),
            Triple(
                TableGrid.TYPE.STRING,
                manager.getCommaValue(strArray[0]?.get(TRCODE.S_K_3_.PRICE.ordinal)!!),
                R.id.view2
            ),
            Triple(TableGrid.TYPE.DAEBI, pArray!![0][TRCODE.S_K_3_.PRICE.ordinal], R.id.view2),

            Triple(
                TableGrid.TYPE.ICON,
                strArray[0]?.get(TRCODE.S_K_3_.SIGN.ordinal)!!,
                R.id.view3_1
            ),

            Triple(
                TableGrid.TYPE.STRING,
                manager.getCommaValue(strArray[0]?.get(TRCODE.S_K_3_.CHANGE.ordinal)!!),
                R.id.view3_2
            ),
            Triple(TableGrid.TYPE.DAEBI, pArray!![0][TRCODE.S_K_3_.CHANGE.ordinal], R.id.view3_2),
            Triple(
                TableGrid.TYPE.DOUBLE, java.lang.Double.toString(
                    java.lang.Double.parseDouble(
                        strArray[0]?.get(TRCODE.S_K_3_.CVOLUME.ordinal)?.trim().toString()
                    )
                ), R.id.view4
            ),
            Triple(TableGrid.TYPE.DAEBI, pArray!![0][TRCODE.S_K_3_.CVOLUME.ordinal], R.id.view4),
            Triple(
                TableGrid.TYPE.STRING,
                manager.getCommaValue(strArray[0]?.get(TRCODE.S_K_3_.VOLUME.ordinal)!!),
                R.id.view5
            ),
            Triple(TableGrid.TYPE.DAEBI, pArray[0][TRCODE.S_K_3_.VOLUME.ordinal], R.id.view5)
        )
        m_adapter.addItem(0, data_record)
        m_adapter.notifyDataSetChanged()   //데이터 갱신을 알린다.
    }
}

