package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistoryInDataModel(
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean = false,
    @SerializedName("_id")
    @Expose
    var _id: String? = null,
    @SerializedName("locationId")
    @Expose
    var locationId: LocationIdModel,
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null,
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null,
    @SerializedName("qrcodeId")
    @Expose
    var qrcodeId: HistoryQrCodeModel,
    @SerializedName("packageName")
    @Expose
    var packageName: String? = null,
    @SerializedName("citizenId")
    @Expose
    var citizenId: String? = null,
    @SerializedName("userId")
    @Expose
    var userId: UserIdModel,
    @SerializedName("eventType")
    @Expose
    var eventType: String? = null,
    @SerializedName("checkinType")
    @Expose
    var checkinType: String? = null,
    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null,
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null,
    @SerializedName("__v")
    @Expose
    var __v: Int? = 0,
    @SerializedName("locationName")
    @Expose
    var locationName: String? = null
) : Parcelable