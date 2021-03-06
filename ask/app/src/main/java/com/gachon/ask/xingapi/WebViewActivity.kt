package com.gachon.ask.xingapi

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Browser
import android.webkit.*
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ebest.api.SocketManager
import kotlinx.android.synthetic.main.activity_webview.*
import java.net.Socket
import com.gachon.ask.R


//class WebViewActivity : AppCompatActivity() {
class WebViewActivity : Activity() {

    val SERVER_URL = "file:///android_asset/www"
    //val SERVER_URL = "http://localhost"
    private var m_androidBridge: AndroidBridge? = null
    private var m_context: Context? = null
    private var m_nHandle = -1
    var handler: Handler? = null
    lateinit internal var manager: SocketManager
    lateinit var m_webView:android.webkit.WebView

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        m_context = applicationContext
        m_webView = findViewById(R.id.webView) as WebView
        m_webView.setBackgroundColor(Color.TRANSPARENT)
        m_webView.rootView.setBackgroundColor(Color.WHITE)
        WebView.setWebContentsDebuggingEnabled(true)
        m_webView.setWebChromeClient(WebChromeClient())


        m_webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {

            }
        })

        startWebView()

        // Bridge ???????????? ??????
        manager = (application as ApplicationManager).getSockInstance()
        m_androidBridge = AndroidBridge(m_webView, m_context, this, manager)
        m_webView.addJavascriptInterface(m_androidBridge!!, "eBestApp")

        // get SocketManager instance
        handler = m_androidBridge!!.getHandler()



//        var connectbtn =  findViewById(R.id.connectbtn) as Button
//        connectbtn.setOnClickListener {
//            var iptxt = findViewById(R.id.iptext) as EditText
//            var porttxt = findViewById(R.id.porttext) as EditText
//
//            var ip = iptxt.text.toString();
//            if(ip.length == 0){
//                ip = "192.168.0.1" // 192.168.0.1
//            }
//            var port = porttxt.text.toString();
//            if(port.length == 0) {
//                port = "7000" // ????????? port??? ??????????????????
//            }
//
//            val socket = Socket(ip, port.toInt()) // ip??? port??? ???????????? ??????????????? ????????? ????????????.
//            val outStream = socket.outputStream // outputStream - ???????????? ???????????? ??????????????????.
//            val inStream = socket.inputStream // inputStream - ???????????? ?????? ??????????????????.
//
//            val data = "????????? ???????????? ???????????????." // ???????????? byteArray??? ?????? ??? ??? ????????? ?????????.
//            outStream.write(data.toByteArray()) // toByteArray() ??????????????? charset??? ????????? ??? ????????????. ????????? utf-8
//
//            val available = inStream.available() // ???????????? ????????? ???????????? ????????? ????????? -1??? ???????????????.
//            if (available > 0){
//                val dataArr = ByteArray(available) // ???????????? ?????? byte array??? ????????????.
//                outStream.write(dataArr) // byte array??? ???????????? ?????????.
//                val data = String(dataArr) // byte array??? ???????????? ?????? String??? ????????????.
//                println("data : ${data}")
//                (findViewById(R.id.textView10) as TextView).text= data;
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        m_nHandle = manager.setHandler(this, handler as Handler)
        m_androidBridge!!.setHandle(m_nHandle)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.deleteHandler(m_nHandle)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun startWebView() {
        m_webView.clearCache(true)

        val settings = m_webView.getSettings()
        settings.setJavaScriptEnabled(true)
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true)
        settings.setAppCacheEnabled(true)
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE)

        settings.setAllowFileAccess(true)
        settings.setAllowContentAccess(true)
        settings.setAllowFileAccessFromFileURLs(true)
        settings.setAllowUniversalAccessFromFileURLs(true)


        //????????? html??? viewport ?????? ????????? ???????????? ??????.
        settings.setUseWideViewPort(true)

        //????????? html ???????????? ???????????? ??? ?????? ????????? ????????? ?????? ??????????????? ??????.
        settings.setLoadWithOverviewMode(true)

        var launchUrl = makeUrl()
        m_webView.loadUrl(launchUrl)
    }

    private fun makeUrl(): String {
        val url: String
        url = String.format("%s/index.html", SERVER_URL)
        return url
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            1 -> {

                when(resultCode) {
                    Activity.RESULT_OK -> {
                        m_androidBridge!!.loginCompleted()
                    }

                    Activity.RESULT_CANCELED -> {
                        //setResult(RESULT_CANCELED)
                        //finish()
                    }

                }

            }

        }

    }

}
