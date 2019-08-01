package com.trueelogistics.checkin.model

data class UserIdModel(
    var address : ArrayList<String> ,
    var isActive : Boolean ,
    var deleted : Boolean ,
    var _id : String ?= null ,
    var citizenId : String ?= null ,
    var updatedAt : String ?= null ,
    var birthday : String ?= null ,
    var lastname : String ?= null ,
    var firstname : String ?= null ,
    var phone : String ?= null ,
    var role : String ?= null ,
    var __v : Int ?= 0,
    var createdAt : String ?= null
)