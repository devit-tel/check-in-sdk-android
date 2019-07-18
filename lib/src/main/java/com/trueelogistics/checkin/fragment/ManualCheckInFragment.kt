package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.model.ScanRootModel
import com.trueelogistics.checkin.model.HubInDataModel
import com.trueelogistics.checkin.service.RetrofitGenerater
import com.trueelogistics.checkin.service.ScanQrService
import kotlinx.android.synthetic.main.fragment_manaul_checkin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualCheckInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manaul_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var type : String? = CheckInTELType.CheckIn.value
        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        checkin_pic.setOnClickListener {
            if (checkin_pic.drawable.constantState == ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_checkin_gray,null)?.constantState )
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_color)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            type = CheckInTELType.CheckIn.value
        }
        between_pic.setOnClickListener {
            if (between_pic.drawable.constantState ==  ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_checkin_gray,null)?.constantState )
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_color)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            type = CheckInTELType.CheckBetween.value
        }
        checkout_pic.setOnClickListener {
            if (checkout_pic.drawable.constantState == ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_checkin_gray,null)?.constantState)
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_color)
            }
            type = CheckInTELType.CheckOut.value
        }
        checkInHub.setOnClickListener {
            val stockDialogFragment  = StockDialogFragment()
            stockDialogFragment.setOnItemLocationClick {
                setView(it)
            }
            stockDialogFragment.show(activity?.supportFragmentManager, "show")
        }
        confirm.setOnClickListener {
            checkLocation(type?:CheckInTELType.CheckIn.value)
        }
    }

    private fun checkLocation(type : String){
        val retrofit = RetrofitGenerater().build(true).create(ScanQrService::class.java)
        val loadingDialog = ProgressDialog.show(context, "Saving History", "please wait...")
        var fusedLocationClient: FusedLocationProviderClient
        var latitude: Double
        var longitude: Double
        activity?.let { activity ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED ) {
                fusedLocationClient.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        if(location?.isFromMockProvider == false) {
                            latitude = location.latitude
                            longitude = location.longitude
                            val call = retrofit?.getData(type, "", latitude.toString(),longitude.toString())
                            call?.enqueue(object : Callback<ScanRootModel> {
                                override fun onFailure(call: Call<ScanRootModel>, t: Throwable) {
                                    //stop dialog and start camera
                                    loadingDialog.dismiss()
                                }
                                override fun onResponse(call: Call<ScanRootModel>, response: Response<ScanRootModel>) {
                                    //stop dialog
                                    loadingDialog.dismiss()
                                    when {
                                        response.code() == 200 -> {
//                            response.body()
                                            SuccessDialogFragment().show(activity.supportFragmentManager, "show")
                                        }
                                        response.code() == 400 -> {
                                            onPause()
                                            OldQrDialogFragment().show(activity.supportFragmentManager, "show")
                                        }
                                        response.code() == 500 -> {
                                            Toast.makeText(activity,"Server Error",Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                        else -> {
                                            response.errorBody()
                                        }
                                    }
                                }
                            })
                        }
                        else{
                            loadingDialog.dismiss()
                            MockDialogFragment().show(activity.supportFragmentManager, "show")
                        }
                    }
            }
        }
    }

    private fun setView(item: HubInDataModel){
        stockName.text = item.locationName
        activity?.let {
            stockName.setTextColor(ContextCompat.getColor(it, R.color.black))
            confirm.setBackgroundColor(ContextCompat.getColor(it,R.color.purple))
            confirm.setTextColor(ContextCompat.getColor(it,R.color.white))
        }
        confirm.isEnabled = true
    }
}
