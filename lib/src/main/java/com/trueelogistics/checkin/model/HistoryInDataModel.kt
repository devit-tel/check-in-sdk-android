package com.trueelogistics.checkin.model

data class HistoryInDataModel(
    var deleted : Boolean = false,
    var _id : String? = null,
    var qrcodeId : HistoryQrCodeModel? =null,
    var packageName : String? = null,
    var userId : String? = null,
    var eventType : String? = null,
    var checkinType : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : Int? = null
)