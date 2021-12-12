package com.gachon.ask.xingapi

import android.app.Activity
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ebest.api.*
import com.gachon.ask.R

class sLoginSet : AppCompatActivity() {

    internal var m_nHandle = -1
    lateinit internal var manager: SocketManager
    internal var handler: ProcMessageHandler? = null
    internal inner class ProcMessageHandler : Handler() {

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.loginset_activity);
        handler = ProcMessageHandler()
        manager = (application as ApplicationManager).getSockInstance()

        this.findViewById<Button>(R.id.btn_login1).setOnClickListener {
            manager.loginPopupID(this,handler as Handler);

        }
        this.findViewById<Button>(R.id.btn_login2).setOnClickListener {
            manager.loginPopupSign(this,handler as Handler);
        }
        this.findViewById<Button>(R.id.btn_login3).setOnClickListener {
            //manager.loginID(0,"","");
            var intent = Intent(this,sLoginSample1::class.java)
            startActivityForResult(intent,1);
        }
        this.findViewById<Button>(R.id.btn_login4).setOnClickListener {
            // manager.loginSign(0,"","");
            var intent = Intent(this,sLoginSample2::class.java)
            startActivityForResult(intent,1);
        }

    }

    override fun onResume() {
        super.onResume()
        m_nHandle = manager.setHandler(this, handler as Handler)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.deleteHandler(m_nHandle)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            1 -> {
                /* LoginProcess 결과값 */
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        setResult(resultCode)
                        finish()
                    }
                    Activity.RESULT_CANCELED -> {
                        setResult(resultCode)
                        finish()
                    }
                }
            }
        }
    }
}