package com.trueelogistics.checkin.scanqr

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.TypeCallback
import kotlinx.android.synthetic.main.activity_main_scan_qr.*

class MainScanQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)

        checkButton()
        checkInBtn.setOnClickListener {
            openScanQr(this, "CHECK_IN")
        }
        checkBetBtn.setOnClickListener {
            openScanQr(this, "CHECK_IN_BETWEEN")
        }
        checkOutBtn.setOnClickListener {
            openScanQr(this, "CHECK_OUT")
        }
        genQr.setOnClickListener {
            CheckInTEL.checkInTEL?.openGenerateQRCode(this, "userId", object : CheckInTELCallBack {
                override fun onCancel() {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCancel === ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInFailure(message: String) {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCheckSuccess = $result", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        checkButton()
    }

    private fun openScanQr(context: Context, type: String) {
        CheckInTEL.checkInTEL?.openScanQRCode(this, "userId", type, object : CheckInTELCallBack {
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

    private fun checkButton() {
        CheckInTEL.checkInTEL?.getHistory(object : TypeCallback {
            override fun getType(type: String) {
                if (type == "CHECK_IN" || type == "CHECK_IN_BETWEEN") {
                    checkInBtn.isEnabled = false
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))

                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                } else if (type == "CHECK_OUT") {
                    checkInBtn.isEnabled = true
                    checkBetBtn.isEnabled = false
                    checkOutBtn.isEnabled = false
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))

                } else {
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))

                }
            }
        })
    }
}
