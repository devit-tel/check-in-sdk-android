package com.trueelogistics.checkin.activity

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShakeActivity : AppCompatActivity() {

    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var REQUESTING_LOCATION_UPDATES_KEY = "101"
    private var requestingLocationUpdates: Boolean = true
    private lateinit var localLocation: Location
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        FirebaseApp.initializeApp(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                localLocation = location
            }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    localLocation = location
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ShakeDetector.start()
        ShakeDetector.create(this) {
            startLocationUpdates()
            val ref = FirebaseDatabase.getInstance().getReference("driverLocation")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    ref.child("latitude").setValue(localLocation.latitude)
                    ref.child("longitude").setValue(localLocation.longitude)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ShakeActivity,
                        " Fail to set database !!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            getManagerFirebase(localLocation)
        }
        ShakeDetector.destroy()
        updateValuesFromBundle(savedInstanceState)
        startLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest(),
            LocationCallback(),
            null
        )
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }

    }

    private fun getManagerFirebase(location: Location) {
        val managerRef = FirebaseDatabase.getInstance().getReference("managerLocation")
        managerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val latDriver = dataSnapshot.child("latitude").value.toString()
                val longDriver = dataSnapshot.child("longitude").value.toString()
                val managerLocation = Location(LocationManager.GPS_PROVIDER)
                managerLocation.latitude = latDriver.toDouble()
                managerLocation.longitude = longDriver.toDouble()
                val distance: Float? = location.distanceTo(managerLocation)
                if (distance != null) {
                    if (distance < 500)
                        Toast.makeText(
                            this@ShakeActivity,
                            " distance so close == $distance",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(
                            this@ShakeActivity,
                            " out of range == $distance",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ShakeActivity,
                    " Fail to get database !!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
