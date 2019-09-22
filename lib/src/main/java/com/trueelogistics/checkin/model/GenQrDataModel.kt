package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenQrDataModel(
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean? = false,
    @SerializedName("_id")
    @Expose
    var _id: String? = null,
    @SerializedName("qrcodeCreateBy")
    @Expose
    var qrcodeCreateBy: String? = null,
    @SerializedName("locationId")
    @Expose
    var locationId: LocationIdModel,
    @SerializedName("qrcodeUniqueKey")
    @Expose
    var qrcodeUniqueKey: String? = null,
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