package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.model.ScanRootModel
import com.trueelogistics.checkin.service.RetrofitGenerater
import com.trueelogistics.checkin.service.ScanQrService
import kotlinx.android.synthetic.main.fragment_scan_qrcode.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanQrFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_qrcode, container, false)
    }

    companion object {
        var isScan = true
        var cancelFirstCheckIn = false
        const val TYPE_KEY = "TYPE_KEY"
        const val CHECK_DISABLE = "CHECK_DISABLE"
        fun newInstance(type: String, checkDisble: Boolean): ScanQrFragment {
            val fragment = ScanQrFragment()
            val bundle = Bundle().apply {
                putString("TYPE_KEY", type)
                putBoolean("CHECK_DISABLE", checkDisble)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private val callback = object : BarcodeCallback {
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {  // do noting in action
        }

        override fun barcodeResult(result: BarcodeResult) {
            result.text.also {
                checkLocation(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDisableBackPage()
        type_check_in.text = when ( arguments?.getString(TYPE_KEY).toString() ) {
            CheckInTELType.CheckIn.value -> {
                getString(R.string.full_checkin_text)
            }
            CheckInTELType.CheckBetween.value -> {
                getString(R.string.full_check_between_text)
            }
            CheckInTELType.CheckOut.value -> {
                getString(R.string.full_checkout_text)
            }
            else -> {
                arguments?.getString(TYPE_KEY).toString()
            }
        }
        scanner_fragment?.setStatusText("")
        scanner_fragment?.decodeContinuous(callback)
        back_page.setOnClickListener {
            val intent = Intent(activity, CheckInTEL::class.java)
            CheckInTEL.checkInTEL?.onActivityResult(
                1750,
                Activity.RESULT_CANCELED, intent
            )
            activity?.onBackPressed()
        }
        self_checkin.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment,
                ManualCheckInFragment.newInstance(arguments?.getString(TYPE_KEY).toString())
            )?.addToBackStack(ManualCheckInFragment::class.java.name)?.commit()
        }
    }

    private fun setDisableBackPage() {
        val check = arguments?.getBoolean(CHECK_DISABLE)
        if (check == true)
            back_page.visibility = View.GONE
    }

    private fun checkLocation(result: String) {
        //start dialog and stop camera
        if (isScan) {
            isScan = false
            val loadingDialog = ProgressDialog.show(context, "Checking Qr code", "please wait...", true, false)
            val retrofit = RetrofitGenerater().build(true).create(ScanQrService::class.java)
            val type = arguments?.getString(TYPE_KEY).toString()
            var fusedLocationClient: FusedLocationProviderClient
            var latitude: Double
            var longitude: Double
            activity?.let { activity ->
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                if (ContextCompat.checkSelfPermission(
                        activity, Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation
                        ?.addOnSuccessListener { location: Location? ->
                            if (location?.isFromMockProvider == false) {
                                latitude = location.latitude
                                longitude = location.longitude
                                val call =
                                    retrofit?.getData(type, result, null, latitude.toString(), longitude.toString())
                                call?.enqueue(object : Callback<ScanRootModel> {
                                    val intent = Intent(activity, CheckInTEL::class.java)
                                    override fun onFailure(call: Call<ScanRootModel>, t: Throwable) {
                                        //stop dialog and start camera
                                        loadingDialog?.dismiss()
                                        isScan = true

                                        intent.putExtras(
                                            Bundle().apply {
                                                putString("error", t.message)
                                            }
                                        )
                                        CheckInTEL.checkInTEL?.onActivityResult(
                                            1750,
                                            0, intent
                                        )
                                        activity.finish()
                                    }

                                    override fun onResponse(
                                        call: Call<ScanRootModel>,
                                        response: Response<ScanRootModel>
                                    ) {
                                        //stop dialog
                                        loadingDialog?.dismiss()

                                        when {
                                            response.code() == 200 -> {
                                                onPause()
                                                intent.putExtras(
                                                    Bundle().apply {
                                                        putString("result", "success")
                                                    }
                                                )
                                                CheckInTEL.checkInTEL?.onActivityResult(
                                                    1750,
                                                    Activity.RESULT_OK, intent
                                                )
                                                SuccessDialogFragment.newInstance(type)
                                                    .show(activity.supportFragmentManager, "show")
                                            }
                                            response.code() == 400 -> {
                                                intent.putExtras(
                                                    Bundle().apply {
                                                        putString("error", "This QRCode Used")
                                                    }
                                                )
                                                CheckInTEL.checkInTEL?.onActivityResult(
                                                    1750,
                                                    0, intent
                                                )
//                                                OldQrDialogFragment().fail_text.text = getString(R.string.qrUsed)
                                                OldQrDialogFragment().show(activity.supportFragmentManager, "show")
                                                cancelFirstCheckIn = true
                                                back_page.visibility = View.VISIBLE
                                            }
                                            else -> {
                                                intent.putExtras(
                                                    Bundle().apply {
                                                        putString("error", "response.code() = "+response.code())
                                                    }
                                                )
                                                CheckInTEL.checkInTEL?.onActivityResult(
                                                    1750,
                                                    0, intent
                                                )
                                                response.errorBody()
                                                isScan = true
                                            }
                                        }

                                    }
                                })
                            } else {
                                loadingDialog?.dismiss()
                                MockDialogFragment().show(activity.supportFragmentManager, "show")
                                val intent = Intent(activity, CheckInTEL::class.java)
                                intent.putExtras(
                                    Bundle().apply {
                                        putString("error", "GPS is Mock !!")
                                    }
                                )
                                CheckInTEL.checkInTEL?.onActivityResult(
                                    1750,
                                    0, intent
                                )
                                activity.recreate()
                            }
                        }
                }
                else{
                    val intent = Intent(activity, CheckInTEL::class.java)
                    intent.putExtras(
                        Bundle().apply {
                            putString("error", "Permission GPS Denied!!")
                        }
                    )
                    CheckInTEL.checkInTEL?.onActivityResult(
                        1750,
                        0, intent
                    )
                }
            }
        }
    }

    override fun onResume() {
        scanner_fragment?.resume()
        super.onResume()
    }

    override fun onPause() {
        scanner_fragment?.pause()
        super.onPause()
    }
}
