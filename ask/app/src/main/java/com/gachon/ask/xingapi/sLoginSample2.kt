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

                // TIMEOUT μλ¬
                API_DEFINE.RECEIVE_TIMEOUTERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val strMsg = msg.obj as String
                    popupNotAble(strMsg)
                }

                // INITECH νΈλμΈμ΄νΉ μλ¬
                API_DEFINE.RECEIVE_INITECHERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val strMsg = msg.obj as String
                    Toast.makeText(applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }


                // μΌλ°μ μΈ μλ¬
                API_DEFINE.RECEIVE_ERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val strMsg = msg.obj as String
                    Toast.makeText(applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                }

                // SOCEKTμ΄ μ°κ²°μ΄ λμ΄μ‘λ€.
                API_DEFINE.RECEIVE_DISCONNECT -> {
                    //val strMsg = msg.obj as String
                    //Toast.makeText(applicationContext, strMsg, Toast.LENGTH_SHORT).show()
                    m_buttonLogin.setEnabled(true)
                }

                // μλ²μμ λ³΄λ΄λ μμ€ν ERROR
                API_DEFINE.RECEIVE_SYSTEMERROR -> {
                    m_buttonLogin.setEnabled(true)
                    //m_customAnimationDialog!!.dismiss()
                    val pMsg = msg.obj as MsgPacket
                    Toast.makeText(applicationContext, pMsg.strMessageData, Toast.LENGTH_SHORT).show()
                }

                // SOCKETμ°κ²°μ΄ μ€ν¨νλ€.
                API_DEFINE.RECEIVE_CONNECTERROR -> {
                    m_buttonLogin.setEnabled(true)
                }

                // SOCKETμ°κ²°μ΄ μ±κ³΅νλ€.
                API_DEFINE.RECEIVE_CONNECT -> {

                }

                // TRλ©μΈμ§
                API_DEFINE.RECEIVE_MSG -> {
                    val lpMp = msg.obj as MsgPacket
                    Toast.makeText(applicationContext, lpMp.strTRCode + " " + lpMp.strMsgCode + lpMp.strMessageData, Toast.LENGTH_SHORT).show()
                }

                // LOGINμ΄ μλ£λλ€.
                API_DEFINE.RECEIVE_LOGINCOMPLETE -> {
                    //m_customAnimationDialog!!.dismiss()
                    //Toast.makeText(applicationContext, msg.obj.toString(), Toast.LENGTH_SHORT).show()

                    //activity.sendActivity(msg)

                    setResult(RESULT_OK)
                    activity.finish()
                }

                // μ νν κ³΅μΈμΈμ¦μ μ λ³΄
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

        // νμ΄νλ° μμ κΈ°
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // μνλ° μμ κΈ°
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_sample_login2)

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // get SocketManager instance
        handler = ProcMessageHandler(this)
        manager = LinkData.getCS()

//        m_SM = SignManager.getInstance()
//        m_SM.initManager(this)


        // μ΄λ―Έμ§ λμκ°κΈ° λ²νΌ ν΄λ¦­μ
        m_imageButtonClose = findViewById(R.id.imageButtonClose)
        if (m_imageButtonClose != null) {
            //m_imageButtonClose.setImageDrawable(ResourceManager.getSingleImage("comm_pre_icon_09")) // eBest Mine μ΄λ―Έμ§
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

                //μμ΄νμ΄ ν΄λ¦­ λλ©΄ λ§¨ μλΆν° position 0λ²λΆν° μμλλ‘ λμνκ² λ©λλ€.
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

    // κ³΅μΈμΈμ¦μ λ‘κ·ΈμΈ
    fun OnButtonLoginClicked(v: View) {

        m_password = m_editTextPWD.text.toString()
        //m_dn = m_textViewDN.text.toString()

        if (m_password.length == 0) {
            Toast.makeText(this, "κ³΅μΈμΈμ¦ λΉλ°λ²νΈλ₯Ό μλ ₯νμΈμ.", Toast.LENGTH_SHORT).show()
            return
        }

        if (m_dn.length == 0 || m_dn == "κ³΅μΈμΈμ¦μλ₯Ό μ ννμΈμ.") {
            Toast.makeText(this, "κ³΅μΈμΈμ¦μλ₯Ό μ ννμΈμ.", Toast.LENGTH_SHORT).show()
            return
        }

        m_buttonLogin.setEnabled(false)
        if (manager.isConnect() == true) {
            manager.disconnect()
        }

        var nReturn = manager.connect(m_nHandle, 1)
        if (nReturn == 0) {
            //m_customAnimationDialog!!.show()
            //m_customAnimationDialog!!.setTextMsg("λ‘κ·ΈμΈ μ€μλλ€. μ μλ§ κΈ°λ€λ¦¬μΈμ.")
            nReturn = manager.loginSign(m_nHandle, m_password, m_dn)
        } else {
            m_buttonLogin.setEnabled(true)
        }

    }

    // μΈμ¦μ κ°μ Έμ€κΈ°
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

    // μ’λ£λ²νΌ
    fun OnButtonCloseClicked() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    fun popupNotAble(strMsg:String) {

        var DialogBuilder = AlertDialog.Builder(this)

        // μ λͺ©μν
        DialogBuilder.setTitle("κ³΅μΈμΈμ¦")

        // AlertDialog μν
        DialogBuilder
            .setMessage(strMsg)
            .setCancelable(false)
            .setPositiveButton("νμΈ"
            ) { dialog, id ->
                // νλ‘κ·Έλ¨μ μ’λ£νλ€
                dialog.cancel()
                closeActivity(Activity.RESULT_CANCELED)
            }
        //.setNegativeButton("μ·¨μ"
        //) { dialog, id ->
        //    // λ€μ΄μΌλ‘κ·Έλ₯Ό μ·¨μνλ€
        //    dialog.cancel()
        //    DupConnDialogBuilder = null
        //    disConnect()
        //}

        // λ€μ΄μΌλ‘κ·Έ μμ±
        try {
            //m_customAnimationDialog!!.dismiss()
            val alertDialog = DialogBuilder.create()

            // λ€μ΄μΌλ‘κ·Έ λ³΄μ¬μ£ΌκΈ°
            alertDialog.show()
        } catch (e: Exception) {
            val str = e.toString()
        }

    }

//    fun checkSign(): ArrayList<String> {
//
//        var items = ArrayList<String>()
//        // κ³΅μΈμΈμ¦μ λͺ©λ‘ μμ
//        val nCount = m_SM.signCnt
//        for (i in 0 until nCount) {
//
//            //String strSubjectName = m_SM.getSignStorage(i, 0);          // DN
//            val strSubjectName = m_SM.getSignDN(i)                      // DN
//            val strPolicy = m_SM.getSignStorage(i, 1)               // λ²μ©OID
//            val strIssuerCn = m_SM.getSignStorage(i, 2)             // λ°κΈκΈ°κ΄
//            val strExpiredTime = m_SM.getSignStorage(i, 3)          // λ§λ£μΌ
//            val strSerialNumberInt = m_SM.getSignStorage(i, 4)      // serial num
//            val strSubjectDn = m_SM.getSignStorage(i, 5)            //
//            val strPolicyNumString = m_SM.getSignStorage(i, 6)      //
//
//            items.add(strSubjectDn)
//        }
//        // κ³΅μΈμΈμ¦μ λͺ©λ‘ λ
//
//        return items
//    }

    fun checkSign() : ArrayList<String>{
        var items = ArrayList<String>()

        var temp =  manager.getSignList(this);

        val nCount = temp.size
        for (i in 0 until nCount) {


            val strSubjectName = temp.get(i).strSubjectName;        // DN
            val strPolicy =  temp.get(i).strPolicy;                // λ²μ©OID
            val strIssuerCn = temp.get(i).strIssuerCn;            // λ°κΈκΈ°κ΄
            val strExpiredTime = temp.get(i).strExpiredTime;         // λ§λ£μΌ
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

        // HT1 μ­μ ν΄μΌνλ€
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