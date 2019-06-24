package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.generate_qr.RootModel
import retrofit2.Call
import retrofit2.http.*

interface GenQrService{
    @POST("/check-in/v1/qrcode/create")
    @FormUrlEncoded
    fun getData(
        @Field("qrcodeCreateBy") qrcodeCreateBy:String,
        @Field("locationId")locationId:String) : Call<RootModel>

}