package com.trueelogistics.checkin.fragment

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import kotlinx.android.synthetic.main.fragment_mock_dialog.*

class MockDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "MockDialogFragment"
        private var onPositiveDialogListener: MockDialogListener? = null
        private var isCancelable: Boolean? = true
        fun newInstance(): MockDialogFragment {
            val dialogFragment = MockDialogFragment()
            dialogFragment.isCancelable = isCancelable ?: true
            dialogFragment.setOnPositiveDialogListener(onPositiveDialogListener)
            return dialogFragment
        }

        fun setOnPositiveDialogListener(onPositiveDialogListener: MockDialogListener? = null) =
                MockDialogFragment.apply {
                    this.onPositiveDialogListener = onPositiveDialogListener
                }

        fun setCancelable(isCancelable: Boolean? = true) =
                MockDialogFragment.apply {
                    this.isCancelable = isCancelable
                }

        fun build() = newInstance()
    }

    private var onPositiveDialogListener: MockDialogListener? = null

    interface MockDialogListener {
        fun onPositive(dialog: MockDialogFragment?)
    }

    fun setOnPositiveDialogListener(onPositiveDialogListener: MockDialogListener? = null) {
        this.onPositiveDialogListener = onPositiveDialogListener
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
