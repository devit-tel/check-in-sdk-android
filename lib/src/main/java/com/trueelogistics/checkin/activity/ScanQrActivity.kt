package com.trueelogistics.checkin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.ScanQrFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.CheckInTEL.Companion.KEY_ERROR_CHECK_IN_TEL


class ScanQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
        KotlinPermissions.with(this) // where this is an FragmentActivity instance
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).onAccepted {
                val type = intent.getStringExtra("type")
                val disable = intent.getBooleanExtra("disable", false)
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment,
                    ScanQrFragment.newInstance(type ?: "", disable)
                ).commit()
            }.onDenied {
                Toast.makeText(
                    this,
                    "Permission Denied",
                    Toast.LENGTH_LONG
                ).show()
                setResult(
                    Activity.RESULT_OK,
                    Intent(this, CheckInTEL::class.java).putExtras(
                        Bundle().apply {
                            this.putString(KEY_ERROR_CHECK_IN_TEL, "Permission Denied!!")
                        }
                    )
                )
                finish()
            }
            .ask()
    }

    override fun onBackPressed() {
        val currentFrag = this.supportFragmentManager.fragments[0].javaClass
        val scanQr = ScanQrFragment::class.java
        val disable = intent.getBooleanExtra("disable", false)
        if (currentFrag != scanQr ) {
            super.onBackPressed()
        } else if (ScanQrFragment.cancelFirstCheckIn || !disable) {
            CheckInTEL.checkInTEL?.onActivityResult(
                CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL
                ,Activity.RESULT_CANCELED,Intent(this,CheckInTEL::class.java))
            finish()
        }
    }
}