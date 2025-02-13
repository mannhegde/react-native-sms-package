package com.smspackage

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("sender")
    val sender: String,
    @SerializedName("smsBody")
    val smsBody: String,
    @SerializedName("timestamp")
    val timestamp: String
){
    override fun toString(): String {
        return "Message(sender='$sender', smsBody='$smsBody', timestamp='$timestamp')"
    }
}
