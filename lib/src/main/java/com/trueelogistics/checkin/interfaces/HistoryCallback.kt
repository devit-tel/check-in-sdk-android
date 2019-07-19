package com.trueelogistics.checkin.interfaces

import com.trueelogistics.checkin.model.HistoryInDataModel

interface HistoryCallback {
    fun onResponse(dataModel : ArrayList<HistoryInDataModel> ?= null)
    fun onFailure( message : String ?= "" )
}