package com.trueelogistics.example

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.trueelogistics.checkin.fragment.ManualCheckInFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.TypeCallback
import kotlinx.android.synthetic.main.app_bar_main_menu.toolbar
import kotlinx.android.synthetic.main.fragment_shake.*

class ShakeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shake, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        toolbar.setOnClickListener {
            mainActivity.actionToolbar()
        }
        shake_phone_fine.setOnClickListener {
            activity?.let {
                CheckInTEL.checkInTEL?.openShake(it, object : CheckInTELCallBack {
                    override fun onCheckInSuccess(result: String) {

                    }

                    override fun onCheckInFailure(message: String) {
                    }

                    override fun onCancel() {

                    }

                })
            }
        }
        activity?.let { fragActivity ->

            ShakeDetector.start()
            ShakeDetector.create(fragActivity) {

            }
            ShakeDetector.destroy()
        }
    }


}
