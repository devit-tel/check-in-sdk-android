package com.trueelogistics.checkin.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.ManualCheckInFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.TypeCallback

class ShakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        CheckInTEL.checkInTEL?.getLastCheckInHistory( object : TypeCallback{
            override fun onResponse(type: String?) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_shake,
                    ManualCheckInFragment.newInstance(type.toString())
                ).commit()
            }

            override fun onFailure(message: String?) {

            }

        })
    }

}
