package com.gachon.ask.xingapi

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.gachon.ask.datamngr.API_DEFINE
import com.gachon.ask.R

class s1008 : Fragment() {

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
                    //if (lpDp.strTRCode == "t1209") {
                    if (lpDp.strTRCode == "t8407") {
                        processT8407(lpDp.strBlockName!!, lpDp.pData!!)
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
    internal var m_adapter = TableGrid().DataAdapter(R.layout.s1008_item01)
    lateinit var m_gridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.activity_s1008, container, false)
        mainView = (activity as MainView)
        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()
        m_gridView = root.findViewById<View>(R.id.grid_view) as GridView
        m_gridView.adapter = m_adapter


        root.findViewById<Button>(R.id.btn_last).setOnClickListener{
            request()
        }


        return root
    }
    override fun onResume() {
        super.onResume()
        m_nHandle = manager.setHandler(mainView, handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.deleteHandler(m_nHandle)
    }

    private fun request(){
        m_adapter.items.clear()
        m_adapter.notifyDataSetChanged()


        /* t8407
            nrec 건수 입력 최대 50
            shcode 6자리 코드로 50개까지 입력 가능
            inblock 데이터 필드값 사이 빈칸(" ") 필요
        */
        val inblock = "001" + " " + "078020"
        /* TR 요청 */
        val nRqID = manager.requestData(m_nHandle, "t8407", inblock, false, "", 30)
    }

    private fun processT8407(strBlockName: String, pData: ByteArray) {
        if (strBlockName == "t8407OutBlock1") {
            processT8407OutBlock1(pData)
        }
    }
    private fun processT8407OutBlock1(pData: ByteArray){
        val map = manager.getOutBlockDataFromByte("t8407", "t8407OutBlock1", pData)

        // 데이터 처리부
        if (map != null) {
            for (i in map.indices) {
                val data_record: List<Triple<TableGrid.TYPE, Any, Int>> = listOf(
                    Triple(
                        TableGrid.TYPE.STRING,
                        map[i]?.get(TRCODE.T8407.CHCODE.ordinal)!!,
                        R.id.view1
                    ),
                    Triple(
                        TableGrid.TYPE.STRING,
                        map[i]?.get(TRCODE.T8407.HNAME.ordinal)?.trim()!!,
                        R.id.view2
                    ),
                    Triple(
                        TableGrid.TYPE.INT,
                        map[i]?.get(TRCODE.T8407.PRICE.ordinal)!!,
                        R.id.view3_1
                    ),
                    Triple(
                        TableGrid.TYPE.DOUBLE,
                        map[i]?.get(TRCODE.T8407.DIFF.ordinal)!!,
                        R.id.view4
                    )
                )

                m_adapter.addItem(data_record)
            }
        }

        m_adapter.notifyDataSetChanged()
    }
}