package com.trueelogistics.checkin.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.extensions.format
import com.trueelogistics.checkin.handler.CheckInTEL
import kotlinx.android.synthetic.main.fragment_success_checkin.*
import java.util.*

class SuccessDialogFragment : DialogFragment() {
    companion object {
        var TYPE_STATUS = "@string/checkin_text"
        fun newInstance(type: String): SuccessDialogFragment {
            val fragment = SuccessDialogFragment()

            val bundle = Bundle().apply {
                putString(TYPE_STATUS, type)
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
        status_checkin1.text = translateType(arguments?.getString(TYPE_STATUS).toString())
        status_checkin2.text = translateType(arguments?.getString(TYPE_STATUS).toString())
        timeCheckIn.text = Date().format("HH:mm")
        isCancelable = false
        confirm.setOnClickListener {
            onResume()
            ScanQrFragment.isScan = true
            activity?.setResult(
                Activity.RESULT_OK,
                Intent(activity, CheckInTEL::class.java).putExtras(
                    Bundle().apply {
                        this.putString(CheckInTEL.KEY_RESULT_CHECK_IN_TEL, "success")
                    }
                )
            )
            activity?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)
    }

    private fun translateType(type: String): String {
        var typeTH = ""
        when (type) {
            "CHECK_IN" -> typeTH = getString(R.string.full_checkin_text)
            "CHECK_IN_BETWEEN" -> typeTH = getString(R.string.full_check_between_text)
            "CHECK_OUT" -> typeTH = getString(R.string.full_checkout_text)
        }
        return typeTH
    }
}
