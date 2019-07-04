package com.trueelogistics.checkin.shakeit


import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trueelogistics.checkin.R

class ShakeFineFragment : Fragment() {
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var REQUESTING_LOCATION_UPDATES_KEY = "101"
    private var requestingLocationUpdates: Boolean = true
    private lateinit var localLocation: Location
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for activity fragment
        return inflater.inflate(R.layout.fragment_shake_fine, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let{
            FirebaseApp.initializeApp(it)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        ShakeDetector.start()
        ShakeDetector.create(activity) {
            startLocationUpdates()
            val ref = FirebaseDatabase.getInstance().getReference("driverLocation")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    ref.child("latitude").setValue(localLocation.latitude)
                    ref.child("longitude").setValue(localLocation.longitude)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        activity,
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
    }
//    override fun onSaveInstanceState(outState: Bundle?) {
//        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
//        super.onSaveInstanceState(outState)
//    }

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
                            activity,
                            " distance so close == $distance",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(
                            activity,
                            " out of range == $distance",
                            Toast.LENGTH_SHORT
                        ).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    activity,
                    " Fail to get database !!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
