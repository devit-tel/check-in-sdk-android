package com.trueelogistics.checkin.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Base64
import com.google.gson.Gson
import com.trueelogistics.checkin.activity.MainScanQrActivity
import com.trueelogistics.checkin.activity.NearByActivity
import com.trueelogistics.checkin.activity.ScanQrActivity
import com.trueelogistics.checkin.activity.ShakeActivity
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.enums.EnvironmentType
import com.trueelogistics.checkin.extensions.format
import com.trueelogistics.checkin.extensions.formatISO
import com.trueelogistics.checkin.fragment.ScanQrFragment
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.*
import com.trueelogistics.checkin.service.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

class CheckInTEL {
    companion object {  // another class can call this value  by statis
        const val KEY_REQUEST_CODE_CHECK_IN_TEL = 1750
        var environmentType: String? = null
        var checkInTEL: CheckInTEL? = null
        var packageName: String? = null
        var sha1: String? = null
        var userId: String? = null
        var app: String? = null
        fun initial(application: Application, env: EnvironmentType) {
            checkInTEL = CheckInTEL()
            checkInTEL?.setPackageName(application)
            checkInTEL?.setSha1(application)
            checkInTEL?.setEnv(env)
        }
    }

    private var checkInTELCallBack: CheckInTELCallBack? = null // ???
    private fun setEnv(env: EnvironmentType) {
        environmentType = if (env == EnvironmentType.Production)
            EnvironmentType.Production.value
        else
            EnvironmentType.Staging.value
    }

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
                        packageName, PackageManager.GET_SIGNATURES
                    )
                }
            val signatures = packageManager.signatures
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

    fun hubGenerater(listener: ArrayListGenericCallback<HubInDataModel>) {
        val retrofit = RetrofitGenerater().build().create(HubService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HubRootModel> {
            override fun onFailure(call: Call<HubRootModel>, t: Throwable) {
                listener.onFailure(t.toString())
            }

            override fun onResponse(call: Call<HubRootModel>, response: Response<HubRootModel>) {
                if (response.code() == 200) {
                    val logModel: HubRootModel? = response.body()
                    listener.onResponse(logModel?.data?.data ?: arrayListOf())
                } else {
                    response.errorBody()
                        listener.onFailure(response.errorBody().toString())
                }
            }
        })
    }

    fun qrGenerate(
        qrCodeCreateBy: String,
        locationId: String,
        latitude: String,
        longitude: String,
        listener: GenerateQrCallback
    ) {
        val retrofit = RetrofitGenerater().build().create(GenQrService::class.java)
        val call =
            retrofit?.getData(qrCodeCreateBy, locationId, latitude, longitude) // "LeaderNo4","5d01d704136e06003c23024f"
        call?.enqueue(object : Callback<GenQrRootModel> {
            override fun onFailure(call: Call<GenQrRootModel>, t: Throwable) {
                listener.onFailure(t.message)
            }

            override fun onResponse(call: Call<GenQrRootModel>, response: Response<GenQrRootModel>) {
                if (response.code() == 200) {
                    val root: GenQrRootModel? = response.body()
                    if (root?.status == "OK") {
                        val timeLatest = root.data.updatedAt.toString()
                        listener.onResponse(
                            root.data.locationId.locationName,
                            root.data.qrcodeUniqueKey.toString(),
                            timeLatest.formatISO("HH:mm")
                        )
                    }
                } else {
                    listener.onFailure(response.message())
                    response.errorBody()
                }
            }
        })
    }

    fun getLastCheckInHistory(typeCallback: TypeCallback) {
        val retrofit = RetrofitGenerater().build(false).create(HistoryTodayService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HistoryTodayModel> {
            override fun onFailure(call: Call<HistoryTodayModel>, t: Throwable) {
                typeCallback.onFailure(t.message)
            }

            override fun onResponse(call: Call<HistoryTodayModel>, response: Response<HistoryTodayModel>) {
                if (response.code() == 200) {
                    val logModel: HistoryTodayModel? = response.body()
                    if (logModel?.data?.size ?: 0 > 0) {
                        val lastDatePick = logModel?.data?.last()
                        if (lastDatePick?.updatedAt?.formatISO("yyyy-MM-dd") == Date().format("yyyy-MM-dd"))
                            typeCallback.onResponse(lastDatePick.eventType ?: "")
                        else
                            typeCallback.onResponse(CheckInTELType.CheckOut.value)
                    }
                    else{
                        typeCallback.onResponse(CheckInTELType.CheckOut.value)
                    }
                }
                else {
                    typeCallback.onFailure(response.message())
                    response.errorBody()
                }
            }
        })
    }

    fun getHistory(arrayListGenericCallback: ArrayListGenericCallback<HistoryInDataModel>) {
        val retrofit = RetrofitGenerater().build(false).create(HistoryTodayService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HistoryTodayModel> {
            override fun onFailure(call: Call<HistoryTodayModel>, t: Throwable) {
                arrayListGenericCallback.onFailure(t.message)
            }
            override fun onResponse(call: Call<HistoryTodayModel>, response: Response<HistoryTodayModel>) {
                when {
                    response.code() == 200 -> {
                        val logModel: HistoryTodayModel? = response.body()
                        if (logModel != null) {
                            arrayListGenericCallback.onResponse(logModel.data)
                        }
                    }
                    else -> {
                        response.errorBody()
                    }
                }
            }
        })
    }

    fun getAllHistory(arrayListGenericCallback: ArrayListGenericCallback<HistoryInDataModel>) {

        val retrofit = RetrofitGenerater().build(false).create(HistoryService::class.java)
        val call = retrofit?.getData(Gson().toJson(SearchCitizenModel(userId.toString())))
        call?.enqueue(object : Callback<HistoryRootModel> {
            override fun onFailure(call: Call<HistoryRootModel>, t: Throwable) {
                arrayListGenericCallback.onFailure(t.message)
            }

            override fun onResponse(call: Call<HistoryRootModel>, response: Response<HistoryRootModel>) {
                when {
                    response.code() == 200 -> {
                        val logModel: HistoryRootModel? = response.body()
                        if (logModel != null) {
                            arrayListGenericCallback.onResponse(logModel.data.data)
                        }
                    }
                    else -> {
                        response.errorBody()
                    }
                }
            }
        })
    }

    fun openScanQRCode(
        activity: Activity, typeCheckIn: String?, onDisableBack : Boolean ,
        checkInTELCallBack: CheckInTELCallBack
    ) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, ScanQrActivity::class.java)
        intent.putExtras(
            Bundle().apply {
                putString("type", typeCheckIn)
                putBoolean("disable", onDisableBack)
            }
        )
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL) // confirm you not from other activity
    }

    fun openMainScanQrCode(activity: Activity, checkInTELCallBack: CheckInTELCallBack) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, MainScanQrActivity::class.java)
        activity.startActivity(intent)
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
                        checkInTELCallBack?.onCheckInSuccess(data.getStringExtra("result") ?: "")
                    }
                Activity.RESULT_CANCELED -> checkInTELCallBack?.onCancel()
                else -> checkInTELCallBack?.onCheckInFailure(" Fail in ActivityResult ")
            }
        }
    }

}