package com.trueelogistics.checkin.api

import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.model.HeaderModel
import okhttp3.Interceptor
import okhttp3.Response

class SignedRequestInterceptor(
    private val moreHeader: ArrayList<HeaderModel>? = null,
    private val token: String? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            header("content-type", "application/json")
            header("packageName", CheckInTEL.packageName ?: "")
            header("userId", CheckInTEL.userId ?: "")
            header("sha1", CheckInTEL.sha1 ?: "")
            header("APIKey", CheckInTEL.app ?: "")
            moreHeader?.forEach { item ->
                header(item.key ?: "", item.value ?: "")
            }
        }
        return chain.proceed(request.build())
    }
}