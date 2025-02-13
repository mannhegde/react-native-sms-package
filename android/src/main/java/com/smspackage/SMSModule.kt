package com.smspackage

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.smspackage.workers.ListSMS
import com.smspackage.workers.SendSMS


class SmsModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        private lateinit var Sender: SendSMS
        private lateinit var Lister: ListSMS
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
        recipient: String,
        payload: String,
        subscriptionId: Int?,
        shouldVerifySuccessfulSend: Boolean?,
        promise: Promise
    ) {
        Sender.sendSms(
            recipient,
            payload,
            subscriptionId,
            shouldVerifySuccessfulSend,
            promise
        )
    }

    @ReactMethod
    fun sendSmsToMultipleRecipients(
        recipients[]: List<String>,
        payload: String,
        subscriptionId: Int?,
        shouldVerifySuccessfulSend: Boolean?,
        promise: Promise
    ) {
        Sender.sendSmsToMultipleRecipients(
            recipients,
            payload,
            subscriptionId,
            shouldVerifySuccessfulSend,
            promise
        )
    }

    @ReactMethod
    fun sendSmsManually(recipient: String, payload: String, promise: Promise) {
        Sender.sendSmsManually(recipient, payload, promise)
    }

    @ReactMethod
    fun fetchAllSMS(includePersonalMessages: Boolean, order: String, promise: Promise) {
        try {
            promise.resolve(Lister.fetchAllSMS(includePersonalMessages, order))
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun fetchSMSForPeriod(
        startDateTime: String,
        endDateTime: String,
        includePersonalMessages: Boolean,
        order: String,
        promise: Promise
    ) {
        try {
            promise.resolve(
                Lister.fetchSMSForPeriod(
                    startDateTime,
                    endDateTime,
                    includePersonalMessages,
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
