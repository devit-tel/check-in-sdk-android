package com.trueelogistics.checkin.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.BaseDialogProgress
import com.trueelogistics.checkin.api.RetrofitGenerator
import com.trueelogistics.checkin.api.repository.CheckInRepository
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.CheckLocationHandler
import com.trueelogistics.checkin.model.GenerateItemHubModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_checkin_dialog.*

class CheckInDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "CheckInDialogFragment"
    }

    var item: GenerateItemHubModel? = null
    var typeFromLastCheckIn: String? = null
    var checkinType: String? = null
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
            NearByFindingFragment.showView = true
            ShakeFindingFragment.showView = true
            checkLocation(typeCheckIn, item?.hubId ?: " HubID is null ")
        }
        cancel_checkin.setOnClickListener {
            dialog?.cancel()
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
            CheckInTELType.CheckBetween.value, CheckInTELType.CheckIn.value -> {
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
        val retrofit = RetrofitGenerator().build(true).create(com.trueelogistics.checkin.api.service.ScanQrService::class.java)
        activity?.let { activity ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            if (ContextCompat.checkSelfPermission(
                            activity, Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
            ) {
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

                })

            } else {
                val intent = Intent(activity, CheckInTEL::class.java)
                intent.putExtras(
                        Bundle().apply {
                            putString(
                                    CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                    , "Permission GPS Denied!!"
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


    fun postCheckIn(location: Location, type: String, hubID: String) {
        baseDialogProcess?.show()
        checkInResponse.postCheckIn(
                type = type,
                qrcodeUniqueKey = null,
                locationId = hubID,
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString()
        ).subscribe({
            baseDialogProcess?.dismiss()
            when {
                it.code() == 200 -> {
                    scanCompleteOpenDialogSuccess(type)
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

    fun scanCompleteOpenDialogSuccess(type: String) {
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            SuccessDialogFragment.newInstance(type)
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

    fun errorScan() {
        showToastMessage("ไม่สามารถสแกนเพื่อระบุตำแหน่งได้")
    }

    fun showToastMessage(message: String?) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
        ).show()
    }
}
