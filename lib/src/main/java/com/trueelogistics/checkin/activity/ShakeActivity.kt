package com.trueelogistics.checkin.activity

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.Interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.scanqr.ScanQrFragment
import com.trueelogistics.checkin.shakeit.ShakeFineFragment

class ShakeActivity : AppCompatActivity() {

    private var checkInTELCallBack: CheckInTELCallBack? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        KotlinPermissions.with(this) // where this is an FragmentActivity instance
            .permissions(
                Manifest.permission.CAMERA
            ).onAccepted {
                supportFragmentManager.beginTransaction().replace(R.id.fragment,
                    ShakeFineFragment()
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
