package com.trueelogistics.example

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.adapter.HistoryStaffAdapter
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.extensions.format
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.fragment_scan_qr.*
import java.util.*
import kotlin.collections.ArrayList


class ScanQrFragment : Fragment() {
    private var adapter = HistoryStaffAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity
        toolbar.setOnClickListener {
            mainActivity.actionToolbar()
        }
        checkButton()
        getHistoryToday()
        val day = Date().format("EE")
        val nDay = Date().format("dd")
        val mouth = Date().format("MMM")
        date.text = String.format(this.getString(R.string.date_checkin), day, nDay, mouth)
        activity?.let { activity ->
            checkInBtn.setOnClickListener {
                openScanQr(activity, CheckInTELType.CheckIn.value)
            }
            checkBetBtn.setOnClickListener {
                openScanQr(activity, CheckInTELType.CheckBetween.value)
            }
            checkOutBtn.setOnClickListener {
                openScanQr(activity, CheckInTELType.CheckOut.value)
            }
            CheckInTEL.checkInTEL?.openScanQRCode(activity,CheckInTELType.CheckIn.value,true, object : CheckInTELCallBack{
                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(
                        activity, result,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCheckInFailure(message: String) {
                    Toast.makeText(
                        activity, message ,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCancel() {
                    Toast.makeText(
                        activity, "on cancel",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun getHistoryToday(){
        historyRecycle.adapter = adapter
        activity?.let {
            historyRecycle?.layoutManager = LinearLayoutManager(it)
            CheckInTEL.checkInTEL?.getHistory(object : ArrayListGenericCallback<HistoryInDataModel> {
                override fun onResponse(dataModel: ArrayList<HistoryInDataModel>?) {

                    dataModel?.let { data ->
                        adapter.items.removeAll(data)
                        adapter.items.addAll(data)
                    }
                    adapter.notifyDataSetChanged()
                    historyRecycle.scrollToPosition((dataModel?.size)?.minus(1)?:0)
                }

                override fun onFailure(message: String?) {

                }
            })
        }
    }

    private fun openScanQr(context: Context, type : String) {
        activity ?.let {
            CheckInTEL.checkInTEL?.openScanQRCode(it,type,false, object : CheckInTELCallBack {
                override fun onCancel() {
                    Toast.makeText(context, " ScanQr.onCancel === ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInFailure(message: String) {
                    Toast.makeText(context, " ScanQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(context, " ScanQr.onCheckSuccess = $result", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        getHistoryToday()
        checkButton()
    }
    var checkFirstInDay = true
    private fun checkButton() {
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun onResponse(type: String?) {
                if (type == CheckInTELType.CheckIn.value || type == CheckInTELType.CheckBetween.value) {
                    checkFirstInDay = false
                    checkInBtn.visibility = View.GONE
                    checkBetBtn.visibility = View.VISIBLE
                    checkOutBtn.visibility = View.VISIBLE
                    pic_checkin.visibility = View.GONE
                    layoutRecycle.visibility = View.VISIBLE
                } else if (type == CheckInTELType.CheckOut.value) {
                    if (checkFirstInDay) {
                        activity?.let {
                            openScanQr(it, CheckInTELType.CheckIn.value)
                        }
                        checkFirstInDay = false
                    }
                    checkInBtn.visibility = View.VISIBLE
                    checkBetBtn.visibility = View.GONE
                    checkOutBtn.visibility = View.GONE
                    pic_checkin.visibility = View.VISIBLE
                    layoutRecycle.visibility = View.GONE

                } else {
                    checkFirstInDay = false
                    checkInBtn.isEnabled = false
                    checkBetBtn.isEnabled = false
                    checkOutBtn.isEnabled = false
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                    }
                }
            }

            override fun onFailure(message: String?) {
                Toast.makeText(context, " ScanQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                checkInBtn.visibility = View.GONE
                checkBetBtn.visibility = View.GONE
                checkOutBtn.visibility = View.GONE
                pic_checkin.visibility = View.VISIBLE
                layoutRecycle.visibility = View.GONE
            }
        })
    }
}
