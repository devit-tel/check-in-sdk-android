package com.trueelogistics.checkin.activity

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.ScanQrFragment
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack

class ScanQrActivity : AppCompatActivity() {
    private var checkInTELCallBack: CheckInTELCallBack? = null
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
                    this, "Permission Denied",
                    Toast.LENGTH_LONG
                ).show()
                checkInTELCallBack?.onCheckInFailure("Permission Denied") // set
                finish()
            }
            .ask()
    }

    override fun onBackPressed() {
        val f = this.supportFragmentManager.fragments[0].javaClass
        val disable = intent.getBooleanExtra("disable", false)
        if (f != ScanQrFragment::class.java || !disable) {
            super.onBackPressed()
        }
    }
}