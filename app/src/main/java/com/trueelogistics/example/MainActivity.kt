package com.trueelogistics.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindingData()
    }

    private fun bindingData() {
        btnCheckHistory.setOnClickListener { historyCheckIn() }
        btnScanCheckIn.setOnClickListener { scanCheckIn() }
        btnNearByCheckIn.setOnClickListener { nearByCheckIn() }
        btnShakeCheckIn.setOnClickListener { shakeCheckIn() }
    }

    private fun historyCheckIn() {
    }

    private fun scanCheckIn() {

    }

    private fun nearByCheckIn() {

    }

    private fun shakeCheckIn() {

    }
}
