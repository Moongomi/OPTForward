package com.example.smsforward

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager

class SMSReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            for(sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                if(BuildConfig.OTP_NUMBER.equals(sms.getOriginatingAddress())){
                    sendSMS(sms.displayMessageBody)
                }
            }
        }
    }
    private fun sendSMS(text:String) {
        var sms = SmsManager.getDefault()
        sms.sendTextMessage(BuildConfig.OTP_DEST,null,
            text,null,null)
    }
}