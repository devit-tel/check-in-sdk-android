package com.trueelogistics.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindingData()
    }

    private fun bindingData() {
        btnCheckHistory.setOnClickListener { historyCheckIn() }
        btnMainCheckIn.setOnClickListener { mainCheckIn() }
        btnScanCheckIn.setOnClickListener { scanCheckIn() }
        btnNearByCheckIn.setOnClickListener { nearByCheckIn() }
        btnShakeCheckIn.setOnClickListener { shakeCheckIn() }
    }

    private fun historyCheckIn() {}

    private fun mainCheckIn() {
        CheckInTEL.checkInTEL?.openMainScanQrCode(
            this,
            object : CheckInTELCallBack {
                override fun onCheckInSuccess(result: String) {
                    showToastMessage(result)
                }

                override fun onCheckInFailure(message: String) {
                    showToastMessage(message)
                }

                override fun onCancel() {
                    showToastMessage("Cancel")
                }

            })
    }

    private fun scanCheckIn() {
        CheckInTEL.checkInTEL?.openScanQRCode(
            activity = this,
            typeCheckIn = CheckInTELType.CheckIn.value,
            onDisableBack = false,
            checkInTELCallBack = object : CheckInTELCallBack {
                override fun onCheckInSuccess(result: String) {
                    showToastMessage(result)
                }

                override fun onCheckInFailure(message: String) {
                    showToastMessage(message)
                }

                override fun onCancel() {
                    showToastMessage("Cancel")
                }
            }
        )
    }

    private fun nearByCheckIn() {
        CheckInTEL.checkInTEL?.openNearBy(
            activity = this,
            checkInTELCallBack = object : CheckInTELCallBack {
                override fun onCheckInSuccess(result: String) {
                    showToastMessage(result)
                }

                override fun onCheckInFailure(message: String) {
                    showToastMessage(message)
                }

                override fun onCancel() {
                    showToastMessage("Cancel")
                }
            }
        )
    }

    private fun shakeCheckIn() {
        CheckInTEL.checkInTEL?.openShake(
            activity = this,
            checkInTELCallBack = object : CheckInTELCallBack {
                override fun onCheckInSuccess(result: String) {
                    showToastMessage(result)
                }

                override fun onCheckInFailure(message: String) {
                    showToastMessage(message)
                }

                override fun onCancel() {
                    showToastMessage("Cancel")
                }
            }
        )
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
