package com.trueelogistics.checkin.model

data class HubInDataModel(
    var deleted : Boolean = false,
    var _id : String? = null,
    var locationName : String? = null,
    var latitude : String ?= null ,
    var longitude : String ?= null
)