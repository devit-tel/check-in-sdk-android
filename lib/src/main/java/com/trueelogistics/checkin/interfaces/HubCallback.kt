package com.trueelogistics.checkin.interfaces

import com.trueelogistics.checkin.model.HubInDataModel

interface HubCallback {
    fun onResponse(dataModel : ArrayList<HubInDataModel> ?= null)
    fun onFailure( message : String ?= "" )
}