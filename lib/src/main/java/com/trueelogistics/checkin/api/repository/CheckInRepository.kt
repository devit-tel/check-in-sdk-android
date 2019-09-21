package com.trueelogistics.checkin.api.repository

import com.trueelogistics.checkin.api.ApiService
import com.trueelogistics.checkin.model.HistoryRootModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class CheckInRepository {

    companion object {
        @get:Synchronized
        val instance = CheckInRepository()
    }

    fun getHostory(): Observable<Response<HistoryRootModel>> {
        return ApiService.checkInService().getHistory()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
    }
}