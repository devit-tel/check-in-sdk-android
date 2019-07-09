package com.trueelogistics.example


import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kotlinpermissions.KotlinPermissions
import kotlinx.android.synthetic.main.fragment_shake.*

class ShakeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shake, container, false)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        toolbar.setOnClickListener {
            mainActivity.actionToolbar()
        }
        activity?.let { fragActivity ->
            val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(fragActivity, permissions, 0)
            KotlinPermissions.with(fragActivity) // where this is an FragmentActivity instance --> KotlinPermissions.with
                .permissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).onAccepted {
                    val fusedLocationClient: FusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(fragActivity)
                    ShakeDetector.start()
                    ShakeDetector.create(fragActivity) {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                val latitude = location?.latitude.toString()
                                val longitude = location?.longitude.toString()

                                FirebaseApp.initializeApp(fragActivity)

                                val ref = FirebaseDatabase.getInstance().getReference("driverLocation")
                                ref.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        ref.child("latitude").setValue(latitude)
                                        ref.child("longitude").setValue(longitude)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            activity,
                                            " Fail to set database !!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                                location?.let {
                                    getManagerFirebase(it)
                                }
                            }
                        object : CountDownTimer(5000, 1000) { // 1 second to onTick & 1 minit to onFinish

                            override fun onTick(millisUntilFinished: Long) {

                            }

                            override fun onFinish() {

                            }
                        }.start()
                    }
                    ShakeDetector.destroy()
                }.ask()
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
                activity?.let {
                    if (distance != null) {
                        if (distance < 500)
                            Toast.makeText(
                                it,
                                " distance so close == $distance",
                                Toast.LENGTH_SHORT
                            ).show()
                        else
                            Toast.makeText(
                                it,
                                " out of range == $distance",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
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

    fun getDriverFirebase(location: Location) {
        val managerRef = FirebaseDatabase.getInstance().getReference("driverLocation")
        managerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val latDriver = dataSnapshot.child("latitude").value.toString()
                val longDriver = dataSnapshot.child("longitude").value.toString()
                val driverLocation = Location(LocationManager.GPS_PROVIDER)
                driverLocation.latitude = latDriver.toDouble()
                driverLocation.longitude = longDriver.toDouble()
                val distance: Float? = location.distanceTo(driverLocation)
                activity?.let {
                    if (distance != null) {
                        if (distance < 500)
                            Toast.makeText(
                                it,
                                " distance so close == $distance",
                                Toast.LENGTH_SHORT
                            ).show()
                        else
                            Toast.makeText(
                                it,
                                " out of range == $distance",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
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
