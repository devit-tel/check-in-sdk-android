package com.trueelogistics.checkin.interfaces

import java.io.Serializable

interface CheckInTELCallBack : Serializable{
    fun onCheckInSuccess(result: String)
    fun onCheckInFailure(message: String)
    fun onCancel()
}