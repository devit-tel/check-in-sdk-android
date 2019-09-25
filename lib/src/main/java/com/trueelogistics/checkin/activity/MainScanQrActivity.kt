package com.trueelogistics.checkin.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
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
    private var isCallOpenScan = false
    private var baseDialogProcess: BaseDialogProgress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)
        baseDialogProcess = BaseDialogProgress(this)
        bindingData()
        getHistoryToday()
    }

    fun bindingData() {
        disableCheckIn()
        date.text = String.format(
                getString(R.string.date_checkin),
                Date().format("EE"),
                Date().format("dd"),
                Date().format("MMM")
        )
        back_to_menu.setOnClickListener {
            onBackPressed()
        }
        checkInBtn.setOnClickListener {
            openScanQr(CheckInTELType.CheckIn.value)
        }
        checkBetBtn.setOnClickListener {
            openScanQr(CheckInTELType.CheckBetween.value)
        }
        checkOutBtn.setOnClickListener {
            openScanQr(CheckInTELType.CheckOut.value)
        }
        historyRecycle.adapter = adapter
        historyRecycle?.layoutManager = LinearLayoutManager(this@MainScanQrActivity)
    }

    override fun onResume() {
        super.onResume()
        getHistoryToday()
    }

    private fun getHistoryToday() {
        baseDialogProcess?.show()
        CheckInTEL.checkInTEL?.getHistory(object : ArrayListGenericCallback<HistoryInDataModel> {
            override fun onResponse(dataModel: ArrayList<HistoryInDataModel>?) {
                baseDialogProcess?.dismiss()
                adapter.items.clear()
                adapter.items.addAll(dataModel ?: arrayListOf())
                adapter.notifyDataSetChanged()
                historyRecycle.scrollToPosition((dataModel?.size)?.minus(1) ?: 0)
                if (adapter.items.isEmpty()) {
                    showEmptyData()
                } else {
                    hideEmptyData()
                }
                checkButton()
            }

            override fun onFailure(message: String?) {
                showToastMessage(message)
                hideEmptyData()
                checkButton()
            }
        })
    }

    private fun openScanQr(type: String) {
        CheckInTEL.checkInTEL?.openScanQRCode(
                this,
                type,
                false,
                object : CheckInTELCallBack {
                    override fun onCancel() {
                        // cancel scan in lib main scan
                    }

                    override fun onCheckInFailure(message: String) {
                        showToastMessage(message)
                    }

                    override fun onCheckInSuccess(result: String) {
                        // scan success will be reload history
                    }
                })
    }

    private fun checkButton() {
        baseDialogProcess?.show()
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun onResponse(type: String?, today: Boolean) {
                baseDialogProcess?.dismiss()
                if (type == CheckInTELType.CheckIn.value || type == CheckInTELType.CheckBetween.value) {
                    showLastCheckIn()
                } else if (type == CheckInTELType.CheckOut.value || type == CheckInTELType.CheckOutOverTime.value) {
                    if (!isCallOpenScan) openScanQr(CheckInTELType.CheckIn.value)
                    isCallOpenScan = true
                    showLastCheckOut()
                } else {
                    disableCheckIn()
                }
            }

            override fun onFailure(message: String?) {
                baseDialogProcess?.dismiss()
                showToastMessage(message)
                disableCheckIn()
            }
        })
    }

    fun showLastCheckOut() {
        checkBetBtn.visibility = GONE
        checkOutBtn.visibility = GONE
        disableButtonCheckInBetween()
        disableButtonCheckOut()
    }

    fun showLastCheckIn() {
        checkInBtn.visibility = VISIBLE
        checkBetBtn.visibility = VISIBLE
        checkOutBtn.visibility = VISIBLE
        enableCheckIn()
    }

    fun disableCheckIn() {
        disableButtonCheckIn()
        disableButtonCheckInBetween()
        disableButtonCheckOut()
    }

    fun enableCheckIn() {
        enableButtonCheckIn()
        enableButtonCheckInBetween()
        enableButtonCheckOut()
    }

    fun disableButtonCheckIn() {
        checkInBtn.isEnabled = false
        checkInBtn.setBackgroundColor(
                ContextCompat.getColor(
                        this@MainScanQrActivity,
                        R.color.gray
                )
        )
    }

    fun disableButtonCheckInBetween() {
        checkBetBtn.isEnabled = false
        checkBetBtn.setBackgroundColor(
                ContextCompat.getColor(
                        this@MainScanQrActivity,
                        R.color.gray
                )
        )
    }

    fun disableButtonCheckOut() {
        checkOutBtn.isEnabled = false
        checkOutBtn.setBackgroundColor(
                ContextCompat.getColor(
                        this@MainScanQrActivity,
                        R.color.gray
                )
        )
    }

    fun enableButtonCheckIn() {
        checkInBtn.isEnabled = true
        checkInBtn.setBackgroundColor(
                ContextCompat.getColor(
                        this@MainScanQrActivity,
                        R.color.purple
                )
        )
    }

    fun enableButtonCheckInBetween() {
        checkBetBtn.isEnabled = true
        checkBetBtn.setBackgroundColor(
                ContextCompat.getColor(
                        this@MainScanQrActivity,
                        R.color.purple
                )
        )
    }

    fun enableButtonCheckOut() {
        checkOutBtn.isEnabled = true
        checkOutBtn.setBackgroundColor(
                ContextCompat.getColor(
                        this@MainScanQrActivity,
                        R.color.purple
                )
        )
    }

    fun showToastMessage(message: String?) {
        Toast.makeText(
                this@MainScanQrActivity,
                message
                , Toast.LENGTH_LONG
        ).show()
    }

    fun showEmptyData() {
        picCheckIn.visibility = VISIBLE
    }

    fun hideEmptyData() {
        picCheckIn.visibility = GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CheckInTEL.checkInTEL?.onActivityResult(requestCode, resultCode, data)
    }
}
