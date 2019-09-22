package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.model.HubRootModel
import retrofit2.Call
import retrofit2.http.GET

interface HubService {
    @GET("/check-in/v1/location/")
    fun getData(): Call<HubRootModel>
}

