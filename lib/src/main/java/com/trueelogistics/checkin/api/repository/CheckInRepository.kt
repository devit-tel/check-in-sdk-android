package com.trueelogistics.checkin.api.repository

import com.trueelogistics.checkin.api.ApiService
import com.trueelogistics.checkin.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class CheckInRepository {

    companion object {
        @get:Synchronized
        val instance = CheckInRepository()
    }

    fun postCreateQRCode(
        qrCodeCreateBy: String,
        locationId: String,
        latitude: String,
        longitude: String
    ): Observable<Response<GenQrRootModel>> {
        return ApiService.checkInService().postCreateQRCode(
            qrCodeCreateBy,
            locationId,
            latitude,
            longitude
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
    }

    fun getLastCheckInHistory(): Observable<Response<HistoryTodayModel>> {
        return ApiService.checkInService().getLastCheckInHistory()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
    }

    fun getCheckInOverTime(): Observable<Response<CheckOverTimeModel>> {
        return ApiService.checkInService().getCheckInOverTime()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
    }

    fun getHistory(
        search: String? = null,
        limit: Int? = null,
        page: Int? = null
    ): Observable<Response<HistoryRootModel>> {
        return ApiService.checkInService().getHistory(
            search, page, limit
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
    }

    fun postCheckIn(
        type: String,
        qrcodeUniqueKey: String? = null,
        locationId: String? = null,
        checkinType: String? = null,
        latitude: String? = null,
        longitude: String? = null
    ): Observable<Response<ScanRootModel>> {
        return ApiService.checkInService().postCheckIn(
            type,
            qrcodeUniqueKey,
            locationId,
            checkinType,
            latitude,
            longitude
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
    }
}