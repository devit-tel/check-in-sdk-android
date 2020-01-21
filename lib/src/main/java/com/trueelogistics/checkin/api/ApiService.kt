package com.trueelogistics.checkin.api

import com.trueelogistics.checkin.api.service.CheckInService
import com.trueelogistics.checkin.enums.EnvironmentType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.model.HeaderModel


class ApiService {

    companion object {

        fun checkInService(
            baseUrl: String = if (CheckInTEL.environmentType == EnvironmentType.Production.value)
                //TODO use when API Production Launch
//                "http://api.sendit.asia"
                "http://api.staging.sendit.asia"
            else
                "http://api.staging.sendit.asia",
            needHeader: Boolean = true,
            moreHeader: ArrayList<HeaderModel>? = null,
            token: String? = null
        ): CheckInService {
            return RetrofitConnection.create(baseUrl, needHeader, moreHeader, token)
                .create(CheckInService::class.java)
        }
    }
}