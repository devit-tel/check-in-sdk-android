package com.trueelogistics.checkin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.ScanQrFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.CheckInTEL.Companion.KEY_ERROR_CHECK_IN_TEL


class ScanQrActivity : AppCompatActivity() {

    companion object {
        const val KEY_TYPE = "KEY_TYPE_SCAN_QR"
        const val KEY_DISABLE_BACK = "KEY_DISABLE_BACK"
        fun startActivityForResult(
            activity: Activity?,
            requestCode: Int,
            typeCheckIn: String?,
            onDisableBack: Boolean
        ) {
            activity?.startActivityForResult(
                Intent(activity, ScanQrActivity::class.java).apply {
                    this.putExtras(
                        Bundle().apply {
                            putString(KEY_TYPE, typeCheckIn)
                            putBoolean(KEY_DISABLE_BACK, onDisableBack)
                        }
                    )
                },
                requestCode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
        bindingData()
    }

    fun bindingData() {
        KotlinPermissions.with(this)
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
        if (intent.getBooleanExtra(KEY_DISABLE_BACK, false)) {
            super.onBackPressed()
        }
    }
}