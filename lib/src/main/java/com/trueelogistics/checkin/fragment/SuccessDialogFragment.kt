package com.trueelogistics.checkin.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.extensions.format
import com.trueelogistics.checkin.handler.CheckInTEL
import kotlinx.android.synthetic.main.fragment_success_checkin.*
import java.util.*

class SuccessDialogFragment : androidx.fragment.app.DialogFragment() {
    companion object {
        var KEY_TYPE_STATUS = "KEY_TYPE_STATUS"
        const val TAG = "SuccessDialogFragment"
        fun newInstance(type: String): SuccessDialogFragment {
            val fragment = SuccessDialogFragment()

            val bundle = Bundle().apply {
                putString(KEY_TYPE_STATUS, type)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_success_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onPause()
        status_checkin1.text = translateType(arguments?.getString(KEY_TYPE_STATUS).toString())
        status_checkin2.text = translateType(arguments?.getString(KEY_TYPE_STATUS).toString())
        timeCheckIn.text = Date().format("HH:mm")
        isCancelable = false
        confirm.setOnClickListener {
            onResume()
            ScanQrFragment.isScan = true
            activity?.setResult(Activity.RESULT_OK,
                Intent(activity, CheckInTEL::class.java).putExtras(
                    Bundle().apply {
                        this.putString(CheckInTEL.KEY_RESULT_CHECK_IN_TEL, "success")
                    }
                ))
            activity?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    private fun translateType(type: String): String {
        var typeTH = ""
        when (type) {
            CheckInTELType.CheckIn.name -> typeTH = getString(R.string.full_checkin_text)
            CheckInTELType.CheckBetween.name -> typeTH = getString(R.string.full_check_between_text)
            CheckInTELType.CheckOutOverTime.name -> typeTH = getString(R.string.full_checkout_text)
            CheckInTELType.CheckOut.name -> typeTH = getString(R.string.full_checkout_text)
        }
        return typeTH
    }
}
