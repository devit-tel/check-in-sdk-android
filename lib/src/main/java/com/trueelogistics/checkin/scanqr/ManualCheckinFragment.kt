package com.trueelogistics.checkin.scanqr

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.ScanRootModel
import com.trueelogistics.checkin.model.list_hub.InDataModel
import com.trueelogistics.checkin.service.RetrofitGenerater
import com.trueelogistics.checkin.service.ScanQrService
import kotlinx.android.synthetic.main.fragment_manaul_checkin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualCheckinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manaul_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var type : String? = "CHECK_IN"
        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        checkin_pic.setOnClickListener {
            if (checkin_pic.drawable.constantState == ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_checkin_gray,null)?.constantState )
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_color)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            type = "CHECK_IN"
        }
        between_pic.setOnClickListener {
            if (between_pic.drawable.constantState ==  ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_checkin_gray,null)?.constantState )
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_color)
                checkout_pic.setImageResource(R.drawable.ic_checkin_gray)
            }
            type = "CHECK_IN_BETWEEN"
        }
        checkout_pic.setOnClickListener {
            if (checkout_pic.drawable.constantState == ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_checkin_gray,null)?.constantState)
            {
                checkin_pic.setImageResource(R.drawable.ic_checkin_gray)
                between_pic.setImageResource(R.drawable.ic_checkin_gray)
                checkout_pic.setImageResource(R.drawable.ic_checkin_color)
            }
            type = "CHECK_OUT"
        }
        checkInHub.setOnClickListener {
            val stockDialogFragment  = StockDialogFragment()
            stockDialogFragment.setOnItemLocationClick {
                setView(it)
            }
            stockDialogFragment.show(activity?.supportFragmentManager, "show")
        }
        confirm.setOnClickListener {
            val retrofit = RetrofitGenerater().build(true).create(ScanQrService::class.java)
            val loadingDialog = ProgressDialog.show(context, "Saving History", "please wait...")
            val call = retrofit?.getData(type.toString(), "")
            call?.enqueue(object : Callback<ScanRootModel>{
                override fun onFailure(call: Call<ScanRootModel>, t: Throwable) {
                    loadingDialog.dismiss()
                }
                override fun onResponse(call: Call<ScanRootModel>, response: Response<ScanRootModel>) {
                    loadingDialog.dismiss()
                    when {
                        response.code() == 200 -> {
//                            response.body()
                            SuccessDialogFragment().show(activity?.supportFragmentManager, "show")
                        }
                        response.code() == 400 -> {
                            onPause()
                            activity?.let {
                                OldQrDialogFragment().show(it.supportFragmentManager, "show")
                                it.recreate()
                            }
                        }
                        response.code() == 500 -> {
                            activity?.let {
                                Toast.makeText(it,"Server Error", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        else -> {
                            response.errorBody()
                        }
                    }
                }
            })
        }
    }

    private fun setView(item: InDataModel){
        stockName.text = item.locationName
        activity?.let {
            stockName.setTextColor(ContextCompat.getColor(it, R.color.black))
            confirm.setBackgroundColor(ContextCompat.getColor(it,R.color.purple))
            confirm.setTextColor(ContextCompat.getColor(it,R.color.white))
        }
        confirm.isEnabled = true
    }
}
