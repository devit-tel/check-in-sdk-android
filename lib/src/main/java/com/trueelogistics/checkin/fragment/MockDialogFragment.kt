package com.trueelogistics.checkin.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import kotlinx.android.synthetic.main.fragment_mock_dialog.*

class MockDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "MockDialogFragment"
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mock_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onPause()
        scanAgain.setOnClickListener {
            dialog?.cancel()
            ScanQrFragment.isScan = true
            onResume()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }
}
