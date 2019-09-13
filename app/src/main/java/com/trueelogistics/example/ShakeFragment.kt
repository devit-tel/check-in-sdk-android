package com.trueelogistics.example

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
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
        ShakeDetector.start()
        activity?.let { fragActivity ->
            ShakeDetector.create(fragActivity) {
                shakeFunction( fragActivity )
                ShakeDetector.stop()
            }
            shake_phone_fine.setOnClickListener {
                shakeFunction(fragActivity)
            }
        }
    }

    private fun shakeFunction(activity : Activity){
        CheckInTEL.checkInTEL?.openShake(activity, object : CheckInTELCallBack {
            override fun onCheckInSuccess(result: String) {
                Toast.makeText(context, " Shake.onSuccess : $result", Toast.LENGTH_SHORT).show()
                ShakeDetector.start()
            }

            override fun onCheckInFailure(message: String) {
                Toast.makeText(context, " Shake.onFail : $message ", Toast.LENGTH_SHORT).show()
            }

            override fun onCancel() {
                Toast.makeText(context, " Shake.onCancel ", Toast.LENGTH_SHORT).show()
                ShakeDetector.start()
            }

        })
    }

}
