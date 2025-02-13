package com.smspackage

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.sms.workers.ListSMS
import com.sms.workers.SendSMS


class SmsModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        private lateinit var Sender: SendSMS
        private lateinit var Lister: ListSMS
        const val SMS_SENDING_FAILED = "Sms verification failed. Please try again."
    }

    override fun getName(): String {
        return "SmsModule"
    }

    init {
        Sender = SendSMS(reactApplicationContext)
        Lister = ListSMS(reactApplicationContext)
    }

    @ReactMethod
    fun sendSms(
        smsRecipient: String,
        smsPayload: String,
        subscriptionId: Int?,
        shouldVerify: Boolean?,
        promise: Promise
    ) {
        Sender.sendSms(
            smsRecipient,
            smsPayload,
            subscriptionId,
            shouldVerify,
            promise
        )
    }

    @ReactMethod
    fun sendSmsToMultipleRecipients(
        smsRecipient: List<String>,
        smsPayload: String,
        subscriptionId: Int?,
        shouldVerify: Boolean?,
        promise: Promise
    ) {
        Sender.sendSmsToMultipleRecipients(
            smsRecipient,
            smsPayload,
            subscriptionId,
            shouldVerify,
            promise
        )
    }

    @ReactMethod
    fun sendSmsManually(smsRecipient: String, smsPayload: String, promise: Promise) {
        Sender.sendSmsManually(smsRecipient, smsPayload, promise)
    }

    @ReactMethod
    fun fetchAllSMS(includePersonalSMS: Boolean, order: String, promise: Promise) {
        try {
            promise.resolve(Lister.fetchAllSMS(includePersonalSMS, order))
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun fetchSMSForPeriod(
        startDateTime: String,
        endDateTime: String,
        includePersonalSMS: Boolean,
        order: String,
        promise: Promise
    ) {
        try {
            promise.resolve(
                Lister.fetchSMSForPeriod(
                    startDateTime,
                    endDateTime,
                    includePersonalSMS,
                    order
                )
            )
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun fetchLatestSMS(limit: Int?, promise: Promise) {
        try {
            promise.resolve(
                promise.resolve(Lister.fetchLatestSMS(limit))
            )
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

}