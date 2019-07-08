package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.HistoryRootModel
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HistoryService{
    @POST("/check-in/v1/history")
    @FormUrlEncoded
    fun getData() : Call<HistoryRootModel>
}