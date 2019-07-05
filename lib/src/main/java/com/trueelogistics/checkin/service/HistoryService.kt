package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.history.RootModel
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HisrotyService{
    @POST("/check-in/v1/history")
    @FormUrlEncoded
    fun getData() : Call<RootModel>
}