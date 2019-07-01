package com.trueelogistics.checkin.scanqr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.GenQrActivity
import kotlinx.android.synthetic.main.activity_main_scan_qr.*

class MainScanQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)

        checkInBtn.setOnClickListener {
            val intent = Intent(this, ScanQrActivity::class.java)
            this.startActivity(intent)
        }
        checkBetBtn.setOnClickListener {
            val intent = Intent(this, ScanQrActivity::class.java)
            this.startActivity(intent)
        }
        checkOutBtn.setOnClickListener {
            val intent = Intent(this, ScanQrActivity::class.java)
            this.startActivity(intent)
        }
        genQr.setOnClickListener {
            val intent = Intent(this, GenQrActivity::class.java)
            this.startActivity(intent)
        }
    }
}
