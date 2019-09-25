package com.trueelogistics.checkin.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.util.Base64
import com.trueelogistics.checkin.activity.MainScanQrActivity
import com.trueelogistics.checkin.activity.NearByActivity
import com.trueelogistics.checkin.activity.ScanQrActivity
import com.trueelogistics.checkin.activity.ShakeActivity
import com.trueelogistics.checkin.api.RetrofitGenerator
import com.trueelogistics.checkin.api.repository.CheckInRepository
import com.trueelogistics.checkin.api.service.HubService
import com.trueelogistics.checkin.enums.CheckInErrorType
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.enums.EnvironmentType
import com.trueelogistics.checkin.extensions.format
import com.trueelogistics.checkin.extensions.formatISO
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.HistoryInDataModel
import com.trueelogistics.checkin.model.HistoryTodayModel
import com.trueelogistics.checkin.model.HubInDataModel
import com.trueelogistics.checkin.model.HubRootModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

class CheckInTEL {
    companion object {

        const val KEY_REQUEST_CODE_CHECK_IN_TEL = 1750
        const val KEY_RESULT_CHECK_IN_TEL = "KEY_RESULT_CHECK_IN_TEL"
        const val KEY_ERROR_CHECK_IN_TEL = "KEY_ERROR_CHECK_IN_TEL"
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

    private var checkInTELCallBack: CheckInTELCallBack? = null
    private var repository = CheckInRepository.instance
    private var compositeDisposable = CompositeDisposable()

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
        ).metaData.getString("com.trueelogistics.API_KEY")
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

    fun hubGenerator(listener: ArrayListGenericCallback<HubInDataModel>) {
        val retrofit = RetrofitGenerator().build().create(HubService::class.java)
        val call = retrofit.getData()
        call.enqueue(object : Callback<HubRootModel> {
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
        repository.postCreateQRCode(
                qrCodeCreateBy,
                locationId,
                latitude,
                longitude
        ).subscribe({
            if (it.code() == 200) {
                it?.body()?.let { genQRCode ->
                    val timeLatest = genQRCode
                            .data
                            .updatedAt
                            .toString()
                    listener.onResponse(
                            genQRCode.data.locationId.locationName,
                            genQRCode.data.qrcodeUniqueKey.toString(),
                            timeLatest.formatISO("HH:mm")
                    )
                } ?: run {
                    listener.onFailure("Data empty")
                }
            } else {
                listener.onFailure(it?.message())
            }
        }, {
            // error
            listener.onFailure(it.message)
        }).addTo(compositeDisposable)
    }

    fun getLastCheckInHistory(typeCallback: TypeCallback) {
        repository.getLastCheckInHistory().subscribe({
            if (it.code() == 200) {
                it.body()?.let { historyTodayModel ->
                    getCheckOverTime(historyTodayModel, typeCallback)
                } ?: run {
                    typeCallback.onFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                }
            } else {
                typeCallback.onFailure(it.message())
            }
        }, {
            // error
            typeCallback.onFailure(it.message)
        }).addTo(compositeDisposable)
    }

    fun getCheckOverTime(historyTodayModel: HistoryTodayModel, typeCallback: TypeCallback) {
        repository.getCheckInOverTime().subscribe({
            if (it.code() == 200) {
                it.body()?.let { checkOverTimeModel ->
                    if (historyTodayModel.data.size > 0) {
                        val lastDatePick = historyTodayModel.data.last()
                        if (lastDatePick
                                        .updatedAt
                                        ?.formatISO("yyyy-MM-dd") ==
                                Date()
                                        .format("yyyy-MM-dd")
                        ) {
                            typeCallback.onResponse(
                                    lastDatePick.eventType ?: "",
                                    true
                            )
                        } else {
                            typeCallback.onResponse(
                                    CheckInTELType.CheckOut.value,
                                    false
                            )
                        }
                    } else {
                        if (checkOverTimeModel.data) {
                            typeCallback.onResponse(
                                    CheckInTELType.CheckOutOverTime.value,
                                    false
                            )
                        } else {
                            typeCallback.onResponse(
                                    CheckInTELType.CheckOut.value,
                                    false
                            )
                        }
                    }
                } ?: run {
                    typeCallback.onFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                }
            } else {
                typeCallback.onFailure(it.message())
            }
        }, {
            // error
            typeCallback.onFailure(it.message)
        }).addTo(compositeDisposable)
    }

    fun getHistory(arrayListGenericCallback: ArrayListGenericCallback<HistoryInDataModel>) {
        repository.getLastCheckInHistory().subscribe({
            if (it.code() == 200) {
                it.body()?.data?.let { historyList ->
                    if (!historyList.isNullOrEmpty()) {
                        arrayListGenericCallback.onResponse(historyList)
                    } else {
                        arrayListGenericCallback.onFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                    }
                } ?: run {
                    arrayListGenericCallback.onFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                }
            } else {
                arrayListGenericCallback.onFailure(it.message())
            }
        }, {
            // error
            arrayListGenericCallback.onFailure(it.message)
        }).addTo(compositeDisposable)
    }

    fun getAllHistory(
            page: Int,
            limit: Int,
            arrayListGenericCallback: ArrayListGenericCallback<HistoryInDataModel>
    ) {

        repository.getHistory().subscribe({
            if (it.code() == 200) {
                it.body()?.data?.data?.let { historyList ->
                    if (historyList.isNullOrEmpty()) {
                        arrayListGenericCallback.onResponse(historyList)
                    } else {
                        arrayListGenericCallback.onFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                    }
                } ?: run {
                    arrayListGenericCallback.onFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                }
            } else {
                arrayListGenericCallback.onFailure(it.message())
            }
        }, {
            // error
            arrayListGenericCallback.onFailure(it.message)
        }).addTo(compositeDisposable)
    }

    fun openScanQRCode(
            activity: Activity,
            typeCheckIn: String?,
            onDisableBack: Boolean,
            checkInTELCallBack: CheckInTELCallBack
    ) {
        this.checkInTELCallBack = checkInTELCallBack
        ScanQrActivity.startActivityForResult(
                activity, KEY_REQUEST_CODE_CHECK_IN_TEL,
                typeCheckIn,
                onDisableBack
        )
    }

    fun openMainScanQrCode(
            activity: Activity,
            checkInTELCallBack: CheckInTELCallBack
    ) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, MainScanQrActivity::class.java)
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL)
    }

    fun openNearBy(
            activity: Activity,
            checkInTELCallBack: CheckInTELCallBack
    ) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, NearByActivity::class.java)
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL)
    }

    fun openShake(
            activity: Activity,
            checkInTELCallBack: CheckInTELCallBack
    ) {
        this.checkInTELCallBack = checkInTELCallBack
        val intent = Intent(activity, ShakeActivity::class.java)
        activity.startActivityForResult(intent, KEY_REQUEST_CODE_CHECK_IN_TEL)

    }

    fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        if (requestCode == KEY_REQUEST_CODE_CHECK_IN_TEL) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (data != null) {
                        if (!data.getStringExtra(KEY_RESULT_CHECK_IN_TEL).isNullOrEmpty()) {
                            checkInTELCallBack?.onCheckInSuccess(
                                    data.getStringExtra(
                                            KEY_RESULT_CHECK_IN_TEL
                                    ) ?: ""
                            )
                        } else if (!data.getStringExtra(KEY_ERROR_CHECK_IN_TEL).isNullOrEmpty()) {
                            checkInTELCallBack?.onCheckInFailure(
                                    data.getStringExtra(
                                            KEY_ERROR_CHECK_IN_TEL
                                    ) ?: ""
                            )
                        } else {
                            checkInTELCallBack?.onCheckInFailure(CheckInErrorType.RESULT_INVALID_ERROR.message)
                        }
                    } else {
                        checkInTELCallBack?.onCheckInFailure(CheckInErrorType.DATA_EMPTY_ERROR.message)
                    }
                }
                else -> {
                    checkInTELCallBack?.onCancel()
                }
            }
        }
    }
}