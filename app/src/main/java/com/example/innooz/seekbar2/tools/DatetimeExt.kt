package com.example.innooz.seekbar2.tools

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

const val MD_FORMAT = "MM/dd"
const val MDHM_FORMAT = "MM/dd(E) HH:mm"
const val HM_FORMAT = "hh:mm"

fun Long.toTimeFormat(format: String ?= MDHM_FORMAT) : String {
    var formattedTime = ""
    val unixTime = this*1000L
    try {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        formattedTime = dateFormat.format(unixTime)
    } catch (e: Exception) {
        Log.e(">>>","Parse datetime failed : $e")
        e.printStackTrace()
    }
    return formattedTime
}
