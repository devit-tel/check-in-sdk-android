package com.trueelogistics.checkin.scanqr

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.trueelogistics.checkin.interfaces.CheckInTELCallBack
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.TypeCallback
import com.trueelogistics.checkin.model.HistoryRootModel
import com.trueelogistics.checkin.service.HistoryService
import com.trueelogistics.checkin.service.RetrofitGenerater
import kotlinx.android.synthetic.main.activity_main_scan_qr.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.*

class MainScanQrActivity : AppCompatActivity() {
    private var adapter = HistoryStaffAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_scan_qr)

        checkButton()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        }else{

        }
        historyRecycle.adapter = adapter
        val retrofit = RetrofitGenerater().build().create(HistoryService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HistoryRootModel> {
            override fun onFailure(call: Call<HistoryRootModel>, t: Throwable) {
                print("Fail")
            }
            override fun onResponse(call: Call<HistoryRootModel>, response: Response<HistoryRootModel>) {
                when {
                    response.code() == 200 -> {
                        val logModel: HistoryRootModel? = response.body()
                        historyRecycle?.layoutManager = LinearLayoutManager(this@MainScanQrActivity)
                        if (logModel != null) {
                            adapter.items.addAll(logModel.data.data)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    response.code() == 400 -> {

                    }
                    response.code() == 500 -> {

                    }
                    else -> {
                        response.errorBody()
                    }
                }
            }
        })
        checkInBtn.setOnClickListener {
            openScanQr(this, "CHECK_IN")
        }
        checkBetBtn.setOnClickListener {
            openScanQr(this, "CHECK_IN_BETWEEN")
        }
        checkOutBtn.setOnClickListener {
            openScanQr(this, "CHECK_OUT")
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
        checkButton()
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

    private fun checkButton() {
        CheckInTEL.checkInTEL?.getHistory(object : TypeCallback {
            override fun getType(type: String) {
                if (type == "CHECK_IN" || type == "CHECK_IN_BETWEEN") {
                    checkInBtn.isEnabled = false
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))

                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                } else if (type == "CHECK_OUT") {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment,
                        ScanQrFragment.newInstance("CHECK_IN")
                    ).commit()
                    checkInBtn.isEnabled = true
                    checkBetBtn.isEnabled = false
                    checkOutBtn.isEnabled = false
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.purple))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))

                } else {
                    checkInBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkBetBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))
                    checkOutBtn.setBackgroundColor(ContextCompat.getColor(this@MainScanQrActivity, R.color.gray))

                }
            }
        })
    }
}
