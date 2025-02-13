package com.smspackage.broadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.smspackage.Message

class SMSReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    var message: Message? = null

    if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
      val bundle = intent.extras
      if (bundle != null) {
        // Retrieve the SMS message received.
        val pdus = bundle["pdus"] as Array<*>
        val format = bundle.getString("format")

        for (pdu in pdus) {
          val smsMessage =
            SmsMessage.createFromPdu(pdu as ByteArray, format)
          message = Message(
            smsMessage.originatingAddress.toString(),
            smsMessage.messageBody,
            smsMessage.timestampMillis.toString()
          )
        }
      }

      (context?.applicationContext as? ReactApplicationContext)?.getJSModule(
        DeviceEventManagerModule.RCTDeviceEventEmitter::class.java
      )?.emit("NEW_MESSAGE_RECEIVED", message)
    }
  }
}
