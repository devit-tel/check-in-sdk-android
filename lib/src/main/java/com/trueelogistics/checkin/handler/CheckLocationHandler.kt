package com.trueelogistics.checkin.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Handler
import android.os.Looper
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class CheckLocationHandler {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 545
        @get:Synchronized
        val instance = CheckLocationHandler()
    }

    interface CheckInLocationListener {
        fun onLocationUpdate(location: Location)
        fun onLocationTimeout()
        fun onLocationError()
    }

    private var checkInLocationListener: CheckInLocationListener? = null
    private var isRequestLocation = false
    private var isResponseLocation = false
    private var googleApiClient: GoogleApiClient? = null
    private var client: FusedLocationProviderClient? = null
    var handler = Handler()
    private val callbackLocation = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.also { location ->
                if (isRequestLocation) {
                    updateLocation(location)
                }
            }
        }
    }
    private val callbackTimeOut = Runnable {
        this.checkInLocationListener?.onLocationTimeout()
    }

    fun requestLocation(activity: Activity, checkInLocationListener: CheckInLocationListener) {
        this.checkInLocationListener = checkInLocationListener

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient
                    .Builder(activity)
                    .addApi(LocationServices.API)
                    .build()
        }
        isRequestLocation = true
        try {
            handler.removeCallbacks(callbackTimeOut)
            handler.postDelayed(callbackTimeOut, 10000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        googleApiClient?.connect()
        checkLocationSettingsRequest(activity)
    }

    @SuppressLint("MissingPermission")
    fun getLocation(activity: Activity) {
        if (!isRequestLocation) {
            return
        }
        client = LocationServices.getFusedLocationProviderClient(activity).apply {
            lastLocation.addOnFailureListener {
                checkInLocationListener?.onLocationError()
                checkLocationSettingsRequest(activity)
            }
        }
        client?.requestLocationUpdates(
                LocationRequest().apply {
                    this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    this.interval = 1000
                    this.fastestInterval = 1000
                },
                callbackLocation,
                Looper.getMainLooper()
        )
    }

    private fun checkLocationSettingsRequest(activity: Activity) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest
                .PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client: SettingsClient = LocationServices
                .getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client
                .checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            if (locationSettingsResponse
                            .locationSettingsStates
                            .isLocationPresent
            ) {
                getLocation(activity)
            }
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                            activity,
                            REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    fun updateLocation(location: Location) {
        try {
            handler.removeCallbacks(callbackTimeOut)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isResponseLocation = true
        isRequestLocation = false
        client?.removeLocationUpdates(callbackLocation)
        checkInLocationListener?.onLocationUpdate(location)
    }
}