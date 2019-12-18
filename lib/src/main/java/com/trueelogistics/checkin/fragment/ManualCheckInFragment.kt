package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.BaseDialogProgress
import com.trueelogistics.checkin.activity.ScanQrActivity
import com.trueelogistics.checkin.api.repository.CheckInRepository
import com.trueelogistics.checkin.enums.CheckInErrorType
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.CheckLocationHandler
import com.trueelogistics.checkin.model.HubInDataModel
import io.reactivex.disposables.CompositeDisposable
import com.trueelogistics.checkin.enums.HistoryStaffType
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
    private var baseDialogProcess: BaseDialogProgress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.also {
            baseDialogProcess = BaseDialogProgress(it)
        }
    }

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
        activity?.let { activity ->
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                baseDialogProcess?.show()
                CheckLocationHandler.instance.requestLocation(activity, object : CheckLocationHandler.CheckInLocationListener {
                    override fun onLocationUpdate(location: Location) {
                        baseDialogProcess?.dismiss()
                        if (!location.isFromMockProvider) {
                            postCheckIn(location, type, hub_id)
                        } else {
                            openDialogMockLocation()
                        }
                    }

                    override fun onLocationTimeout() {
                        baseDialogProcess?.dismiss()
                        showToastMessage("ไม่สามารถระบุตำแหน่งได้")
                    }

                    override fun onLocationError() {
                        baseDialogProcess?.dismiss()
                        showToastMessage("ไม่สามารถระบุตำแหน่งได้")
                    }

                    override fun dismissProgress() {
                        baseDialogProcess?.dismiss()
                    }
                })
            } else {
                showToastMessage(CheckInErrorType.PERMISSION_DENIED_ERROR.message)
            }
        }
    }

    fun postCheckIn(location: Location, type: String, hubID: String) {
        baseDialogProcess?.show()
        checkInResponse.postCheckIn(
                type = type,
                qrcodeUniqueKey = null,
                locationId = hubID,
                checkinType = HistoryStaffType.MANUAL.name,
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString()
        ).subscribe({
            baseDialogProcess?.dismiss()
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
            baseDialogProcess?.dismiss()
            errorScan()
        }).addTo(compositeDisposable)
    }

    fun openDialogMockLocation() {
        activity?.supportFragmentManager?.also {
            MockDialogFragment
                    .setOnPositiveDialogListener(object : MockDialogFragment.MockDialogListener {
                        override fun onPositive(dialog: MockDialogFragment?) {
                            dialog?.dismiss()
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
        activity?.supportFragmentManager?.let {
            OldQrDialogFragment
                    .setOnPositiveDialogListener(
                            object : OldQrDialogFragment.OldQrDialogListener {
                                override fun onPositive(dialog: OldQrDialogFragment?) {
                                    dialog?.dismiss()
                                }
                            }
                    )
                    .setCancelable(false)
                    .build().show(it, OldQrDialogFragment.TAG)
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
