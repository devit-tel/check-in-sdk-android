package com.trueelogistics.checkin.activity

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.NearByHubFragment
import kotlinx.android.synthetic.main.activity_near_by.*

class NearByActivity : AppCompatActivity() {

    private var mMessageListener: MessageListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by)


    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount == 2){
            finish()
        }
        else
            super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        mMessageListener?.let { mML ->
            this.let {
                Nearby.getMessagesClient(it).subscribe(mML)
            }
        }
    }
}
