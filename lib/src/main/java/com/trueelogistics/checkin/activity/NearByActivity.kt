package com.trueelogistics.checkin.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
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
