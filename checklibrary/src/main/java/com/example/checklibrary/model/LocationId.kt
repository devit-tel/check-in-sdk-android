package com.example.checklibrary.model

data class LocationId (
    var locationPoint: ArrayList<locationPoint>,
    var deleted : Boolean? = false,
    var _id : String? = null ,
    var locationName : String? = null ,
    var updatedAt : String? = null ,
    var createdAt : String? = null ,
    var __v : String? = null
)