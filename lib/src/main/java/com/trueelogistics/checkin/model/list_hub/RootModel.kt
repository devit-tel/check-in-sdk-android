package com.trueelogistics.checkin.model.list_hub

import com.trueelogistics.checkin.model.history.DataModel


data class RootModel (
    var data : DataModel,
    var status : String? = null,
    var statusCodes : Int? = null
)