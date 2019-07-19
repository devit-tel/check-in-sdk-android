package com.trueelogistics.checkin.interfaces

interface GenerateQrCallback {
    fun onResponse(qrCodeText : String  ?= "" ,time : String  ?= "" )
    fun onFailure( message : String ?= "" )
}