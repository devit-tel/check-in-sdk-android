package com.trueelogistics.checkin.interfaces

interface TypeCallback {
    fun onResponse(type : String ?= "" , today : Boolean)
    fun onFailure( message : String ?= "" )
}