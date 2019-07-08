package com.trueelogistics.checkin.service

import android.content.pm.PackageManager
import com.trueelogistics.checkin.handler.CheckInTEL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetScanQrRetrofit {
    companion object{
        var getRetrofit : GetScanQrRetrofit? = null
        fun initial(){
            getRetrofit = GetScanQrRetrofit()
        }
    }
    fun build() : Retrofit {
        val interceptor = HttpLoggingInterceptor()

        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("packageName", "com.trueelogistics.staff")
                    .addHeader("userId", CheckInTEL.userId ?: "")
                    .addHeader("sha1", CheckInTEL.sha1 ?: "")
                    .addHeader("APIKey", CheckInTEL.app ?: "")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
        return Retrofit.Builder().baseUrl("http://api.staging.sendit.asia")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}