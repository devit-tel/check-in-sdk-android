package com.trueelogistics.checkin.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.jaredrummler.android.device.DeviceName
import kotlinx.android.synthetic.main.activity_near_by.*

class NearByActivity : AppCompatActivity() {
    private var mMessageListener: MessageListener? = null
    private var mMessage: Message? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by)

        btSentNearBy.setOnClickListener {
            this.let {
                val message = DeviceName.getDeviceName().toByteArray(
                    Charsets.UTF_8
                )
                mMessage = object : Message(
                    message
                ) {}
                Nearby.getMessagesClient(it).publish(mMessage as Message)
            }
        }
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                Toast.makeText(
                    this@NearByActivity, " Found Device === $content",
                    Toast.LENGTH_LONG
                ).show()
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

    override fun onStart() {
        super.onStart()
        mMessageListener?.let { mML ->
            this.let {
                Nearby.getMessagesClient(it).subscribe(mML)
            }
        }
    }

    override fun onStop() {
        mMessage?.let { mMS ->
            this.let {
                Nearby.getMessagesClient(it).unpublish(mMS)
            }
        }
        mMessageListener?.let { mML ->
            this.let {
                Nearby.getMessagesClient(it).unsubscribe(mML)
            }
        }
        super.onStop()
    }
}
