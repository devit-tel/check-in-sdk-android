package com.trueelogistics.checkin.model.scan_qr

data class DataModel (
    var _id : String? = null,
    var deleted : Boolean? = false,
    var type : String? = null,
    var qrcodeId : String? = null,
    var packageName : String? = null,
    var userId : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : String? = null
)