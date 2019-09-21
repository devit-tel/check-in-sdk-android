package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.model.HubInDataModel
import com.trueelogistics.checkin.model.ScanRootModel
import com.trueelogistics.checkin.api.service.RetrofitGenerater
import com.trueelogistics.checkin.api.service.ScanQrService
import kotlinx.android.synthetic.main.fragment_manaul_checkin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualCheckInFragment : androidx.fragment.app.Fragment() {
    companion object {
        var TYPE_KEY = ""
        fun newInstance(type: String): ManualCheckInFragment {
            val fragment = ManualCheckInFragment()
            val bundle = Bundle().apply {
                putString(TYPE_KEY, type)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manaul_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString(TYPE_KEY).toString()
        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        fixTypeView(type)
        var hubId = ""
        checkInHub.setOnClickListener {
            val stockDialogFragment = StockDialogFragment()
            stockDialogFragment.setOnItemLocationClick {
                setView(it)
                hubId = it._id.toString()
            }
            activity?.supportFragmentManager?.also {
                stockDialogFragment.show(it, "show")
            }
        }
        confirm.setOnClickListener {
            checkLocation(type, hubId)
        }
    }

    private fun checkLocation(type: String, hub_id: String) {
        val retrofit = RetrofitGenerater().build(true)
            .create(ScanQrService::class.java)
        val loadingDialog = ProgressDialog
            .show(context, "$type Processing", "please wait...")
        activity?.let { activity ->
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(activity)
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_COARSE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        if (location?.isFromMockProvider == false) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            val call = retrofit.getData(
                                type, "", hub_id, "MANUAL"
                                , latitude.toString(), longitude.toString()
                            )
                            call.enqueue(object : Callback<ScanRootModel> {
                                val intent = Intent(activity, CheckInTEL::class.java)
                                override fun onFailure(call: Call<ScanRootModel>, t: Throwable) {
                                    //stop dialog and start camera
                                    loadingDialog.dismiss()

                                    intent.putExtras(
                                        Bundle().apply {
                                            putString(CheckInTEL.KEY_ERROR_CHECK_IN_TEL, t.message)
                                        }
                                    )
                                    CheckInTEL.checkInTEL?.onActivityResult(
                                        CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                        Activity.RESULT_OK, intent
                                    )
                                    ScanQrFragment.cancelFirstCheckIn = true

                                }

                                override fun onResponse(
                                    call: Call<ScanRootModel>,
                                    response: Response<ScanRootModel>
                                ) {
                                    //stop dialog
                                    loadingDialog.dismiss()
                                    when {
                                        response.code() == 200 -> {
                                            SuccessDialogFragment.newInstance(type)
                                                .show(activity.supportFragmentManager, "show")
                                        }
                                        response.code() == 400 -> {
                                            onPause()
                                            intent.putExtras(
                                                Bundle().apply {
                                                    putString(
                                                        CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                        , getString(R.string.wrong_locationId)
                                                    )
                                                }
                                            )
                                            CheckInTEL.checkInTEL?.onActivityResult(
                                                CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                                Activity.RESULT_OK, intent
                                            )
                                            OldQrDialogFragment().show(
                                                activity.supportFragmentManager,
                                                "show"
                                            )
                                        }
                                        else -> {
                                            ScanQrFragment.cancelFirstCheckIn = true
                                            intent.putExtras(
                                                Bundle().apply {
                                                    putString(
                                                        CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                        ,
                                                        "${response.code()} : ${response.message()}"
                                                    )
                                                }
                                            )
                                            CheckInTEL.checkInTEL?.onActivityResult(
                                                CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                                Activity.RESULT_OK, intent
                                            )
                                        }
                                    }
                                }
                            })
                        } else {
                            loadingDialog.dismiss()
                            MockDialogFragment().show(activity.supportFragmentManager, "show")
                            val intent = Intent(activity, CheckInTEL::class.java)
                            intent.putExtras(
                                Bundle().apply {
                                    putString(CheckInTEL.KEY_ERROR_CHECK_IN_TEL, "GPS is Mock !!")
                                }
                            )
                            CheckInTEL.checkInTEL?.onActivityResult(
                                CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                Activity.RESULT_OK, intent
                            )
                        }
                    }
            } else {
                val intent = Intent(activity, CheckInTEL::class.java)
                intent.putExtras(
                    Bundle().apply {
                        putString(CheckInTEL.KEY_ERROR_CHECK_IN_TEL, "Permission GPS Denied!!")
                    }
                )
                CheckInTEL.checkInTEL?.onActivityResult(
                    CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                    Activity.RESULT_OK, intent
                )
            }
        }
    }

    private fun setView(item: HubInDataModel) {
        stockName.text = item.locationName
        activity?.let {
            stockName.setTextColor(ContextCompat.getColor(it, R.color.black))
            confirm.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
            confirm.setTextColor(ContextCompat.getColor(it, R.color.white))
        }
        confirm.isEnabled = true
    }

    private fun fixTypeView(type: String) {
        when (type) {
            CheckInTELType.CheckIn.value -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_color)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            CheckInTELType.CheckBetween.value -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_color)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            CheckInTELType.CheckOut.value -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_color)
            }
        }
    }
}
