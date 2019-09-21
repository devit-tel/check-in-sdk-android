package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.enums.EnvironmentType
import com.trueelogistics.checkin.handler.CheckInTEL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitGenerater {
    fun build(isRequestHeader: Boolean? = false): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                if (isRequestHeader == true) {
                    newRequest.addHeader("Content-Type", "application/json")
                        .addHeader("packageName", CheckInTEL.packageName ?: "")
                        .addHeader("userId", CheckInTEL.userId ?: "")
                        .addHeader("sha1", CheckInTEL.sha1 ?: "")
                        .addHeader("APIKey", CheckInTEL.app ?: "")
                } else {
                    newRequest.addHeader("userId", CheckInTEL.userId ?: "")
                }
                chain.proceed(newRequest.build())
            }
            .build()
        val baseUrl = if (CheckInTEL.environmentType == EnvironmentType.Production.value)
            "http://api.sendit.asia"
        else
            "http://api.staging.sendit.asia"
        return Retrofit.Builder().baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}