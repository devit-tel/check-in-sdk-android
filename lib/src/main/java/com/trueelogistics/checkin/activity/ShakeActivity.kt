package com.trueelogistics.checkin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInErrorType
import com.trueelogistics.checkin.fragment.ShakeFindingFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.model.HubInDataModel

class ShakeActivity : AppCompatActivity() {


    interface ShakeCallback {
        fun onFound(hubId: String? = null, hubName: String? = null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)
        KotlinPermissions.with(this)
                .permissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).onAccepted {
                    supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_shake, ShakeFindingFragment()).commit()
                }.onDenied {
                    setResult(Activity.RESULT_OK, Intent(this, CheckInTEL::class.java).apply {
                        this.putExtras(
                                Bundle().apply {
                                    putString(
                                            CheckInTEL.KEY_ERROR_CHECK_IN_TEL,
                                            CheckInErrorType.PERMISSION_DENIED_ERROR.message
                                    )
                                }
                        )
                    })
                    finish()
                }.ask()
    }

    fun itemShake(shakeListener: ShakeCallback) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        fusedLocationClient.let {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener(this) { location: Location? ->
                        CheckInTEL.checkInTEL?.hubGenerator(
                                object : ArrayListGenericCallback<HubInDataModel> {
                                    override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                                        successShakeCheckIn(dataModel, location, shakeListener)
                                    }

                                    override fun onFailure(message: String?) {
                                        errorShakeCheckIn(" get nameHub onFailure : $message ")
                                    }
                                })
                    }
        }
    }

    fun successShakeCheckIn(
            dataModel: ArrayList<HubInDataModel>?,
            location: Location?,
            shakeListener: ShakeCallback
    ) {
        dataModel?.forEach {
            val hubLocation = Location(LocationManager.GPS_PROVIDER)
            hubLocation.latitude = it.latitude ?: 0.0
            hubLocation.longitude = it.longitude ?: 0.0
            val distance: Float? = location?.distanceTo(hubLocation)
            if (distance != null) {
                if (distance < 500) {
                    shakeListener.onFound(it._id, it.locationName)
                } else {
                    // distance not find hub
                }
            } else {
                // distance null
            }
        }
    }

    fun errorShakeCheckIn(message: String) {
        setResult(
                Activity.RESULT_OK,
                Intent(this, CheckInTEL::class.java).apply {
                    this.putExtras(
                            Bundle().apply {
                                putString(
                                        CheckInTEL.KEY_ERROR_CHECK_IN_TEL,
                                        message
                                )
                            }
                    )
                })
        finish()
    }

    override fun onBackPressed() {
        ShakeFindingFragment.showView = true
        finish()
    }
}
