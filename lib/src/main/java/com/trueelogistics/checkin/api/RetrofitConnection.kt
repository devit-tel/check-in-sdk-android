package com.trueelogistics.checkin.api


import com.trueelogistics.checkin.BuildConfig.DEBUG
import com.trueelogistics.checkin.model.HeaderModel
import com.trueelogistics.checkin.utils.Constant.Companion.SERVICE_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConnection {

    companion object {

        fun create(
            baseUrl: String = "BASE_URL",
            needHeader: Boolean = true,
            moreHeader: ArrayList<HeaderModel>? = null,
            token: String? = null
        ): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder().apply {
                if (needHeader)
                    addInterceptor(SignedRequestInterceptor(moreHeader, token))
                if (DEBUG) addInterceptor(logging)
                readTimeout(SERVICE_TIMEOUT, TimeUnit.SECONDS)
            }

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build()
        }
    }
}