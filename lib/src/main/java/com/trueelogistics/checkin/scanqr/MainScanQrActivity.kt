package com.trueelogistics.checkin.scanqr

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.trueelogistics.checkin.Interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.handler.CheckInTEL
import kotlinx.android.synthetic.main.activity_main_scan_qr.*

class MainScanQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)

        checkInBtn.setOnClickListener {
            openScanQr(this)
        }
        checkBetBtn.setOnClickListener {
            openScanQr(this)
        }
        checkOutBtn.setOnClickListener {
            openScanQr(this)
        }
        genQr.setOnClickListener {
            CheckInTEL.checkInTEL?.openGenerateQRCode(this,"userId", object : CheckInTELCallBack {
                override fun onCancel() {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCancel === ", Toast.LENGTH_SHORT).show()
                }
                override fun onCheckInFailure(message: String) {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                }
                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun openScanQr(context: Context) {
        CheckInTEL.checkInTEL?.openScanQRCode(this,"userId", object : CheckInTELCallBack {
            override fun onCancel() {
                Toast.makeText(context, " ScanQr.onCancel === ", Toast.LENGTH_SHORT).show()
            }
            override fun onCheckInFailure(message: String) {
                Toast.makeText(context, " ScanQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
            }
            override fun onCheckInSuccess(result: String) {
                Toast.makeText(context, " ScanQr.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
