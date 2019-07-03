package com.trueelogistics.checkin.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.util.Base64
import android.util.Log
import com.trueelogistics.checkin.Interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.activity.GenQrActivity
import com.trueelogistics.checkin.activity.NearByActivity
import com.trueelogistics.checkin.activity.ShakeActivity
import com.trueelogistics.checkin.scanqr.ScanQrActivity
import java.net.URLEncoder
import java.security.MessageDigest


class CheckInTEL {

    companion object {  // another class can call this value  by statis
        const val KEY_REQUEST_CODE_CHECK_IN_TEL = 1750
        var checkInTEL: CheckInTEL? = null
        var packageName: String? = null
        var sha1: String? = null
        fun initial(application: Application) {
            checkInTEL = CheckInTEL()
            checkInTEL?.setPackageName(application)
            checkInTEL?.setSha1(application)
        }
    }

    private var checkInTELCallBack: CheckInTELCallBack? = null // ???
    private fun setPackageName(application: Application) {
        packageName = application.applicationContext.packageName
    }

    private fun setSha1(application: Application) {
        @SuppressLint("PackageManagerGetSignatures")
        if (SDK_INT >= 28) {
            val packageManager =
                application.run { packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNING_CERTIFICATES) }
            val signatures = packageManager.signingInfo.apkContentsSigners
            val md = MessageDigest.getInstance("SHA-1")
            for (signature in signatures) {
                md.update(signature.toByteArray())
                var signatureBase64 = String(Base64.encode(md.digest(), Base64.NO_WRAP))
                signatureBase64 = URLEncoder.encode(signatureBase64, "UTF-8")
                Log.e("Signature Base64", signatureBase64)
//                yagQ7W+ZNPeXRC7GlssW1bZyttE=
            }
        } else {
            val packageManagers =
                application.run {packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNATURES) }
            for (signature in packageManagers.signatures) {
                val md = MessageDigest.getInstance("SHA-1")
                md.update(signature.toByteArray())
                sha1 = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.e("Signature LT 28", sha1)
//               yagQ7W+ZNPeXRC7GlssW1bZyttE=
//               Real sha1 = 7D:2B:89:70:00:35:66:11:77:14:BB:ED:11:EC:0F:E8:50:C0:B1:24
            }
        }
        Log.e("= package Name == ", packageName)
    }

    fun openScanQRCode(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, ScanQrActivity::class.java)
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL) // confirm you not from other activity
    }

    fun openGenerateQRCode(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
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
                else -> Log.e(" ERROR ", "!!!!!")
            }
        }
    }

}