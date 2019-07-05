package com.trueelogistics.checkin.model.list_hub

data class InDataModel(
    var locationPoint : LocationPointModel,
    var deleted : Boolean = false,
    var _id : String? = null,
    var locationName : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : Int? = null
)