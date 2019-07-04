package com.trueelogistics.checkin.scanqr


import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.trueelogistics.checkin.R
import kotlinx.android.synthetic.main.fragment_success_checkin.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SuccessDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_success_checkin, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = Date()
        timeCheckIn.text = current.toString().substring(11,16)
        isCancelable = false
        confirm.setOnClickListener {
            activity?.finish() //activity where call this fragment will finish
        }
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)
    }
}
