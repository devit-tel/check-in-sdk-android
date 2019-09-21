package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.GenQrRootModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GenQrService {
    @POST("/check-in/v1/qrcode/create")
    @FormUrlEncoded
    fun getData(
        @Field("qrcodeCreateBy") qrcodeCreateBy: String? = null,
        @Field("locationId") locationId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Call<GenQrRootModel>
}