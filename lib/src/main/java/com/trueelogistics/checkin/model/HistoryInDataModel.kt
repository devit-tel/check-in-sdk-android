package com.trueelogistics.checkin.model

data class HistoryInDataModel(
    var deleted: Boolean = false,
    var _id: String? = null,
    var locationId: LocationIdModel,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var qrcodeId: HistoryQrCodeModel,
    var packageName: String? = null,
    var citizenId: String? = null,
    var userId: UserIdModel,
    var eventType: String? = null,
    var checkinType: String? = null,
    var updatedAt: String? = null,
    var createdAt: String? = null,
    var __v: Int? = 0,
    var locationName: String? = null
)