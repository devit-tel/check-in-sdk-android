package com.trueelogistics.checkin.interfaces

interface GenerateQrCallback {
    fun onResponse(hubName : String ?= "" ,qrCodeText : String  ?= "" ,time : String  ?= "" )
    fun onFailure( message : String ?= "" )
}