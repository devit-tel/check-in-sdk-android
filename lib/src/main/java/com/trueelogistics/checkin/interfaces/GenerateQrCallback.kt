package com.trueelogistics.checkin.interfaces

interface GenerateQrCallback {
    fun qrGenerate( qrCodeText : String)
    fun timeLatest( time : String)
}