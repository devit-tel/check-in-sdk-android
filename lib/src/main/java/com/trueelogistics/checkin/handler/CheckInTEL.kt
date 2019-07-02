package com.trueelogistics.checkin.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.trueelogistics.checkin.Interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.activity.GenQrActivity
import com.trueelogistics.checkin.activity.NearByActivity
import com.trueelogistics.checkin.scanqr.ScanQrActivity
import com.trueelogistics.checkin.activity.ShakeActivity
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.provider.SyncStateContract.Helpers.update
import android.os.Build.VERSION.SDK_INT
import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.provider.SyncStateContract.Helpers.update
import java.awt.SystemColor.info
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.provider.SyncStateContract.Helpers.update






class CheckInTEL {

    companion object {  // another class can call this value statis

        var checkInTEL: CheckInTEL? = null
        const val KEY_REQUEST_CODE_CHECK_IN_TEL = 1750
        var packageName : String? = null
        fun initial() {
            checkInTEL = CheckInTEL()
        }
        fun packageName(activity: Activity){

            packageName = activity.applicationContext.packageName
            @SuppressLint("WrongConstant")
            val packageManager = if (SDK_INT >= 28)
                activity.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)

            for (signature in packageManager.signatures) {
                val signatures = packageInfo.signingInfo.getApkContentsSigners()
                val md = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    val signatureBase64 = String(Base64.encode(md.digest(), Base64.DEFAULT))
                    Log.d("Signature Base64", signatureBase64)
                }
            }
        }
    }

    private var checkInTELCallBack: CheckInTELCallBack? = null // ???

    fun openScanQRCode(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {

        this.checkInTELCallBack = checkInTELCallBack

        val intent = Intent(activity, ScanQrActivity::class.java)
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL) // confirm you not from other activity
    }
    fun openGenarateQRCode(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, GenQrActivity::class.java)
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL) // confirm you not from other activity
    }
    fun openNearBy(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, NearByActivity::class.java)
        activity.startActivity(intent)
    }

    fun openShake(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, ShakeActivity::class.java)
        activity.startActivity(intent)

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == KEY_REQUEST_CODE_CHECK_IN_TEL) {
            when (resultCode) {
                Activity.RESULT_OK ->
                    if (data != null) {
                        checkInTELCallBack?.onCheckInSuccess(data.getStringExtra("result"))
                    }

                Activity.RESULT_CANCELED -> checkInTELCallBack?.onCancel()
                else -> Log.e(" ERROR ","!!!!!")
            }
        }

    }

}