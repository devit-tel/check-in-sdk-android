package com.trueelogistics.checkin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInErrorType
import com.trueelogistics.checkin.extensions.replaceFragmentInActivity
import com.trueelogistics.checkin.fragment.ScanQrFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.CheckInTEL.Companion.KEY_ERROR_CHECK_IN_TEL


class ScanQrActivity : AppCompatActivity() {

    companion object {
        const val KEY_TYPE_SCAN_QR = "KEY_TYPE_SCAN_QR"
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
                                    putString(KEY_TYPE_SCAN_QR, typeCheckIn)
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
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).onAccepted {
                    replaceFragmentInActivity(
                            R.id.fragment,
                            ScanQrFragment.newInstance(intent.extras),
                            ScanQrFragment.TAG
                    )
                }.onDenied {
                    showToastMessage(CheckInErrorType.PERMISSION_DENIED_ERROR.message)
                    setResult(
                            Activity.RESULT_OK,
                            Intent(this, CheckInTEL::class.java).putExtras(
                                    Bundle().apply {
                                        this.putString(
                                                KEY_ERROR_CHECK_IN_TEL,
                                                CheckInErrorType.PERMISSION_DENIED_ERROR.message
                                        )
                                    }
                            )
                    )
                    finishAffinity()
                }
                .ask()
    }

    override fun onBackPressed() {
        if (!intent.getBooleanExtra(KEY_DISABLE_BACK, false) || supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        }
    }

    fun showToastMessage(message: String?) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show()
    }
}