package com.trueelogistics.checkin.scanqr

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.testretofit.PersonModel
import kotlinx.android.synthetic.main.fragment_manaul_checkin.*

class ManualCheckinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manaul_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        checkin_pic.setOnClickListener {
            if (checkin_pic.drawable.constantState == resources
                    .getDrawable( R.drawable.ic_checkin_gray).constantState)
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_color)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
        }
        between_pic.setOnClickListener {
            if (between_pic.drawable.constantState == resources
                    .getDrawable( R.drawable.ic_checkin_gray).constantState)
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_color)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
        }
        checkout_pic.setOnClickListener {
            if (checkout_pic.drawable.constantState == resources
                    .getDrawable( R.drawable.ic_checkin_gray).constantState)
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_color)
            }
        }
        checkInHub.setOnClickListener {
            val stockDialogFragment  = StockDialogFragment()
            stockDialogFragment.setOnItemLocationClick {
                setView(it)
            }
            stockDialogFragment.show(activity?.supportFragmentManager, "show")
        }
        confirm.setOnClickListener {
            SuccessDialogFragment().show(activity?.supportFragmentManager,"show")
        }
    }

    private fun setView(item:PersonModel){
        stockName.text = item.firstName
        stockName.setTextColor(resources.getColor(R.color.black))
        confirm.setBackgroundColor(resources.getColor(R.color.purple))
        confirm.setTextColor(resources.getColor(R.color.white))
        confirm.isEnabled = true
    }
}
