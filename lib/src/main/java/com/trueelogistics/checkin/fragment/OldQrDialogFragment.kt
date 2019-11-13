package com.trueelogistics.checkin.fragment

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import kotlinx.android.synthetic.main.fragment_old_qr_dialog.*


class OldQrDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "OldQrDialogFragment"
        private var onPositiveDialogListener: OldQrDialogListener? = null
        private var isCancelable: Boolean? = true
        fun newInstance(): OldQrDialogFragment {
            val dialogFragment = OldQrDialogFragment()
            dialogFragment.isCancelable = isCancelable ?: true
            dialogFragment.setOnPositiveDialogListener(onPositiveDialogListener)
            return dialogFragment
        }

        fun setOnPositiveDialogListener(onPositiveDialogListener: OldQrDialogListener? = null) =
                OldQrDialogFragment.apply {
                    this.onPositiveDialogListener = onPositiveDialogListener
                }

        fun setCancelable(isCancelable: Boolean? = true) =
                OldQrDialogFragment.apply {
                    this.isCancelable = isCancelable
                }

        fun build() = newInstance()
    }

    private var onPositiveDialogListener: OldQrDialogListener? = null

    interface OldQrDialogListener {
        fun onPositive(dialog: OldQrDialogFragment?)
    }

    fun setOnPositiveDialogListener(onPositiveDialogListener: OldQrDialogListener? = null) {
        this.onPositiveDialogListener = onPositiveDialogListener
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_old_qr_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanAgain.setOnClickListener {
            onPositiveDialogListener?.onPositive(this)
        }
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }
}

