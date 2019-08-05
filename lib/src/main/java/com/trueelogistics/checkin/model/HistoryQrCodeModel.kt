package com.trueelogistics.checkin.model

data class HistoryQrCodeModel(
    var deleted : Boolean ?= null,
    var _id : String? = null,
    var locationId : LocationIdModel,
    var latitude : Double ?= null ,
    var longitude : Double ?= null ,
    var qrcodeUniqueKey : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : Int ?= null
)