package com.gachon.ask.xingapi

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.gachon.ask.datamngr.API_DEFINE
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import com.gachon.ask.R

// TR 테스트 관련 화면

class s1009 : Fragment() {

    internal var m_nHandle = -1
    internal var handler: ProcMessageHandler? = null
    lateinit internal var manager: SocketManager
    internal inner class ProcMessageHandler : Handler() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {

                // 퍼미션 에러
                API_DEFINE.RECEIVE_PERMISSIONERROR -> {
                    activity?.finishAffinity();               // 해당앱의 루트 액티비티를 종료시킨다.
                    System.runFinalization();               // 현재 작업중인 쓰레드가 종료되면 종료 시키라는 명령어
                    System.exit(0);                 // 현재 액티비티를 종료시킨다.
                }

                // INITECH 핸드세이킹 에러
                API_DEFINE.RECEIVE_INITECHERROR -> {
                    val strMsg = msg.obj as String
                    //Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }


                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    val strMsg = msg.obj as String
                    //Toast.makeText(activity?.applicationContext, strMsg, Toast.LENGTH_SHORT).show()
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

                    var code = root!!.findViewById<TextView>(R.id.txt_currentTR);
                    code.setText(lpDp.strTRCode.toString());

                    var current =        root!!.findViewById<TextView>(R.id.txt_currentstate);
                    current.setText(manager!!.getTRRequestCount(lpDp.strTRCode.toString()).toString());


                    val dateAndtime: LocalDateTime = LocalDateTime.now()
                    Log.i("Receive Query :", "$dateAndtime")

                    if (lpDp.strTRCode == "") {

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
                    //val lpMp = msg.obj as MsgPacket
                    //Toast.makeText(
                    //    activity?.applicationContext,
                    //    lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData,
                   //     Toast.LENGTH_SHORT
                   // ).show()
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
    val ti = Timer()

    val timerMap : MutableMap<String,TimerTask> = mutableMapOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.activity_s1009, container, false)
        mainView = (activity as MainView)
        // get SocketManager instance
        handler = ProcMessageHandler()
        manager = (activity?.application as ApplicationManager).getSockInstance()


        val switch1 = root.findViewById<Switch>(R.id.switch1)
        switch1.setOnClickListener{

            val bCheck = (it as Switch).isChecked;

            val editcnt = root.findViewById<EditText>(R.id.edit_count)
            editcnt.setText("");
            if(bCheck){
                editcnt.setHint("초당전송횟수(기본 1회)")
            }
            else{
                editcnt.setHint("1회 전송 간격(기본 1초)")
            }
        }


        val btn1101 = root.findViewById<Button>(R.id.btn_t1101)
        btn1101.setOnClickListener {

            val strTrCode = "t1101";
            val inblock : ArrayList<String> = arrayListOf();
            TRCODE.makeInblock(inblock,0,"005930");
            val ref = TRCODE.makeInblock(inblock);
            onRequest(strTrCode,ref);

        }
        val btn8407 = root.findViewById<Button>(R.id.btn_t8407)
        btn8407.setOnClickListener {

            val strTrCode = "t8407";
            val inblock : ArrayList<String> = arrayListOf();
            TRCODE.makeInblock(inblock,0,"020");
            TRCODE.makeInblock(inblock,1,"005930");
            val ref = inblock.joinToString(" ");

            onRequest(strTrCode,ref);

        }
        val btn1110 = root.findViewById<Button>(R.id.btn_t1110)
        btn1110.setOnClickListener {

            val strTrCode = "t1110";
            val inblock : ArrayList<String> = arrayListOf();
            TRCODE.makeInblock(inblock,0,"005930");
            val ref = inblock.joinToString(" ");
            onRequest(strTrCode,ref);

        }
        val btn3111 = root.findViewById<Button>(R.id.btn_t3111)
        btn3111.setOnClickListener {

        }

        val btn1302 = root.findViewById<Button>(R.id.btn_t1302)
        btn1302.setOnClickListener {
            val strTrCode = "t1302";
            val inblock : ArrayList<String> = arrayListOf();
            TRCODE.makeInblock(inblock,0,"005930");
            TRCODE.makeInblock(inblock,1,"0");
            TRCODE.makeInblock(inblock,2,"200212");
            TRCODE.makeInblock(inblock,3,"100");
            val ref = inblock.joinToString(" ");
            onRequest(strTrCode,ref);
        }


        val btn1305 = root.findViewById<Button>(R.id.btn_t1305)
        btn1305.setOnClickListener{
            val strTrCode = "t1305";
            val inblock : ArrayList<String> = arrayListOf();
            TRCODE.makeInblock(inblock,0,"005930");
            TRCODE.makeInblock(inblock,1,"1");
            TRCODE.makeInblock(inblock,2,"",8);
            TRCODE.makeInblock(inblock,3,"1");
            TRCODE.makeInblock(inblock,4,"100");
            val ref = inblock.joinToString(" ");
            onRequest(strTrCode,ref);


        }

        val btnConnectPC = root.findViewById<Button>(R.id.btn_connectPC);
        btnConnectPC.setOnClickListener {
            val ip =
                makeZeroString(root.findViewById<EditText>(R.id.et_ip).text.toString(),3)+"."+
                makeZeroString(root.findViewById<EditText>(R.id.et_ip2).text.toString(),3)+"."+
                makeZeroString(root.findViewById<EditText>(R.id.et_ip3).text.toString(),3)+"."+
                makeZeroString(root.findViewById<EditText>(R.id.et_ip4).text.toString(),3)

//                String.format("%03d",root.findViewById<EditText>(R.id.et_ip).text?.toString()?.toInt())+"."+
//                        String.format("%03d",root.findViewById<EditText>(R.id.et_ip2).text?.toString()?.toInt())+"."+
//                        String.format("%03d",root.findViewById<EditText>(R.id.et_ip3).text?.toString()?.toInt())+"."+
//                        String.format("%03d",root.findViewById<EditText>(R.id.et_ip4).text?.toString()?.toInt());

            val port =
                makeZeroString(root.findViewById<EditText>(R.id.et_port).text.toString(),5)
                //String.format("%05d", root.findViewById<EditText>(R.id.et_port).text?.toString()?.toInt())

            manager.setLogSendToPC(ip,port);
            if(JniCall.isConnectPC){
                root.findViewById<TextView>(R.id.txt_isConnectPC).setText("연결중");
            }
            else{
                root.findViewById<TextView>(R.id.txt_isConnectPC).setText("연결되지 않음");
            }

        }



        return root
    }

    fun makeZeroString( src : String , len : Int) : String{
        if( (src.length <= 0) or (src == "")){
          return  String.format("%0"+"$len"+"d",0);
        }
        else{
          return  String.format("%0"+"$len"+"d",src.toInt());
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onRequest(strTrCode : String , ref : String){

        if(manager.isConnect() == false){
            Toast.makeText(
                root.context,
                "로그인후 이용해주세요",
                 Toast.LENGTH_SHORT
             ).show()
            return
        };

        // 전송횟수 타이머 설정/해제
        if ( timerMap[strTrCode] != null){
            timerMap[strTrCode]!!.cancel();
            timerMap.remove(strTrCode);
            return;
        }

        val tt = object : TimerTask() {
            override fun run() {

                val dateAndtime: LocalDateTime = LocalDateTime.now()
                Log.i("Send Query :", "$strTrCode $dateAndtime")

                manager.requestData(m_nHandle, strTrCode, ref, false, "", 200)
            }
        }
        timerMap.set(strTrCode,tt);

        val switch1 = root.findViewById<Switch>(R.id.switch1)

        val editcnt = root.findViewById<EditText>(R.id.edit_count).text.toString()
        var cnt : Long = 1000 / 1;

        if( switch1.isChecked) {
            if (editcnt.length > 0) {
                cnt = (1000.0f / editcnt.toFloat()).toLong();
            }
        }
        else{
            if (editcnt.length > 0) {
                cnt = (1000.0f * editcnt.toFloat()).toLong();
            }
        }
        ti.schedule(tt,0,cnt);
    }

    override fun onResume() {
        super.onResume()
        m_nHandle = manager.setHandler(mainView, handler as Handler)

        if(JniCall.isConnectPC){
            root.findViewById<TextView>(R.id.txt_isConnectPC).setText("연결중");
        }
        else{
            root.findViewById<TextView>(R.id.txt_isConnectPC).setText("연결되지 않음");
        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.deleteHandler(m_nHandle)

        timerMap.forEach{
            it.value.cancel();
        }
    }

}