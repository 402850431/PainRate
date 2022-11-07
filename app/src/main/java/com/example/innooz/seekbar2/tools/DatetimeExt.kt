package com.example.innooz.seekbar2.tools

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

const val YMD_FORMAT = "yyyy/MM/dd"
const val YMDHM_FORMAT = "yyyyMMddHHmm"
const val MD_FORMAT = "MM/dd"
const val MDHM_FORMAT = "MM/dd(E) HH:mm"
const val HM_FORMAT = "hh:mm:ss"

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

fun Date.toTimeFormat(format: String ?= MDHM_FORMAT) : String {
    var formattedTime = ""
    try {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        formattedTime = dateFormat.format(this)
    } catch (e: Exception) {
        Log.e(">>>","Parse datetime failed : $e")
        e.printStackTrace()
    }
    return formattedTime
}
