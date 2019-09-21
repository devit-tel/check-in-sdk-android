package com.trueelogistics.checkin.api.service

import com.trueelogistics.checkin.model.HistoryRootModel
import retrofit2.Call
import retrofit2.http.GET

interface GenHistoryService {
    @GET("/check-in/v1/history/")
    fun getData(): Call<HistoryRootModel>
}