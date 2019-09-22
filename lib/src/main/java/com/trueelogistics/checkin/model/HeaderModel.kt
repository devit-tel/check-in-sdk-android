package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeaderModel(
    @SerializedName("key")
    @Expose
    var key: String? = null,
    @SerializedName("value")
    @Expose
    var value: String? = null
) : Parcelable