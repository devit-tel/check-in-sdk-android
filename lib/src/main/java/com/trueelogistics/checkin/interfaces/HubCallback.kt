package com.trueelogistics.checkin.interfaces

import com.trueelogistics.checkin.model.HubInDataModel

interface HubCallback {
    fun generateHub( dataModel : ArrayList<HubInDataModel>)
}