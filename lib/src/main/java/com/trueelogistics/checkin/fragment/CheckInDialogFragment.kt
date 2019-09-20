package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.LocationServices
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.model.GenerateItemHubModel
import com.trueelogistics.checkin.model.ScanRootModel
import com.trueelogistics.checkin.service.RetrofitGenerater
import com.trueelogistics.checkin.service.ScanQrService
import kotlinx.android.synthetic.main.fragment_checkin_dialog.*
import kotlinx.android.synthetic.main.fragment_old_qr_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckInDialogFragment : BottomSheetDialogFragment(){
    var item : GenerateItemHubModel ?= null
    var typeFromLastCheckIn : String ?= null
    var checkinType : String ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_checkin_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hub_name.text = item?.hubName
        var typeCheckIn = typeFromLastCheckIn ?: ""

        fixTypeView(typeCheckIn)
        checkin_pic.setOnClickListener {
            typeCheckIn = CheckInTELType.CheckIn.value
            selectedType(typeCheckIn)
        }
        between_pic.setOnClickListener {
            typeCheckIn = CheckInTELType.CheckBetween.value
            selectedType(typeCheckIn)
        }
        checkout_pic.setOnClickListener {
            typeCheckIn = CheckInTELType.CheckOut.value
            selectedType(typeCheckIn)
        }
        confirm_checkin.setOnClickListener {
            NearByFindingFragment.showView= true
            ShakeFindingFragment.showView = true
            checkLocation(typeCheckIn, item?.hubId ?: " HubID is null ")
        }
        cancel_checkin.setOnClickListener {
            dialog.cancel()
        }


    }

    private fun fixTypeView(type: String) {
        when (type) {
            CheckInTELType.CheckIn.value -> {
                checkin_view.visibility = View.VISIBLE
                between_view.visibility = View.GONE
                checkout_view.visibility = View.GONE
                checkin_pic.setImageResource(R.drawable.ic_checkin_color)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            CheckInTELType.CheckBetween.value , CheckInTELType.CheckIn.value -> {
                checkin_view.visibility = View.GONE
                between_view.visibility = View.VISIBLE
                checkout_view.visibility = View.VISIBLE
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_color)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            else -> {
                checkin_view.visibility = View.GONE
                between_view.visibility = View.GONE
                checkout_view.visibility = View.GONE

            }
        }
    }

    private fun selectedType(type: String) {
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
            else -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
        }
    }

    private fun checkLocation(type: String, hub_id: String) {
        val retrofit = RetrofitGenerater().build(true).create(ScanQrService::class.java)
        val loadingDialog = ProgressDialog.show(context, "$type Processing", "please wait...")
        activity?.let { activity ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
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
                                type, "", hub_id, checkinType
                                , latitude.toString(), longitude.toString()
                            )
                            call.enqueue(object : Callback<ScanRootModel> {
                                val intent = Intent(activity, CheckInTEL::class.java)
                                override fun onFailure(call: Call<ScanRootModel>, t: Throwable) {
                                    //stop dialog and start camera
                                    loadingDialog.dismiss()

                                    intent.putExtras(
                                        Bundle().apply {
                                            putString(CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                , t.message)
                                        }
                                    )
                                    CheckInTEL.checkInTEL?.onActivityResult(
                                        CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                        Activity.RESULT_OK, intent
                                    )
                                    ScanQrFragment.cancelFirstCheckIn = true

                                }

                                override fun onResponse(call: Call<ScanRootModel>, response: Response<ScanRootModel>) {
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
                                                    putString( CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                        , getString(R.string.wrong_locationId))
                                                }
                                            )
                                            CheckInTEL.checkInTEL?.onActivityResult(
                                                CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                                Activity.RESULT_OK, intent
                                            )
                                            OldQrDialogFragment().fail_text.visibility = View.GONE
                                            OldQrDialogFragment().show(activity.supportFragmentManager, "show")
                                        }
                                        else -> {
                                            ScanQrFragment.cancelFirstCheckIn = true
                                            intent.putExtras(
                                                Bundle().apply {
                                                    putString( CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                        , "${response.code()} : ${response.message()}")
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
                                    putString( CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                        , "GPS is Mock !!")
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
                        putString( CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                            , "Permission GPS Denied!!")
                    }
                )
                CheckInTEL.checkInTEL?.onActivityResult(
                    CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                    Activity.RESULT_OK, intent
                )
            }
        }
    }
}
