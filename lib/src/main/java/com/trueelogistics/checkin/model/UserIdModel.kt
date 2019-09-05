package com.trueelogistics.checkin.model

data class UserIdModel(
    var address: ArrayList<String>? = null,
    var isActive: Boolean? = null,
    var deleted: Boolean? = null,
    var _id: String? = null,
    var username: String? = null,
    var phone: String? = null,
    var role: String? = null,
    var birthday: String? = null,
    var lastname: String? = null,
    var firstname: String? = null,
    var citizenId: String? = null,
    var imgProfile: String? = null,
    var hash: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var __v: Int? = 0

)