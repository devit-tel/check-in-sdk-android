package com.trueelogistics.checkin.service

import com.trueelogistics.checkin.handler.CheckInTEL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetScanQrRetrofit {
    companion object{
        var getRetrofit : GetRetrofit? = null
        fun initial(){
            getRetrofit = GetRetrofit()
        }
    }
    fun build() : Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("packageName", CheckInTEL.packageName)
                    .addHeader("userId", "whatever_YOU_recieved_FROM_2STAGE_or_ONDEMAND")
                    .addHeader("sha1", "C9:A8:10:ED:6F:99:34:F7:97:44:2E:C6:96:CB:16:D5:B6:72:B6:D1")
                    .addHeader("APIKey", "10a34637ad661d98ba3344717656fcc76209c2f810a34637ad661d98ba3344717656fcc76209c2f8")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
        return Retrofit.Builder().baseUrl("http://api.staging.sendit.asia")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}