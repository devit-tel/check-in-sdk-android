package com.trueelogistics.example

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindingData()
    }

    private fun bindingData() {
        CheckInTEL.userId = "8888888888888"
        btnCheckLastHistory.setOnClickListener { historyCheckIn() }
        btnGetAllHistory.setOnClickListener { allHistoryCheckIN() }
        btnMainCheckIn.setOnClickListener { mainCheckIn() }
        btnScanCheckIn.setOnClickListener { scanCheckIn() }
        btnNearByCheckIn.setOnClickListener { nearByCheckIn() }
        btnShakeCheckIn.setOnClickListener { shakeCheckIn() }
        btnCreateQRCodeCheckIn.setOnClickListener { createQRCodeCheckIn() }
    }

    private fun allHistoryCheckIN() {
        CheckInTEL.checkInTEL?.getAllHistory(
                1,
                10,
                object : ArrayListGenericCallback<HistoryInDataModel> {
                    override fun onResponse(dataModel: ArrayList<HistoryInDataModel>?) {
                        showToastMessage(dataModel.toString())
                    }

                    override fun onFailure(message: String?) {
                        showToastMessage(message)
                    }
                })
    }

    private fun historyCheckIn() {
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {

            override fun onResponse(type: String?, today: Boolean) {
                tvStatusLastHistory.text = type
            }

            override fun onFailure(message: String?) {
                showToastMessage(message)
            }
        })
    }

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
                "5d877d1f1f21e7f72e5decda",
                "5d4963f59d5b1fba2e0c37d7",
                "13.715383600000013",
                "100.50457460000007",
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
