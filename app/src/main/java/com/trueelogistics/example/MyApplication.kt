package com.trueelogistics.example

import android.app.Application
import com.trueelogistics.checkin.enums.EnvironmentType
import com.trueelogistics.checkin.handler.CheckInTEL

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CheckInTEL.initial(this, EnvironmentType.Staging)
    }
}