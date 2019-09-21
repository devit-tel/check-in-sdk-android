package com.trueelogistics.checkin.model

data class ProfileDataModel(
    var _id: String? = null,
    var deleted: Boolean? = false,
    var qrcodeId: String? = null,
    var packageName: String? = null,
    var userId: String? = null,
    var eventType: String? = null,
    var checkinType: String? = null,
    var updatedAt: String? = null,
    var createdAt: String? = null,
    var __v: String? = null
)