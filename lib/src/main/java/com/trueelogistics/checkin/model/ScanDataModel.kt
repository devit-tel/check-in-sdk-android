package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScanDataModel(
    @SerializedName("_id")
    @Expose
    var _id: String? = null,
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean? = false,
    @SerializedName("locationId")
    @Expose
    var locationId: String? = null,
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null,
    @SerializedName("longtitude")
    @Expose
    var longtitude: String? = null,
    @SerializedName("qrcodeId")
    @Expose
    var qrcodeId: String? = null,
    @SerializedName("packageName")
    @Expose
    var packageName: String? = null,
    @SerializedName("citizenId")
    @Expose
    var citizenId: String? = null,
    @SerializedName("userId")
    @Expose
    var userId: String? = null,
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
    var __v: String? = null
) : Parcelable