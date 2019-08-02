package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.HistoryRootModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryService{
    @GET("/check-in/v1/history")
    fun getData(
        @Query("search")search  : String ?= null
    ) : Call<HistoryRootModel>
}