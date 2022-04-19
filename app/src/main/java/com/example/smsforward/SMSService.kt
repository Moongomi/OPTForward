package com.example.smsforward

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SMSService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}