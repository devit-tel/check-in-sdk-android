package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchCitizenModel(
    @SerializedName("citizenId")
    @Expose
    var citizenId: String? = null
) : Parcelable