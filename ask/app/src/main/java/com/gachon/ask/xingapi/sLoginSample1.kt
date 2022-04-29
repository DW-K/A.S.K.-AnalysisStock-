package com.gachon.ask.xingapi

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ebest.api.ConnectionService
import com.ebest.api.LinkData
import com.ebest.api.MsgPacket
import com.ebest.api.error.ExceptionHandler
import com.gachon.ask.datamngr.API_DEFINE
import com.gachon.ask.R

class sLoginSample1: AppCompatActivity()  {


    //lateinit var imm: InputMethodManager
    lateinit var m_editTextID: EditText
    lateinit var m_editTextPWD: EditText
    lateinit var m_checkBox: CheckBox
    internal var m_id = ""
    internal var m_password = ""
    lateinit var m_buttonLogin: Button
    lateinit var m_imageButtonClose: ImageView

    internal var handler: ProcMessageHandler? = null
    lateinit internal var manager: ConnectionService
    internal var m_nHandle = -1
    //private var m_customAnimationDialog: CustomAnimationDialog? = null

    internal inner class ProcMessageHandler(private val activity: sLoginSample1) : Handler() {

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

                    setResult(RESULT_OK, null)
                    activity.finish()
                }

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

        setContentView(R.layout.activity_sample_login1)

        // 상태바 텍스트 설정 ()
        // supportActionBar?.setDisplayShowTitleEnabled(false)
        // supportActionBar?.title = "Login"

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // get SocketManager instance
        handler = ProcMessageHandler(this)
        manager = LinkData.getCS()
        //imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        // 이미지 돌아가기 버튼 클릭시
        m_imageButtonClose = this.findViewById(R.id.imageButtonClose)
        if (m_imageButtonClose != null) {
            //m_imageButtonClose.setImageDrawable(ResourceManager.getSingleImage("comm_pre_icon_09")) // eBest Mine 이미지
            m_imageButtonClose.setOnClickListener(View.OnClickListener {
                OnButtonCloseClicked()
            })
        }

        m_editTextID = this.findViewById(R.id.editTextID) as EditText
        m_editTextPWD = this. findViewById(R.id.editTextPWD) as EditText
        m_buttonLogin =  this.findViewById(R.id.buttonLogin) as Button
        m_checkBox =  this.findViewById(R.id.checkBox) as CheckBox
        m_checkBox.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //setConfig(false)
            }
        })

        /*
        m_editTextID.setOnClickListener(View.OnClickListener {
            showKeyboard()
        })
        */

        //m_customAnimationDialog = CustomAnimationDialog(this)

        if (manager.getAutoLogin() == true) {
            var strIDSave = manager.getDataString("idsave")
            if (strIDSave == "1") {
                m_checkBox.setChecked(true)
                var id = manager.getDataString("id")
                m_editTextID.setText(id)
            }
            manager.setDataString("login_type", "1")
        }
    }

    override fun onResume() {
        super.onResume()
        //m_nHandle = manager.setHandler(this, handler as Handler)
        m_nHandle = manager.setHandler(handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.deleteHandler(m_nHandle)
    }

    fun OnButtonLoginClicked(v: View) {

        m_id = m_editTextID.text.toString()
        m_password = m_editTextPWD.text.toString()

        if (m_id.length == 0) {
            Toast.makeText(this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (m_password.length == 0) {
            Toast.makeText(this, "접속비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        m_buttonLogin.setEnabled(false)
        if (manager.isConnect() == true) {
            manager.disconnect()
        }

        var nServerGubun = 2
        var iseBestApp = manager.iseBestApp()
        if (iseBestApp == true)
            nServerGubun = 1


        var nReturn = manager.connect(m_nHandle, nServerGubun)
        if (nReturn == 0) {
            //m_customAnimationDialog!!.show()
            //m_customAnimationDialog!!.setTextMsg("로그인 중입니다. 잠시만 기다리세요.")
            nReturn = manager.loginID(m_nHandle, m_id, m_password)
        } else {
            m_buttonLogin.setEnabled(true)
        }

        if (manager.getAutoLogin() == true) {
            setConfig(true)
        }
    }

    fun setConfig(bID:Boolean) {
        var bChecked = m_checkBox.isChecked()
        if (bChecked == true) {
            if (bID == true)
                manager.setDataString("id", m_id)
            manager.setDataString("idsave", "1")
        } else {
            manager.setDataString("idsave", "0")
        }
    }

    /*
    fun hideKeyboard(){
        var inputManager =  getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    fun showKeyboard(){
        imm.showSoftInput(m_editTextID, 0);
    }
    */

    fun OnButtonCloseClicked() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun popupNotAble(strMsg:String) {

        var DialogBuilder = AlertDialog.Builder(this)

        // 제목셋팅
        DialogBuilder.setTitle("ID로그인")

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

    fun closeActivity(nReturn:Int) {
        setResult(nReturn)
        finish()
    }


}