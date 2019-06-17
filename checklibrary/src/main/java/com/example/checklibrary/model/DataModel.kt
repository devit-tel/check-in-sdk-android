package com.example.checklibrary.model

data class DataModel (
    var deleted : Boolean? = false,
    var _id : String? = null,
    var locationIdModel : LocationIdModel,
    var qrcodeUniqueKey : String? = null,
    var updatedAt : String? = null,
    var createdAt : String? = null,
    var __v : String? = null

)