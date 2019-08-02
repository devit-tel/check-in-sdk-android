package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.HistoryRootModel
import retrofit2.Call
import retrofit2.http.GET

interface HistoryTodayService{
    @GET("/check-in/v1/history/check-in")
    fun getData( ) : Call<HistoryRootModel>
}