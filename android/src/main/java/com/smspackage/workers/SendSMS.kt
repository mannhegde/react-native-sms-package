package com.smspackage.workers

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.sms.SmsModule.Companion.SMS_SENDING_FAILED

class SendSMS(private var reactApplicationContext: ReactApplicationContext) {

    fun onSuccessfulSmsSent(
        smsRecipient: String?,
        smsPayload: String,
        promise: Promise
    ) {
        if (smsRecipient == null) {
            promise.reject(java.lang.Exception("SMS Destination is invalid"))
        }

        var cursor: Cursor? = null;

        try {
            cursor = reactApplicationContext.contentResolver.query(
                Uri.parse("content://sms/sent"),
                null,
                null,
                null,
                "date DESC"
            )

            if (cursor?.moveToFirst() == true) {
                val addressIndex = cursor.getColumnIndex("address")
                val bodyIndex = cursor.getColumnIndex("body")

                if (addressIndex == -1 || bodyIndex == -1) {
                    promise.reject(Exception(SMS_SENDING_FAILED))
                    return
                }

                val address = cursor.getString(addressIndex)
                val body = cursor.getString(bodyIndex)

                if (address == smsRecipient && body == smsPayload) {
                    promise.resolve("success")
                } else {
                    promise.reject(Exception(SMS_SENDING_FAILED))
                }
            } else {
                promise.reject(Exception(SMS_SENDING_FAILED))
            }
        } catch (e: Exception) {
            promise.reject(e)
        } finally {
            cursor?.close()
        }
    }

    fun sendSmsToMultipleRecipients(recipients: List<String>, message: String, subscriptionId:Int?,shouldVerify:Boolean?, promise: Promise) {
        for (recipient in recipients) {
            sendSms(recipient, message, subscriptionId,shouldVerify, promise)
        }
    }

    /*fun sendSms(
        smsRecipient: String?,
        smsPayload: String,
        subscriptionId: Int?,
        shouldVerify:Boolean?,
        promise: Promise
    ) {
        try {
            if (smsRecipient == null) {
                promise.reject(java.lang.Exception("SMS Destination is invalid"))
            }

            val smsManager: SmsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId ?: SmsManager.getDefaultSmsSubscriptionId())
            val sent = "SMS_SENT"
            val sentPI = PendingIntent.getBroadcast(
                reactApplicationContext,
                0,
                Intent(sent),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                reactApplicationContext.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(arg0: Context?, arg1: Intent?) {
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                if(shouldVerify==true){
                                    onSuccessfulSmsSent(smsRecipient, smsPayload, promise)
                                }else{
                                    promise.resolve("success")
                                }
                            }
                            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> promise.resolve("generic-failure")
                            SmsManager.RESULT_ERROR_NO_SERVICE -> promise.resolve("no-service-failure")
                            SmsManager.RESULT_ERROR_NULL_PDU -> promise.resolve("null-pdu-failure")
                            SmsManager.RESULT_ERROR_RADIO_OFF -> promise.resolve("radio-off-failure")
                        }
                    }
                }, IntentFilter(sent), RECEIVER_EXPORTED)
            } else {
                ContextCompat.registerReceiver(
                    reactApplicationContext,
                    object : BroadcastReceiver() {
                        override fun onReceive(arg0: Context?, arg1: Intent?) {
                            when (resultCode) {
                                Activity.RESULT_OK ->  {
                                    if(shouldVerify==true){
                                        onSuccessfulSmsSent(smsRecipient, smsPayload, promise)
                                    }else{
                                        promise.resolve("success")
                                    }
                                }
                                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> promise.resolve("generic-failure")
                                SmsManager.RESULT_ERROR_NO_SERVICE -> promise.resolve("no-service-failure")
                                SmsManager.RESULT_ERROR_NULL_PDU -> promise.resolve("null-pdu-failure")
                                SmsManager.RESULT_ERROR_RADIO_OFF -> promise.resolve("radio-off-failure")
                            }
                        }
                    },
                    IntentFilter(sent),
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
            }
            smsManager.sendTextMessage(smsRecipient, null, smsPayload, sentPI, null)
        } catch (e: Exception) {
            e.message?.let { Log.i("SendSMS", it) }
            promise.reject(e)
        }
    }*/

    fun sendSms(
        smsRecipient: String?,
        smsPayload: String,
        subscriptionId: Int?,
        shouldVerify: Boolean?,
        promise: Promise
    ) {
        if (smsRecipient.isNullOrEmpty()) {
            promise.reject(Exception("SMS Destination is invalid"))
            return
        }

        val smsManager = SmsManager.getSmsManagerForSubscriptionId(
            subscriptionId ?: SmsManager.getDefaultSmsSubscriptionId()
        )

        val sentIntent = Intent("SMS_SENT")
        val sentPI = PendingIntent.getBroadcast(
            reactApplicationContext,
            0,
            sentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val smsSentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (shouldVerify == true) {
                            onSuccessfulSmsSent(smsRecipient, smsPayload, promise)
                        } else {
                            promise.resolve("success")
                        }
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> promise.resolve("generic-failure")
                    SmsManager.RESULT_ERROR_NO_SERVICE -> promise.resolve("no-service-failure")
                    SmsManager.RESULT_ERROR_NULL_PDU -> promise.resolve("null-pdu-failure")
                    SmsManager.RESULT_ERROR_RADIO_OFF -> promise.resolve("radio-off-failure")
                    else -> promise.resolve("unknown-error")
                }
                reactApplicationContext.unregisterReceiver(this)
            }
        }

        try {
            ContextCompat.registerReceiver(
                reactApplicationContext,
                smsSentReceiver,
                IntentFilter("SMS_SENT"),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            smsManager.sendTextMessage(smsRecipient, null, smsPayload, sentPI, null)
        } catch (e: Exception) {
            Log.e("SendSMS", "Error sending SMS: ${e.message}", e)
            promise.reject(e)
            reactApplicationContext.unregisterReceiver(smsSentReceiver)
        }
    }

    fun sendSmsManually(smsRecipient: String, smsPayload: String, promise: Promise) {
        try {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            sendIntent.data = Uri.parse("smsto:" + smsRecipient)
            sendIntent.putExtra("sms_body", smsPayload)
            sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            reactApplicationContext.startActivity(sendIntent)
            promise.resolve("success")
        } catch (e: Exception) {
            promise.resolve("failure")
        }
    }
}
