package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistoryDataModel(
    @SerializedName("data")
    @Expose
    var data: ArrayList<HistoryInDataModel>,
    @SerializedName("total")
    @Expose
    var total: Int? = null,
    @SerializedName("limit")
    @Expose
    var limit: Int? = null,
    @SerializedName("page")
    @Expose
    var page: Int? = null,
    @SerializedName("hasNext")
    @Expose
    var hasNext: Boolean = false
) : Parcelable