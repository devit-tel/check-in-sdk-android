package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HubRootModel(
    @SerializedName("data")
    @Expose
    var data: HubDataModel,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("statusCodes")
    @Expose
    var statusCodes: Int? = null
) : Parcelable