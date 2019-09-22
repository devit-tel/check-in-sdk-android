package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationIdModel(
    @SerializedName("locationPoint")
    @Expose
    var locationPoint: LocationPointModel,
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean? = false,
    @SerializedName("_id")
    @Expose
    var _id: String? = null,
    @SerializedName("locationName")
    @Expose
    var locationName: String? = null,
    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null,
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null,
    @SerializedName("__v")
    @Expose
    var __v: Int? = null
) : Parcelable