package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationPointModel(
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("coordinates")
    @Expose
    var coordinates: ArrayList<Int>
) : Parcelable