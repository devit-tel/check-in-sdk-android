package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HubInDataModel(
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean = false,
    @SerializedName("_id")
    @Expose
    var _id: String? = null,
    @SerializedName("locationName")
    @Expose
    var locationName: String? = null,
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null,
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
) : Parcelable