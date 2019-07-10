package com.trueelogistics.checkin.model

data class GenQrLocationIdModel (
    var locationPoint: GenQrLocationPointModel,
    var deleted : Boolean? = false,
    var _id : String? = null,
    var locationName : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : String? = null
)