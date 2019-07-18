package com.trueelogistics.checkin.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.MockDialogFragment
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
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                ?.addOnSuccessListener { location: Location? ->
                    if (location?.isFromMockProvider == false) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        CheckInTEL.checkInTEL?.qrGenerate("LeaderNo4", "5d01d704136e06003c23024f",
                            latitude.toString(), longitude.toString(), object : GenerateQrCallback {
                                override fun timeLatest(time: String) {
                                }

                                override fun qrGenerate(qrCodeText: String) {
                                    val result = QRCode.from(qrCodeText).withSize(1000, 1000).bitmap()
                                    qrCode.setImageBitmap(result)
                                }
                            })
                    } else {
                        MockDialogFragment().show(this.supportFragmentManager, "show")
                    }
                }
        }


    }
}
