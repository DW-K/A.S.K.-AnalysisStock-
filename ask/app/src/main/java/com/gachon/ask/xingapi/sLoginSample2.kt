package com.gachon.ask.xingapi

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ebest.api.ConnectionService
import com.ebest.api.LinkData
import com.ebest.api.MsgPacket
import com.ebest.api.dialog.importSignDialog
import com.ebest.api.dialog.Listener
import com.ebest.api.error.ExceptionHandler
import com.ebest.api.login.importSignActivity
import com.ebest.api.rm.ResourceManager
import com.gachon.ask.datamngr.API_DEFINE
import java.util.ArrayList
import com.gachon.ask.R

class sLoginSample2 : AppCompatActivity(), Listener {

    lateinit var m_textViewDN: TextView
    lateinit var m_editTextPWD: EditText
    internal var m_dn = ""
    internal var m_password = ""
    lateinit var m_combobox: Spinner
    lateinit var m_buttonLogin: Button
    lateinit var m_buttonCert: Button
    lateinit var m_textViewNot: TextView
    lateinit var m_imageButtonClose: ImageView

    internal var handler: ProcMessageHandler? = null
    lateinit internal var manager: ConnectionService
    internal var m_nHandle = -1

    var m_popup :importSignDialog? = null
    //private var m_customAnimationDialog: CustomAnimationDialog? = null
   // private lateinit var m_SM: SignManager

    internal inner class ProcMessageHandler(private val activity: sLoginSample2) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {

                // TIMEOUT 에러
                API_DEFINE.RECEIVE_TIMEOUTERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val strMsg = msg.obj as String
                    popupNotAble(strMsg)
                }

                // INITECH 핸드세이킹 에러
                API_DEFINE.RECEIVE_INITECHERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val strMsg = msg.obj as String
                    Toast.makeText(applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }


                // 일반적인 에러
                API_DEFINE.RECEIVE_ERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val strMsg = msg.obj as String
                    Toast.makeText(applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // SOCEKT이 연결이 끊어졌다.
                API_DEFINE.RECEIVE_DISCONNECT -> {
                    //val strMsg = msg.obj as String
                    //Toast.makeText(applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                    m_buttonLogin.setEnabled(true)
                }

                // 서버에서 보내는 시스템 ERROR
                API_DEFINE.RECEIVE_SYSTEMERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val pMsg = msg.obj as MsgPacket
                    Toast.makeText(applicationContext, pMsg.strMessageData, Toast.LENGTH_SHORT).show()
                }

                // SOCKET연결이 실패했다.
                API_DEFINE.RECEIVE_CONNECTERROR -> {
                    m_buttonLogin.setEnabled(true)
                }

                // SOCKET연결이 성공했다.
                API_DEFINE.RECEIVE_CONNECT -> {

                }

                // TR메세지
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(applicationContext, lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData, Toast.LENGTH_SHORT).show()
                }

                // LOGIN이 완료됐다.
                API_DEFINE.RECEIVE_LOGINCOMPLETE -> {
                    //m_customAnimationDialog!!.dismiss()
                    //Toast.makeText(applicationContext, msg.obj.toString(), Toast.LENGTH_SHORT).show()

                    //activity.sendActivity(msg)

                    setResult(RESULT_OK)
                    activity.finish()
                }

                // 선택한 공인인증서 정보
                /*
                RECEIVE_SIGN -> {
                    val lpSign = msg.obj as SignPacket
                    if (lpSign.strSubjectName == "") return
                    m_textViewDN.setText(lpSign.strSubjectName)
                    //m_strPolicy = lpSign.strPolicy
                    //m_strIssuerCn = lpSign.strIssuerCn
                    //m_strExpiredTime = lpSign.strExpiredTime
                    //m_strSerialNumberInt = lpSign.strSerialNumberInt
                    manager.setDataString("dn", lpSign.strSubjectName)
                }
                */


                else -> {
                }
            }
        }
    }

    fun sendActivity(msg: Message) {

        if(LinkData.m_loginPopupRequestHandler == null)
            return

        val msgSend = LinkData.m_loginPopupRequestHandler!!.obtainMessage()
        msgSend.what = msg.what
        msgSend.obj = msg.obj
        LinkData.m_loginPopupRequestHandler!!.sendMessage(msgSend)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        // 타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 상태바 없애기
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_sample_login2)

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // get SocketManager instance
        handler = ProcMessageHandler(this)
        manager = LinkData.getCS()

//        m_SM = SignManager.getInstance()
//        m_SM.initManager(this)


        // 이미지 돌아가기 버튼 클릭시
        m_imageButtonClose = findViewById(R.id.imageButtonClose)
        if (m_imageButtonClose != null) {
            //m_imageButtonClose.setImageDrawable(ResourceManager.getSingleImage("comm_pre_icon_09")) // eBest Mine 이미지
            m_imageButtonClose.setOnClickListener(View.OnClickListener {
                OnButtonCloseClicked()
            })
        }

        m_editTextPWD = findViewById(R.id.editTextPWD) as EditText
        //m_editTextPWD.setText("")
        m_combobox = findViewById(R.id.combobox1) as Spinner
        m_buttonLogin = findViewById(R.id.buttonLogin) as Button
        m_buttonCert = findViewById(R.id.buttonCert) as Button
        m_textViewNot = findViewById(R.id.textViewNot) as TextView

        var items = checkSign()
        //val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        val myAdapter = ArrayAdapter(this, R.layout.spinneritem, items)

        m_combobox.adapter = myAdapter

        m_combobox.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                var t = (parent.getChildAt(0) as TextView)
                //t.setTextColor(Color.BLUE)
                //(parent.getChildAt(0) as TextView).textSize = 10f
                t.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResourceManager.calcFontSize(t.textSize.toInt()))

                m_dn = m_combobox.getItemAtPosition(position) as String

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

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        setControl()

        //m_customAnimationDialog = CustomAnimationDialog(this);

        if (manager.getAutoLogin() == true) {
            manager.setDataString("login_type", "2")
            var strDN = manager.getDataString("dn")
            if (strDN.length > 0) {
                comboSelect(strDN)
            }
        }
        //val intent = Intent(this, importSignActivity::class.java)
        //startActivityForResult(intent, 1)

    }

    override fun onResume() {
        super.onResume()
//        m_nHandle = manager.setHandler(this, handler as Handler)
        m_nHandle = manager.setHandler(handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (manager.getAutoLogin() == true) {
            manager.setDataString("dn", m_dn)
        }
        manager.deleteHandler(m_nHandle)
    }

    // 공인인증서 로그인
    fun OnButtonLoginClicked(v: View) {

        m_password = m_editTextPWD.text.toString()
        //m_dn = m_textViewDN.text.toString()

        if (m_password.length == 0) {
            Toast.makeText(this, "공인인증 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (m_dn.length == 0 || m_dn == "공인인증서를 선택하세요.") {
            Toast.makeText(this, "공인인증서를 선택하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        m_buttonLogin.setEnabled(false)
        if (manager.isConnect() == true) {
            manager.disconnect()
        }

        var nReturn = manager.connect(m_nHandle, 1)
        if (nReturn == 0) {
            //m_customAnimationDialog!!.show()
            //m_customAnimationDialog!!.setTextMsg("로그인 중입니다. 잠시만 기다리세요.")
            nReturn = manager.loginSign(m_nHandle, m_password, m_dn)
        } else {
            m_buttonLogin.setEnabled(true)
        }

    }

    // 인증서 가져오기
    fun OnButtonCertClicked(v: View) {
//        val intent = Intent(this, importSignActivity::class.java)
//        startActivityForResult(intent, 1)

        if (m_popup == null) {
            m_popup = importSignDialog(this)
            m_popup!!.setListener(this)
            m_popup!!.setCancelable(false)
            m_popup!!.show()
        }
    }

    // 종료버튼
    fun OnButtonCloseClicked() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    fun popupNotAble(strMsg:String) {

        var DialogBuilder = AlertDialog.Builder(this)

        // 제목셋팅
        DialogBuilder.setTitle("공인인증")

        // AlertDialog 셋팅
        DialogBuilder
            .setMessage(strMsg)
            .setCancelable(false)
            .setPositiveButton("확인"
            ) { dialog, id ->
                // 프로그램을 종료한다
                dialog.cancel()
                closeActivity(Activity.RESULT_CANCELED)
            }
        //.setNegativeButton("취소"
        //) { dialog, id ->
        //    // 다이얼로그를 취소한다
        //    dialog.cancel()
        //    DupConnDialogBuilder = null
        //    disConnect()
        //}

        // 다이얼로그 생성
        try {
            //m_customAnimationDialog!!.dismiss()
            val alertDialog = DialogBuilder.create()

            // 다이얼로그 보여주기
            alertDialog.show()
        } catch (e: Exception) {
            val str = e.toString()
        }

    }

//    fun checkSign(): ArrayList<String> {
//
//        var items = ArrayList<String>()
//        // 공인인증서 목록 시작
//        val nCount = m_SM.signCnt
//        for (i in 0 until nCount) {
//
//            //String strSubjectName = m_SM.getSignStorage(i, 0);          // DN
//            val strSubjectName = m_SM.getSignDN(i)                      // DN
//            val strPolicy = m_SM.getSignStorage(i, 1)               // 법용OID
//            val strIssuerCn = m_SM.getSignStorage(i, 2)             // 발급기관
//            val strExpiredTime = m_SM.getSignStorage(i, 3)          // 만료일
//            val strSerialNumberInt = m_SM.getSignStorage(i, 4)      // serial num
//            val strSubjectDn = m_SM.getSignStorage(i, 5)            //
//            val strPolicyNumString = m_SM.getSignStorage(i, 6)      //
//
//            items.add(strSubjectDn)
//        }
//        // 공인인증서 목록 끝
//
//        return items
//    }

    fun checkSign() : ArrayList<String>{
        var items = ArrayList<String>()

        var temp =  manager.getSignList(this);

        val nCount = temp.size
        for (i in 0 until nCount) {


            val strSubjectName = temp.get(i).strSubjectName;        // DN
            val strPolicy =  temp.get(i).strPolicy;                // 법용OID
            val strIssuerCn = temp.get(i).strIssuerCn;            // 발급기관
            val strExpiredTime = temp.get(i).strExpiredTime;         // 만료일
            val strSerialNumberInt =  temp.get(i).strSerialNumberInt;      // serial num
            val strPolicyNumString = temp.get(i).strPolicyNumString;      //

            items.add(strSubjectName)
        }
        return items
    }


    fun closeActivity(nReturn:Int) {
        setResult(nReturn)
        finish()
    }

    fun setControl() {
        var items = checkSign()

        // HT1 삭제해야한다
        //items.clear()

        if (items.isEmpty() == true) {
            m_buttonLogin.setVisibility(View.INVISIBLE)
            m_buttonCert.setVisibility(View.VISIBLE)
            m_editTextPWD.setVisibility(View.INVISIBLE)
            m_combobox.setVisibility(View.INVISIBLE)
            m_textViewNot.setVisibility(View.VISIBLE)
        } else {
            m_buttonLogin.setVisibility(View.VISIBLE)
            m_buttonCert.setVisibility(View.VISIBLE)
            m_editTextPWD.setVisibility(View.VISIBLE)
            m_combobox.setVisibility(View.VISIBLE)
            m_textViewNot.setVisibility(View.GONE)
        }
    }

    fun comboSelect(strDN:String) {
        var nCount = m_combobox.count
        for (i in 0 until nCount) {
            var obj = (m_combobox.getItemAtPosition(i)) as String
            if (obj.equals(strDN)) {
                m_combobox.setSelection(i)
                break
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            1 -> {

                when(resultCode) {
                    Activity.RESULT_OK -> {
                        setControl()
                        var items = checkSign()
                        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
                        m_combobox.adapter = myAdapter

                    }
                    Activity.RESULT_CANCELED -> {
                        //setResult(RESULT_CANCELED)
                        //finish()
                    }

                }

            }

        }

    }

    override fun triggerEvent(strKey: String, strValue: String) {
        if (strKey == "cancel") {
            m_popup!!.dismiss()
            m_popup = null

        } else if (strKey == "ok") {
            m_popup!!.dismiss()
            m_popup = null
            //setControl()
        }
    }

}