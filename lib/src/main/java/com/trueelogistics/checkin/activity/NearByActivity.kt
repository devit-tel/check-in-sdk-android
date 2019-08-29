package com.trueelogistics.checkin.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.NearByFindingFragment

class NearByActivity : AppCompatActivity() {

    private var mMessageListener: MessageListener? = null
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
        mMessageListener?.let { mML ->
            this.let {
                Nearby.getMessagesClient(activity).subscribe(mML)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 2) {
            finish()
        } else
            super.onBackPressed()
    }

    override fun onStop() {
        mMessageListener?.let { mML ->
            Nearby.getMessagesClient(this).unsubscribe(mML)
        }
        super.onStop()
    }

    interface NearByCallback {
        fun onFoundNearBy(hubId: String? = "")
        fun onLostNearBy(hubId: String? = "")
    }
}
