package com.trueelogistics.checkin.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(type: String): String {
    val dateFormat = SimpleDateFormat(type, Locale.getDefault())
    return dateFormat.format(this)
}

fun String.formatISO(type: String): String {
    var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val formatDate = dateFormat.parse(this)
    dateFormat = SimpleDateFormat(type, Locale.getDefault())
    return dateFormat.format(formatDate ?: "")
}