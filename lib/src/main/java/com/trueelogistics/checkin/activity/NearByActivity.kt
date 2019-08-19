package com.trueelogistics.checkin.activity

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.NearByHubFragment
import kotlinx.android.synthetic.main.activity_near_by.*

class NearByActivity : AppCompatActivity() {
    private var mMessageListener: MessageListener? = null
    private var nearbyAnimation : AnimationDrawable ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by)

        back_page.setOnClickListener {
            onBackPressed()
        }
        nearByAnimation()
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                loading_hub_nearby.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frag_nearby, NearByHubFragment.newInstance(content?:""))
                    .commit()
            }

            override fun onLost(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                Toast.makeText(
                    this@NearByActivity, " Lost message = $content",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun nearByAnimation(){
        loading_hub_nearby.setBackgroundResource(R.drawable.nearby_finding)
        nearbyAnimation = loading_hub_nearby.background as AnimationDrawable
        nearbyAnimation?.start()
    }

    override fun onStart() {
        super.onStart()
        mMessageListener?.let { mML ->
            this.let {
                Nearby.getMessagesClient(it).subscribe(mML)
            }
        }
    }

    override fun onStop() {
        mMessageListener?.let { mML ->
            this.let {
                Nearby.getMessagesClient(it).unsubscribe(mML)
            }
        }
        super.onStop()
    }
}
