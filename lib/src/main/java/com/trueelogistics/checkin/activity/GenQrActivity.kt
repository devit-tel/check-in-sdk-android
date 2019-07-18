package com.trueelogistics.checkin.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
import kotlinx.android.synthetic.main.activity_gen_qr.*
import net.glxn.qrgen.android.QRCode

class GenQrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gen_qr)
        getQr()
        val countTime = object : CountDownTimer(30000, 500) { // 1 second to onTick & 1 minit to onFinish
            override fun onTick(millisUntilFinished: Long) {
                timeCount.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                getQr()
                this.start()
            }
        }.start()
        refreshTime.setOnClickListener {
            getQr()
            countTime.start()
        }
    }

    fun getQr() {
        CheckInTEL.checkInTEL?.qrGenerate("LeaderNo4","5d01d704136e06003c23024f", object : GenerateQrCallback {
            override fun qrGenerate(qrCodeText: String) {
                val result = QRCode.from(qrCodeText).withSize(1000, 1000).bitmap()
                qrCode.setImageBitmap(result)
            }
        })

    }
}
