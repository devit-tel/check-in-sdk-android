package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.ScanRootModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ScanQrService{
    @POST("/check-in/v1/history/create")
    @FormUrlEncoded
    fun getData(
        @Field("type") type:String,
        @Field("qrcodeUniqueKey")qrcodeUniqueKey:String ?= null,
        @Field("locationId")locationId:String ?= null,
        @Field("checkinType")checkinType:String ?= null,
        @Field("latitude")latitude:String ?= null,
        @Field("longitude")longitude:String ?= null
    ): Call<ScanRootModel>
}