package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.ProfileRootModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProfileService{
    @POST("/check-in/v1/profile")
    @FormUrlEncoded
    fun getData(
        @Field("qrcodeCreateBy") qrcodeCreateBy:String,
        @Field("locationId")locationId:String,
        @Field("latitude")latitude:String,
        @Field("longitude")longitude:String
    ) : Call<ProfileRootModel>
}