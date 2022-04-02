package com.example.smsforward

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    companion object{
        const val otpsender = BuildConfig.OTP_NUMBER
        lateinit var editTextPhone: TextView
        lateinit var editTextTextMultiLine: TextView
        lateinit var button:Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val noty = NotyBuilder()

        editTextPhone = findViewById(R.id.editTextPhone)
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine)
        button = findViewById(R.id.button)

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS),111)
        }
        else{
            val flag = receiveMsg()
            if(flag){
                noty.sendNotification(this,"OTPForward","OTP Send SUCCESS")
            }
        }
        button.setOnClickListener {
            sendSMS(editTextTextMultiLine.text.toString())
            noty.sendNotification(this,"OTPForward","OTP Send SUCCESS")
        }
    }

    private fun sendSMS(text:String) {
        /*
        Todo
        1. SmsManager.getDefault() 대신하여 applicationContext.getSystemService(SmsManager::class.java) 로 메시지 전송하기
        2. 버전별 분류
         */

        var sms = SmsManager.getDefault()
        sms.sendTextMessage(BuildConfig.OTP_DEST,null,
            text,null,null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            receiveMsg()
        }
    }

    private fun receiveMsg(): Boolean {
        var flag = false
        var br = object:BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    for(sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)){
                        if(otpsender.equals(sms.getOriginatingAddress())){
                            editTextPhone.setText(sms.originatingAddress)
                            editTextTextMultiLine.setText(sms.displayMessageBody)
                            sendSMS(sms.displayMessageBody)
                            flag = true
                        }
                    }
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        return flag
    }
}