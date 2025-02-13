package com.smspackage.workers

import android.database.Cursor
import android.net.Uri
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.sms.exceptions.ReadFailure
import com.sms.helpers.Message
import com.sms.helpers.toEpochMilli
import com.sms.helpers.toISO8601String
import com.sms.helpers.toZonedDateTime
import java.time.ZoneId

class ListSMS(private var reactApplicationContext: ReactApplicationContext) {

    fun fetchAllSMS(includePersonalSMS: Boolean?=false, order: String): Pair<List<Message>, Int> {

        val selection = if (includePersonalSMS!=true) {
            "address NOT LIKE '+%' AND LENGTH(address) NOT BETWEEN 9 AND 14"
        } else {
            null
        }

        val cursor: Cursor? = reactApplicationContext.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("address AS sender", "body AS smsBody", "date AS timestamp"),
            selection,
            null,
            "date $order"
        )

        cursor?.use {
            val messages = mutableListOf<Message>()
            while (it.moveToNext()) {
                getFormattedMessage(it)?.let { message ->
                    messages.add(message)
                }
            }
            return Pair(messages, messages.size)
        } ?: throw ReadFailure()
    }

    fun fetchSMSForPeriod(
        startDateTime: String? = null,
        endDateTime: String? = null,
        includePersonalSMS: Boolean? = false,
        order: String? = "ASC"
    ): Pair<List<Message>, Int> {
        val selectionClauses = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        // Filter for non-personal SMS if required
        if (includePersonalSMS!=true) {
            selectionClauses.add("address NOT LIKE '+%' AND LENGTH(address) NOT BETWEEN 9 AND 14")
        }

        // Filter for start date
        startDateTime?.let {
            val startTimestamp = it.toZonedDateTime().toEpochMilli()
            selectionClauses.add("date > ?")
            selectionArgs.add(startTimestamp.toString())
        }

        // Filter for end date
        endDateTime?.let {
            val endTimestamp = it.toZonedDateTime().toEpochMilli()
            selectionClauses.add("date < ?")
            selectionArgs.add(endTimestamp.toString())
        }

        // Combine selection clauses
        val selection = if (selectionClauses.isNotEmpty()) {
            selectionClauses.joinToString(" AND ")
        } else {
            null
        }

        // Query the SMS inbox
        val cursor: Cursor = reactApplicationContext.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("address AS sender", "body AS smsBody", "date AS timestamp"),
            selection,
            if (selectionArgs.isNotEmpty()) selectionArgs.toTypedArray() else null,
            "date $order"
        ) ?: throw ReadFailure()

        cursor.use {
            val messages = mutableListOf<Message>()
            while (it.moveToNext()) {
                getFormattedMessage(it)?.let { message ->
                    messages.add(message)
                }
            }
            return Pair(messages, messages.size)
        }
    }

    private fun getFormattedMessage(it: Cursor): Message? {
        var sender = ""
        var timestamp = ""
        var smsBody = ""
        it.columnNames.forEachIndexed { index, name ->
            val content = it.getString(index)
            if (content.isNullOrEmpty()) {
                return@getFormattedMessage null
            } else {
                if (name == "timestamp") {
                    timestamp = content.toLong().toZonedDateTime(true).toISO8601String()
                }
                if (name == "smsBody") {
                    smsBody = content
                }
                if (name == "sender") {
                    sender = content
                }
            }
        }
        return Message(sender, smsBody, timestamp)
    }

    fun fetchLatestSMS(limit: Int? = 1): List<Message> {
        val smsUri = Uri.parse("content://sms/inbox")
        val projection = arrayOf("address", "body", "date")
        val sortOrder = "date DESC LIMIT $limit"

        val messages = mutableListOf<Message>()

        val cursor: Cursor? = reactApplicationContext.contentResolver.query(
            smsUri,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            while (it.moveToNext()) {
                getFormattedMessage(it)?.let { message ->
                    messages.add(message)
                }
            }
        }

        return messages
    }
}