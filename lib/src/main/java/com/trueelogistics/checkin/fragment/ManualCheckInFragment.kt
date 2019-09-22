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
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.ScanQrActivity
import com.trueelogistics.checkin.api.repository.CheckInRepository
import com.trueelogistics.checkin.enums.CheckInErrorType
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.model.HubInDataModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_manaul_checkin.*

class ManualCheckInFragment : Fragment() {

    companion object {
        const val TAG = "ManualCheckInFragment"
        fun newInstance(bundle: Bundle? = null): ManualCheckInFragment {
            val fragment = ManualCheckInFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val checkInResponse = CheckInRepository.instance
    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
                R.layout.fragment_manaul_checkin,
                container,
                false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingData()
    }

    fun bindingData() {
        back_page.setOnClickListener { activity?.onBackPressed() }
        fixTypeView(arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString())
        var hubId = ""
        checkInHub.setOnClickListener {
            activity?.supportFragmentManager?.also { supportFragmentManager ->
                StockDialogFragment().apply {
                    this.setOnItemLocationClick { hubInDataModel ->
                        setView(hubInDataModel)
                        hubId = hubInDataModel._id.toString()
                    }
                }.show(supportFragmentManager, StockDialogFragment.TAG)
            }
        }
        confirm.setOnClickListener {
            checkLocation(arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString(), hubId)
        }
    }

    private fun checkLocation(type: String, hub_id: String) {

        val loadingDialog = ProgressDialog.show(
                context,
                "$type Processing",
                "please wait..."
        )

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
                            loadingDialog.dismiss()
                            if (location?.isFromMockProvider == false) {
                                postCheckIn(location, type, hub_id)
                            } else {
                                openDialogMockLocation()
                            }
                        }
            } else {
                showToastMessage(CheckInErrorType.PERMISSION_DENIED_ERROR.message)
            }
        }
    }

    fun postCheckIn(location: Location, type: String, hubID: String) {

        val loadingDialog = ProgressDialog.show(
                context,
                "$type Processing",
                "please wait..."
        )

        checkInResponse.postCheckIn(
                arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString(),
                type, "",
                hubID,
                location.latitude.toString(),
                location.longitude.toString()
        ).subscribe({
            loadingDialog?.dismiss()
            when {
                it.code() == 200 -> {
                    scanCompleteOpenDialogSuccess()
                }
                it.code() == 400 -> {
                    scanErrorBadRequest()
                }
                else -> {
                    errorScan()
                }
            }
        }, {
            // error
            loadingDialog?.dismiss()
            errorScan()
        }).addTo(compositeDisposable)
    }

    fun openDialogMockLocation() {
        activity?.supportFragmentManager?.also {
            MockDialogFragment()
                    .show(
                            it,
                            MockDialogFragment.TAG
                    )
        }
    }

    fun scanCompleteOpenDialogSuccess() {
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            SuccessDialogFragment.newInstance(arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString())
                    .show(
                            supportFragmentManager,
                            SuccessDialogFragment.TAG
                    )
        } ?: run {
            scanCompleteNotOpenDialogSuccess()
        }
    }

    fun scanCompleteNotOpenDialogSuccess() {
        activity?.setResult(
                Activity.RESULT_OK,
                Intent(activity, CheckInTEL::class.java).putExtras(
                        Bundle().apply {
                            this.putString(CheckInTEL.KEY_RESULT_CHECK_IN_TEL, "success")
                        }
                ))
        activity?.finish()
    }

    fun scanErrorBadRequest() {
        activity?.supportFragmentManager?.also { supportFragmentManager ->
            OldQrDialogFragment().show(
                    supportFragmentManager,
                    OldQrDialogFragment.TAG
            )
        }
        errorScan()
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

    fun errorScan() {
        showToastMessage("ไม่สามารถสแกนเพื่อระบุตำแหน่งได้")
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

    fun showToastMessage(message: String?) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
        ).show()
    }
}
