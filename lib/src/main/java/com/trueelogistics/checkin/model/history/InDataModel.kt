package com.trueelogistics.checkin.model.history

data class InDataModel(
    var deleted : Boolean = false,
    var _id : String? = null,
    var type : String? = null,
    var qrcodeId : QrCodeModel,
    var packageName : String? = null,
    var userId : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : Int? = null
)