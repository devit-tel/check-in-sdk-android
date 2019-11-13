package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.BaseDialogProgress
import com.trueelogistics.checkin.activity.ScanQrActivity
import com.trueelogistics.checkin.api.repository.CheckInRepository
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.enums.HistoryStaffType
import com.trueelogistics.checkin.extensions.replaceFragmentInActivity
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.CheckLocationHandler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_scan_qrcode.*

class ScanQrFragment : Fragment() {

    companion object {
        const val TAG = "ScanQrFragment"
        const val REQUESTCODE_DIALOG = 352
        var cancelFirstCheckIn = false
        fun newInstance(bundle: Bundle? = Bundle()): ScanQrFragment {
            val fragment = ScanQrFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val checkInResponse = CheckInRepository.instance
    private var compositeDisposable = CompositeDisposable()
    private var isScan = true
    private var baseDialogProcess: BaseDialogProgress? = null
    private var isShowDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.also {
            baseDialogProcess = BaseDialogProgress(it)
        }
    }

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
        isScan = true
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
            activity?.let { activity ->
                if (ContextCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                ) {
                    baseDialogProcess?.show()
                    CheckLocationHandler.instance.requestLocation(activity, object : CheckLocationHandler.CheckInLocationListener {
                        override fun onLocationUpdate(location: Location) {
                            baseDialogProcess?.dismiss()
                            if (!location.isFromMockProvider) {
                                postCheckIn(result, location.latitude, location.longitude)
                            } else {
                                openDialogMockLocation()
                            }
                        }

                        override fun onLocationTimeout() {
                            baseDialogProcess?.dismiss()
                            showToastMessage("ไม่สามารถระบุตำแหน่งได้")
                            waitEnableScan()
                        }

                        override fun onLocationError() {
                            baseDialogProcess?.dismiss()
                            showToastMessage("ไม่สามารถระบุตำแหน่งได้")
                            waitEnableScan()
                        }
                    })
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

    override fun onPause() {
        scanner_fragment?.pause()
        super.onPause()
    }

    fun postCheckIn(
            result: String,
            latitude: Double,
            longitude: Double
    ) {
        baseDialogProcess?.show()
        checkInResponse.postCheckIn(
                type = arguments?.getString(ScanQrActivity.KEY_TYPE_SCAN_QR).toString(),
                qrcodeUniqueKey = result,
                locationId = null,
                checkinType = HistoryStaffType.NORMAL.name,
                latitude = latitude.toString(),
                longitude = longitude.toString()
        ).subscribe({
            baseDialogProcess?.dismiss()
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
                    waitEnableScan()
                }
            }
        }, {
            // error
            baseDialogProcess?.dismiss()
            errorScan()
            waitEnableScan()
        }).addTo(compositeDisposable)
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
            MockDialogFragment
                    .setOnPositiveDialogListener(object : MockDialogFragment.MockDialogListener {
                        override fun onPositive(dialog: MockDialogFragment?) {
                            dialog?.dismiss()
                            waitEnableScan()
                        }
                    })
                    .setCancelable(false)
                    .build()
                    .show(
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
        isShowDialog = true
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
        isShowDialog = true
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
        activity?.supportFragmentManager?.let {
            OldQrDialogFragment
                    .setOnPositiveDialogListener(
                            object : OldQrDialogFragment.OldQrDialogListener {
                                override fun onPositive(dialog: OldQrDialogFragment?) {
                                    dialog?.dismiss()
                                    waitEnableScan()
                                }
                            }
                    )
                    .setCancelable(false)
                    .build().show(it, OldQrDialogFragment.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    fun waitEnableScan() {
        Handler().postDelayed({
            isScan = true
        }, 3000)
    }
}
