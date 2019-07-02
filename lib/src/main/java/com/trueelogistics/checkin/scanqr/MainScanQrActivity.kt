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
            openScanQr()
        }
        checkBetBtn.setOnClickListener {
            openScanQr()
        }
        checkOutBtn.setOnClickListener {
            openScanQr()
        }
        genQr.setOnClickListener {
            CheckInTEL.checkInTEL?.openGenarateQRCode(this, object : CheckInTELCallBack {
                override fun onCancel() {
                    Toast.makeText(this, " GenQr.onCancel === ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInFailure(message: String) {
                    Toast.makeText(this, " GenQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(this, " GenQr.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
    private fun openScanQr(){
        CheckInTEL.checkInTEL?.openScanQRCode(activity, object : CheckInTELCallBack {
            override fun onCancel() {
                Toast.makeText(this, " ScanQr.onCancel === ", Toast.LENGTH_SHORT).show()
            }

            override fun onCheckInFailure(message: String) {
                Toast.makeText(this, " ScanQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
            }

            override fun onCheckInSuccess(result: String) {
                Toast.makeText(this, " ScanQr.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
