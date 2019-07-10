package com.trueelogistics.checkin.scanqr

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.R

class ScanQrActivity : AppCompatActivity() {

    private var checkInTELCallBack: CheckInTELCallBack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)

        KotlinPermissions.with(this) // where this is an FragmentActivity instance
            .permissions(
                Manifest.permission.CAMERA
            ).onAccepted {
                val type = intent.getStringExtra("type")

                supportFragmentManager.beginTransaction().replace(R.id.fragment,
                    ScanQrFragment.newInstance(type ?: "")
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
}