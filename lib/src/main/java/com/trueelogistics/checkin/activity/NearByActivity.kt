package com.trueelogistics.checkin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.NearByFindingFragment
import com.trueelogistics.checkin.handler.CheckInTEL

class NearByActivity : AppCompatActivity() {
    companion object {
        private var mMessageListener: MessageListener? = null
    }

    interface NearByCallback {
        fun onFoundNearBy(hubId: String? = "")
        fun onLostNearBy(hubId: String? = "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by)

        supportFragmentManager.beginTransaction().add(R.id.frag_nearby, NearByFindingFragment())
                .commit()
    }

    fun itemNearBy(activity: Activity, itemListener: NearByCallback) {
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                        Charsets.UTF_8
                )
                itemListener.onFoundNearBy(content)
            }

            override fun onLost(message: Message?) {
                val content = message?.content?.toString(
                        Charsets.UTF_8
                )
                itemListener.onLostNearBy(content)
            }
        }
        mMessageListener?.let {
            Nearby.getMessagesClient(activity).subscribe(it)
        }

    }

    override fun onBackPressed() {
        mMessageListener?.let { ml ->
            Nearby.getMessagesClient(this).unsubscribe(ml)
            NearByFindingFragment.showView = true
        }
        finish()
    }
}
