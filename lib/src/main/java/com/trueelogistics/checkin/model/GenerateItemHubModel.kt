package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenerateItemHubModel(
    @SerializedName("hubId")
    @Expose
    var hubId: String? = "",
    @SerializedName("hubName")
    @Expose
    var hubName: String? = ""
) : Parcelable