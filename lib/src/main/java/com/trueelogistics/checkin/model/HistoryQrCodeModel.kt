package com.trueelogistics.checkin.model

data class HistoryQrCodeModel(
    var deleted : Boolean = false,
    var _id : String? = null,
    var qrcodeCreateBy : String? = null,
    var locationId : String? = null,
    var qrcodeUniqueKey : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : Int
)