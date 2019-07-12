package com.trueelogistics.checkin.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.HistoryCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.HistoryInDataModel
import com.trueelogistics.checkin.adapter.HistoryStaffAdapter
import com.trueelogistics.checkin.enums.CheckinTELType
import kotlinx.android.synthetic.main.activity_main_scan_qr.*
import java.util.*
import kotlin.collections.ArrayList

class MainScanQrActivity : AppCompatActivity() {
    private var adapter = HistoryStaffAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)

        checkButton()
        val day = Date().toString().substring(0,3)
        val nDay = Date().toString().substring(8,10)
        val mouth = Date().toString().substring(4,7)
        date.text = "$day , $nDay $mouth"
        getHistoryToday()
        checkInBtn.setOnClickListener {
            openScanQr(this, CheckinTELType.CheckIn.value)
        }
        checkBetBtn.setOnClickListener {
            openScanQr(this, CheckinTELType.CheckBetween.value)
        }
        checkOutBtn.setOnClickListener {
            openScanQr(this, CheckinTELType.CheckOut.value)
        }
        genQr.setOnClickListener {
            CheckInTEL.checkInTEL?.openGenerateQRCode(this, "userId", object : CheckInTELCallBack {
                override fun onCancel() {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCancel === ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInFailure(message: String) {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCheckFail = $message ", Toast.LENGTH_SHORT).show()
                }

                override fun onCheckInSuccess(result: String) {
                    Toast.makeText(this@MainScanQrActivity, " GenQr.onCheckSuccess = $result", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        getHistoryToday()
        checkButton()
    }

    private fun getHistoryToday(){
        historyRecycle.adapter = adapter
        historyRecycle?.layoutManager = LinearLayoutManager(this@MainScanQrActivity)
        CheckInTEL.checkInTEL?.getHistory(object : HistoryCallback {
            override fun historyGenerate(dataModel: ArrayList<HistoryInDataModel>) {
                adapter.items.removeAll(dataModel)
                adapter.items.addAll(dataModel)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun openScanQr(context: Context, type: String) {
        CheckInTEL.checkInTEL?.openScanQRCode(this, "userId", type, object : CheckInTELCallBack {
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
    var checkFirstInDay = true
    private fun checkButton() {
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun getType(type: String) {
                if (type == CheckinTELType.CheckIn.value || type == CheckinTELType.CheckBetween.value) {
                    checkFirstInDay = false
                    checkInBtn.isEnabled = false
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))

                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                } else if (type == CheckinTELType.CheckOut.value) {
                    if (checkFirstInDay) {
                        openScanQr(this@MainScanQrActivity, CheckinTELType.CheckIn.value)
                        checkFirstInDay = false
                    }
                    checkInBtn.isEnabled = true
                    checkBetBtn.isEnabled = false
                    checkOutBtn.isEnabled = false
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))

                } else {
                    checkFirstInDay = false
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))

                }
            }
        })
    }
}
