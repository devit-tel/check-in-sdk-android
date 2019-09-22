package com.trueelogistics.example

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
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
        btnCreateQRCodeCheckIn.setOnClickListener { createQRCodeCheckIn() }
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

    private fun createQRCodeCheckIn() {
        CheckInTEL.checkInTEL?.qrGenerate(
            "qrCodeCrateBy",
            "0",
            "0.00",
            "0.00",
            object : GenerateQrCallback {
                override fun onResponse(hubName: String?, qrCodeText: String?, time: String?) {
                    tvHubName.text = hubName
                    createQRCode(qrCodeText)
                }

                override fun onFailure(message: String?) {
                    showToastMessage(message)
                }
            }
        )
    }

    private fun showToastMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun createQRCode(qrCode: String?) {
        val writer = QRCodeWriter()
        try {
            qrCode?.also {
                val bitMatrix = writer.encode(
                    it,
                    BarcodeFormat.QR_CODE,
                    512,
                    512
                )
                val bmp = Bitmap.createBitmap(
                    bitMatrix.width,
                    bitMatrix.height,
                    Bitmap.Config.RGB_565
                )
                for (x in 0 until bitMatrix.width) {
                    for (y in 0 until bitMatrix.height) {
                        bmp.setPixel(
                            x,
                            y,
                            if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                        )
                    }
                }
                imgQRCode.setImageBitmap(bmp)
            }

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
