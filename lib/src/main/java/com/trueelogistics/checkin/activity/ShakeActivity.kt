package com.trueelogistics.checkin.activity

import android.Manifest
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.model.HubInDataModel

class ShakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)
        KotlinPermissions.with(this) // where this is an FragmentActivity instance --> KotlinPermissions.with
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).onAccepted {
                onShakeDetect()
            }.onDenied {

            }.ask()

    }

    private fun onShakeDetect() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

            }
    }

    private fun checkLocation( shakeListner: ShakeCallback ) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                CheckInTEL.checkInTEL?.hubGenerater(
                    object : ArrayListGenericCallback<HubInDataModel> {
                        override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                            dataModel?.forEach {
                                val hubLocation = Location(LocationManager.GPS_PROVIDER)
                                hubLocation.latitude = it.latitude ?: 0.0
                                hubLocation.longitude = it.longitude ?: 0.0
                                val distance: Float? = location?.distanceTo(hubLocation)
                                if (distance != null) {
                                    if (distance < 500)
                                        shakeListner.onFound(it._id, it.locationName)

                                }
                            }
                        }

                        override fun onFailure(message: String?) {

                        }

                    })
                location?.let {

                }
            }

    }

    interface ShakeCallback {
        fun onFound(hubId: String? = "", hubName: String? = "")
        fun onLost(hubId: String? = "", hubName: String? = "")
    }
}
