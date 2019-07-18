package com.trueelogistics.checkin.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Base64
import com.trueelogistics.checkin.activity.*
import com.trueelogistics.checkin.model.HistoryRootModel
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.extensions.*
import com.trueelogistics.checkin.interfaces.*
import com.trueelogistics.checkin.model.GenQrRootModel
import com.trueelogistics.checkin.model.HubRootModel
import com.trueelogistics.checkin.service.GenQrService
import com.trueelogistics.checkin.service.HistoryService
import com.trueelogistics.checkin.service.HubService
import com.trueelogistics.checkin.service.RetrofitGenerater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

class CheckInTEL {
    companion object {  // another class can call this value  by statis
        const val KEY_REQUEST_CODE_CHECK_IN_TEL = 1750
        var checkInTEL: CheckInTEL? = null
        var packageName: String? = null
        var sha1: String? = null
        var userId: String? = "guest"
        var app: String? = null
        fun initial(application: Application) {
            checkInTEL = CheckInTEL()
            checkInTEL?.setPackageName(application)
            checkInTEL?.setSha1(application)
        }
    }

    private var checkInTELCallBack: CheckInTELCallBack? = null // ???
    private fun setPackageName(application: Application) {
        packageName = application.applicationContext.packageName
        app = application.packageManager.getApplicationInfo(
            application.packageName, PackageManager.GET_META_DATA
        )
            .metaData.getString("com.trueelogistics.API_KEY")
    }

    private fun setSha1(application: Application) {
        @SuppressLint("PackageManagerGetSignatures")
        if (SDK_INT >= 28) {
            val packageManager =
                application.run {
                    packageManager.getPackageInfo(
                        packageName, PackageManager.GET_SIGNING_CERTIFICATES
                    )
                }
            val signatures = packageManager.signingInfo.apkContentsSigners
            val md = MessageDigest.getInstance("SHA-1")
            for (signature in signatures) {
                md.update(signature.toByteArray())
                sha1 = String(Base64.encode(md.digest(), Base64.NO_WRAP))
            }
        } else {
            val packageManagers =
                application.run {
                    packageManager.getPackageInfo(
                        packageName, PackageManager.GET_RESOLVED_FILTER
                    )
                }
            for (signature in packageManagers.signatures) {
                val md = MessageDigest.getInstance("SHA-1")
                md.update(signature.toByteArray())
                sha1 = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            }
        }
    }

    fun hubGenerater(listener : HubCallback){
        val retrofit = RetrofitGenerater().build().create(HubService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HubRootModel> {
            override fun onFailure(call: Call<HubRootModel>, t: Throwable) {

            }
            override fun onResponse(call: Call<HubRootModel>, response: Response<HubRootModel>) {
                if (response.code() == 200) {
                    val logModel: HubRootModel? = response.body()
                        listener.generateHub(logModel?.data?.data ?: arrayListOf())
                } else {
                    response.errorBody()
                }
            }
        })
    }

     fun  qrGenerate(qrCodeCreateBy : String,locationId : String , listener: GenerateQrCallback){
         val retrofit = RetrofitGenerater().build().create(GenQrService::class.java)
         val call = retrofit?.getData(qrCodeCreateBy, locationId) // "LeaderNo4","5d01d704136e06003c23024f"
         call?.enqueue(object : Callback<GenQrRootModel> {
             override fun onFailure(call: Call<GenQrRootModel>, t: Throwable) {
             }
             override fun onResponse(call: Call<GenQrRootModel>, response: Response<GenQrRootModel>) {
                 if (response.code() == 200) {
                     val root: GenQrRootModel? = response.body()
                     if (root?.status == "OK") {
                        listener.qrGenerate(root.data.qrcodeUniqueKey.toString())
                     }
                 } else {
                     response.errorBody()
                     listener.qrGenerate("Fail to Generate QrCode")
                 }
             }
         })
    }
    fun getLastCheckInHistory(listener: TypeCallback) {
        val retrofit = RetrofitGenerater().build().create(HistoryService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HistoryRootModel> {
            override fun onFailure(call: Call<HistoryRootModel>, t: Throwable) {

            }

            override fun onResponse(call: Call<HistoryRootModel>, response: Response<HistoryRootModel>) {
                if (response.code() == 200) {
                    val logModel: HistoryRootModel? = response.body()
                    val lastDatePick = logModel?.data?.data?.last()
                    if (lastDatePick?.updatedAt?.formatISO("yyyy-MM-dd") == Date().format("yyyy-MM-dd"))
                        listener.getType(lastDatePick.eventType ?: "")
                    else
                        listener.getType(CheckInTELType.CheckOut.value)
                } else {
                    response.errorBody()
                }
            }
        })
    }

    fun getHistory(dataModel: HistoryCallback) {

        val retrofit = RetrofitGenerater().build().create(HistoryService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HistoryRootModel> {
            override fun onFailure(call: Call<HistoryRootModel>, t: Throwable) {
            }

            override fun onResponse(call: Call<HistoryRootModel>, response: Response<HistoryRootModel>) {
                when {
                    response.code() == 200 -> {
                        val logModel: HistoryRootModel? = response.body()
                        if (logModel != null) {
                            dataModel.historyGenerate(logModel.data.data)
                        }
                    }
                    else -> {
                        response.errorBody()
                    }
                }
            }
        })
    }

    fun openScanQRCode(activity: Activity, userId: String?, typeCheckIn: String?,
                       checkInTELCallBack: CheckInTELCallBack ) {
        CheckInTEL.userId = userId
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, ScanQrActivity::class.java)
        intent.putExtras(
            Bundle().apply {
                putString("type", typeCheckIn)
            }
        )
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL) // confirm you not from other activity
    }

    fun openGenerateQRCode(activity: Activity, userId: String?, checkInTELCallBack: CheckInTELCallBack) {
        CheckInTEL.userId = userId
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

    fun openHistory(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, HistoryActivity::class.java)
        activity.startActivity(intent)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == KEY_REQUEST_CODE_CHECK_IN_TEL) {
            when (resultCode) {
                Activity.RESULT_OK ->
                    if (data != null) {
                        checkInTELCallBack?.onCheckInSuccess(data.getStringExtra("result") ?: "")
                    }
                Activity.RESULT_CANCELED -> checkInTELCallBack?.onCancel()
                else -> checkInTELCallBack?.onCheckInFailure(" Fail in ActivityResult ")
            }
        }
    }

}