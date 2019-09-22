package com.trueelogistics.checkin.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserIdModel(
    @SerializedName("address")
    @Expose
    var address: ArrayList<String>? = null,
    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null,
    @SerializedName("deleted")
    @Expose
    var deleted: Boolean? = null,
    @SerializedName("_id")
    @Expose
    var _id: String? = null,
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("phone")
    @Expose
    var phone: String? = null,
    @SerializedName("role")
    @Expose
    var role: String? = null,
    @SerializedName("birthday")
    @Expose
    var birthday: String? = null,
    @SerializedName("lastname")
    @Expose
    var lastname: String? = null,
    @SerializedName("firstname")
    @Expose
    var firstname: String? = null,
    @SerializedName("citizenId")
    @Expose
    var citizenId: String? = null,
    @SerializedName("imgProfile")
    @Expose
    var imgProfile: String? = null,
    @SerializedName("hash")
    @Expose
    var hash: String? = null,
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null,
    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null,
    @SerializedName("__v")
    @Expose
    var __v: Int? = 0
) : Parcelable