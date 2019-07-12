package com.trueelogistics.checkin.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.dateInt() : String{
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.format(type : String): String {
    val dateFormat = SimpleDateFormat(type, Locale.getDefault())
    return dateFormat.format(this)
}

fun String.formatISO(type : String) : String{  //format ISO -> 2019-07-12T07:17:59.969Z "TO" Fri Jul 12 18:01:46 GMT+07:00 2019
    var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatDate = dateFormat.parse(this)
    dateFormat = SimpleDateFormat(type, Locale.getDefault())
    return dateFormat.format(formatDate ?: "")
}

fun Date.dayText(): String {
    val dateFormat = SimpleDateFormat("EE", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.dayInt(): String {
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.mouthText(): String {
    val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.mouthInt(): String {
    val dateFormat = SimpleDateFormat("MM", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.yearInt(): String {
    val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.timeMinute(): String {
    val dateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    return dateFormat.format(this)
}