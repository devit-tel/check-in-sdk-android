package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.history.RootModel
import retrofit2.Call
import retrofit2.http.GET

interface GenHistoryService {
    @GET("/check-in/v1/history/")
    fun getData() : Call<RootModel>
}