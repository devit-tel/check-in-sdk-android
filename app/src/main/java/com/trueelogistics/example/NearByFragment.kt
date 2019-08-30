package com.trueelogistics.example

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.activity.NearByActivity
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import kotlinx.android.synthetic.main.fragment_near_by.*

class NearByFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_near_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        toolbar.setOnClickListener {
            mainActivity.actionToolbar()
        }
        nearby_phone_fine.setOnClickListener {
            activity?.let{
                CheckInTEL.checkInTEL?.openNearBy( it , object : CheckInTELCallBack{
                    override fun onCancel() {
                        Toast.makeText(context, " NearBy.onCancel === ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCheckInFailure(message: String) {
                        Toast.makeText(context, " NearBy.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCheckInSuccess(result: String) {
                        Toast.makeText(context, "NearBy.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }
    }
}
