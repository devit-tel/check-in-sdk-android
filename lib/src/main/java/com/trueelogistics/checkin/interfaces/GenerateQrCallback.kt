package com.trueelogistics.checkin.interfaces

interface GenerateQrCallback {
    fun onResponse(hub_name : String ?= "" ,qrCodeText : String  ?= "" ,time : String  ?= "" )
    fun onFailure( message : String ?= "" )
}