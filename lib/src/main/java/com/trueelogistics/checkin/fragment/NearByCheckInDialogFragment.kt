package com.trueelogistics.checkin.fragment

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.model.NearByHubModel
import kotlinx.android.synthetic.main.fragment_nearby_checkin_dialog.*

class NearByCheckInDialogFragment : BottomSheetDialogFragment(){
    var item : NearByHubModel ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_nearby_checkin_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hub_name.text = item?.hubName

    }

    private fun fixTypeView(type: String) {
        when (type) {
            CheckInTELType.CheckIn.value -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_color)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            CheckInTELType.CheckBetween.value -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_color)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            CheckInTELType.CheckOut.value -> {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_color)
            }
        }
    }
}
