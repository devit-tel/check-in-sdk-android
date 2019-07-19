package com.trueelogistics.checkin.interfaces

interface TypeCallback {
    fun onResponse(type : String ?= "")
    fun onFailure( message : String ?= "" )
}