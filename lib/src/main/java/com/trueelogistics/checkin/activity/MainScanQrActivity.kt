package com.trueelogistics.checkin.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.adapter.HistoryStaffAdapter
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.extensions.format
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.activity_main_scan_qr.*
import java.util.*

class MainScanQrActivity : AppCompatActivity() {
    private var adapter = HistoryStaffAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)
        checkButton()
        getHistoryToday()
        val day = Date().format("EE")
        val nDay = Date().format("dd")
        val mouth = Date().format("MMM")
        date.text = String.format(this.getString(R.string.date_checkin), day, nDay, mouth)
        back_to_menu.setOnClickListener {
            onBackPressed()
        }
        checkInBtn.setOnClickListener {
            openScanQr( CheckInTELType.CheckIn.value)
        }
        checkBetBtn.setOnClickListener {
            openScanQr( CheckInTELType.CheckBetween.value)
        }
        checkOutBtn.setOnClickListener {
            openScanQr( CheckInTELType.CheckOut.value)
        }

    }

    override fun onResume() {
        super.onResume()
        getHistoryToday()
        checkButton()
    }

    private fun getHistoryToday() {
        historyRecycle.adapter = adapter
        historyRecycle?.layoutManager = LinearLayoutManager(this@MainScanQrActivity)
        CheckInTEL.checkInTEL?.getHistory(object : ArrayListGenericCallback<HistoryInDataModel> {
            override fun onResponse(dataModel: ArrayList<HistoryInDataModel>?) {
                adapter.items.removeAll(dataModel ?: arrayListOf())
                adapter.items.addAll(dataModel ?: arrayListOf())
                adapter.notifyDataSetChanged()
                historyRecycle.scrollToPosition((dataModel?.size)?.minus(1) ?: 0)
            }

            override fun onFailure(message: String?) {
            }
        })

    }

    private fun openScanQr( type: String) {
        CheckInTEL.checkInTEL?.openScanQRCode(this, type ,false, object : CheckInTELCallBack {
            override fun onCancel() {

            }

            override fun onCheckInFailure(message: String) {

            }

            override fun onCheckInSuccess(result: String) {

            }
        })
    }

    var checkFirstInDay = true
    private fun checkButton() {
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun onResponse(type: String? , today : Boolean) {
                if (type == CheckInTELType.CheckIn.value || type == CheckInTELType.CheckBetween.value) {
                    checkFirstInDay = false
                    checkInBtn.visibility = View.GONE
                    checkBetBtn.visibility = View.VISIBLE
                    checkOutBtn.visibility = View.VISIBLE
                    pic_checkin.visibility = View.GONE
                    layoutRecycle.visibility = View.VISIBLE
                } else if (type == CheckInTELType.CheckOut.value) {
                    if (checkFirstInDay) {
                        openScanQr( CheckInTELType.CheckIn.value)
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
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                }
            }

            override fun onFailure(message: String?) {
                Toast.makeText(this@MainScanQrActivity, " ScanQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                checkInBtn.isEnabled = false
                checkBetBtn.isEnabled = false
                checkOutBtn.isEnabled = false
                checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
            }
        })
    }
}
