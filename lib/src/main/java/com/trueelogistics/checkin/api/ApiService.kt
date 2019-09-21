package com.trueelogistics.checkin.api

import com.trueelogistics.checkin.api.service.CheckInService
import com.trueelogistics.checkin.model.HeaderModel


class ApiService {

    companion object {

        fun checkInService(
            baseUrl: String = "url_config",
            needHeader: Boolean = true,
            moreHeader: ArrayList<HeaderModel>? = null,
            token: String? = null
        ): CheckInService {
            return RetrofitConnection.create(baseUrl, needHeader, moreHeader, token)
                .create(CheckInService::class.java)
        }
    }
}