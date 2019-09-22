package com.trueelogistics.checkin.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.ScanQrFragment
import kotlinx.android.synthetic.main.fragment_old_qr_dialog.*

class OldQrDialogFragment : androidx.fragment.app.DialogFragment() {

    companion object{
        const val TAG = "OldQrDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_old_qr_dialog, container, false)
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
