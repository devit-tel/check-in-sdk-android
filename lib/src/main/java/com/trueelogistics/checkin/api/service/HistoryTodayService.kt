package com.trueelogistics.checkin.api.service

import com.trueelogistics.checkin.model.CheckOverTimeModel
import com.trueelogistics.checkin.model.HistoryTodayModel
import retrofit2.Call
import retrofit2.http.GET

interface HistoryTodayService {
    @GET("/check-in/v1/history/check-in")
    fun getData(): Call<HistoryTodayModel>


    @GET("/check-in/v1/history/flagCheckin")
    fun getCheckInOverTime(): Call<CheckOverTimeModel>
}