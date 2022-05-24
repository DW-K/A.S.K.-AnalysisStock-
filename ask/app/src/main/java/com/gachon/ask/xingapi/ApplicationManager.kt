package com.gachon.ask.xingapi

import android.app.Application
import com.ebest.api.SocketManager

class ApplicationManager : Application(){
    val sock = SocketManager()

    fun getSockInstance() : SocketManager {
        return sock
    }

    override fun onCreate() {
        super.onCreate()

        // get context
        sock.init(this, applicationContext)
    }
}