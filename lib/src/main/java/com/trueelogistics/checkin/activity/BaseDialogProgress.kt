package com.trueelogistics.checkin.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import com.trueelogistics.checkin.R

class BaseDialogProgress(private val activity: Activity) : Dialog(activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_progress)

        window?.let {
            it.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setGravity(Gravity.CENTER)
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }
}