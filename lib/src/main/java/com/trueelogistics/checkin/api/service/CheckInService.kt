package com.trueelogistics.checkin.api.service

import com.trueelogistics.checkin.model.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface CheckInService {

    @POST("/check-in/v1/qrcode/create")
    @FormUrlEncoded
    fun postCreateQRCode(
        @Field("qrcodeCreateBy") qrCodeCreateBy: String? = null,
        @Field("locationId") locationId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Observable<Response<GenQrRootModel>>

    @GET("/check-in/v1/history/check-in")
    fun getLastCheckInHistory(): Observable<Response<HistoryTodayModel>>


    @GET("/check-in/v1/history/flagCheckin")
    fun getCheckInOverTime(): Observable<Response<CheckOverTimeModel>>

    @GET("/check-in/v1/history")
    fun getHistory(
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Observable<Response<HistoryRootModel>>

    @FormUrlEncoded
    fun postCheckIn(
        @Field("type") type: String,
        @Field("qrcodeUniqueKey") qrcodeUniqueKey: String? = null,
        @Field("locationId") locationId: String? = null,
        @Field("checkinType") checkinType: String? = null,
        @Field("latitude") latitude: String? = null,
        @Field("longitude") longitude: String? = null
    ): Observable<Response<ScanRootModel>>
}