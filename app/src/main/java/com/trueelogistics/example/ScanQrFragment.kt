package com.trueelogistics.example

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.enums.CheckinTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import kotlinx.android.synthetic.main.fragment_scan_qr.*

class ScanQrFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity
        toolbar.setOnClickListener {
            mainActivity.actionToolbar()
        }
        checkButton()
        activity?.let {activity ->
            checkInBtn.setOnClickListener {
                openScanQr(activity, CheckinTELType.CheckIn.value)
            }
            checkBetBtn.setOnClickListener {
                openScanQr(activity, CheckinTELType.CheckBetween.value)
            }
            checkOutBtn.setOnClickListener {
                openScanQr(activity, CheckinTELType.CheckOut.value)
            }
            genQr.setOnClickListener {
                CheckInTEL.checkInTEL?.openGenerateQRCode(activity, "userId", object : CheckInTELCallBack {
                    override fun onCancel() {
                        Toast.makeText(activity, " GenQr.onCancel === ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCheckInFailure(message: String) {
                        Toast.makeText(activity, " GenQr.onCheckFail = $message ", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onCheckInSuccess(result: String) {
                        Toast.makeText(activity, " GenQr.onCheckSuccess = $result", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        }
    }

    private fun openScanQr(context: Context, type : String) {
        activity ?.let {
            CheckInTEL.checkInTEL?.openScanQRCode(it,"userId",type, object : CheckInTELCallBack {
                override fun onCancel() {
                    Toast.makeText(context, " ScanQr.onCancel === ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInFailure(message: String) {
                    Toast.makeText(context, " ScanQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(context, " ScanQr.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        checkButton()
    }

    private fun checkButton() {
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun getType(type: String) {
                if (type == CheckinTELType.CheckIn.value || type == CheckinTELType.CheckBetween.value) {
                    checkInBtn.isEnabled = false
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
                    }
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                } else if (type == CheckinTELType.CheckOut.value) {
                    checkInBtn.isEnabled = true
                    checkBetBtn.isEnabled = false
                    checkOutBtn.isEnabled = false
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                    }
                } else {
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                    }
                }
            }
        })
    }
}
