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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.ScanQrActivity
import com.trueelogistics.checkin.api.repository.CheckInRepository
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.extensions.replaceFragmentInActivity
import com.trueelogistics.checkin.handler.CheckInTEL
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_scan_qrcode.*

class ScanQrFragment : Fragment() {

    companion object {
        const val TAG = "ScanQrFragment"
        var isScan = true
        var cancelFirstCheckIn = false
        fun newInstance(bundle: Bundle? = Bundle()): ScanQrFragment {
            val fragment = ScanQrFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val checkInResponse = CheckInRepository.instance
    private var compositeDisposable = CompositeDisposable()

    private val callback = object : BarcodeCallback {
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        }

        override fun barcodeResult(result: BarcodeResult) {
            result.text.also {
                checkLocation(it)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_qrcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingData()
    }

    fun bindingData() {
        setDisableBackPage()
        type_check_in.text = when (arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString()) {
            CheckInTELType.CheckIn.value -> {
                getString(CheckInTELType.CheckIn.res)
            }
            CheckInTELType.CheckBetween.value -> {
                getString(CheckInTELType.CheckBetween.res)
            }
            CheckInTELType.CheckOut.value -> {
                getString(CheckInTELType.CheckOut.res)
            }
            CheckInTELType.CheckOutOverTime.value -> {
                getString(CheckInTELType.CheckOutOverTime.res)
            }
            else -> {
                arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString()
            }
        }
        scanner_fragment?.setStatusText("")
        scanner_fragment?.decodeContinuous(callback)
        back_page.setOnClickListener { activity?.onBackPressed() }
        self_checkin.setOnClickListener { openScanManual() }
    }

    private fun setDisableBackPage() {
        if (arguments?.getBoolean(ScanQrActivity.KEY_DISABLE_BACK, false) == true) {
            disableBack()
        }
    }

    private fun checkLocation(result: String) {
        if (isScan) {
            isScan = false
            var fusedLocationClient: FusedLocationProviderClient
            activity?.let { activity ->
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                if (ContextCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation?.addOnSuccessListener { location: Location? ->
                        if (location?.isFromMockProvider == false) {
                            postCheckIn(result, location.latitude, location.longitude)
                        } else {
                            openDialogMockLocation()
                        }
                    }
                } else {
                    showToastMessage("กรุณาเปิดใช้สิทธิเพื่อระบุตำแหน่ง")
                    activity.finishAffinity()
                }
            }
        }
    }

    override fun onResume() {
        scanner_fragment?.resume()
        super.onResume()
    }

    fun postCheckIn(
            result: String,
            latitude: Double,
            longitude: Double
    ) {
        val loadingDialog = ProgressDialog.show(
                context,
                "Checking Qr code",
                "please wait...",
                true,
                false
        )
        checkInResponse.postCheckIn(
                arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString(),
                result,
                null,
                latitude.toString(),
                longitude.toString()
        ).subscribe({
            loadingDialog?.dismiss()
            when {
                it.code() == 200 -> {
                    onPause()
                    scanCompleteOpenDialogSuccess()
                }
                it.code() == 400 -> {
                    scanErrorBadRequest()
                }
                else -> {
                    errorScan()
                    isScan = true
                }
            }
        }, {
            // error
            loadingDialog?.dismiss()
            errorScan()
            isScan = true
        }).addTo(compositeDisposable)
    }

    override fun onPause() {
        scanner_fragment?.pause()
        super.onPause()
    }

    fun openScanManual() {
        replaceFragmentInActivity(
                R.id.fragment,
                ManualCheckInFragment.newInstance(arguments),
                ManualCheckInFragment.TAG,
                true
        )
    }

    fun openDialogMockLocation() {
        activity?.supportFragmentManager?.also {
            MockDialogFragment().show(
                    it,
                    MockDialogFragment.TAG
            )
        }
    }

    fun showToastMessage(message: String?) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
        ).show()
    }

    fun errorScan() {
        showToastMessage("ไม่สามารถสแกนเพื่อระบุตำแหน่งได้")
        enableBack()
    }

    fun disableBack() {
        this.back_page.visibility = View.GONE
    }

    fun enableBack() {
        this.back_page.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}
