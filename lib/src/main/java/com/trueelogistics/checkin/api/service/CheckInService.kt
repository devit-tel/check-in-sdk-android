package com.trueelogistics.checkin.api.service

import com.trueelogistics.checkin.model.CheckOverTimeModel
import com.trueelogistics.checkin.model.GenQrRootModel
import com.trueelogistics.checkin.model.HistoryRootModel
import com.trueelogistics.checkin.model.HistoryTodayModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface CheckInService {

    @GET("/check-in/v1/history/")
    fun getHistory(): Observable<Response<HistoryRootModel>>

    @POST("/check-in/v1/qrcode/create")
    @FormUrlEncoded
    fun postScanQRCode(
        @Field("qrcodeCreateBy") qrCodeCreateBy: String? = null,
        @Field("locationId") locationId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Observable<Response<GenQrRootModel>>

    @GET("/check-in/v1/history")
    fun getHostory(
        @Query("search") search: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Observable<Response<HistoryRootModel>>

    @GET("/check-in/v1/history/check-in")
    fun getHistoryCheckIn(): Observable<Response<HistoryTodayModel>>


    @GET("/check-in/v1/history/flagCheckin")
    fun getCheckInOverTime(): Observable<Response<CheckOverTimeModel>>
}