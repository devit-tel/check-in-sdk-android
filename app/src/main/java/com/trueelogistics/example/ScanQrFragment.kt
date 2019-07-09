package com.trueelogistics.example

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.activity.GenQrActivity
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.handler.TypeCallback
import com.trueelogistics.checkin.scanqr.ScanQrActivity
import kotlinx.android.synthetic.main.fragment_scan_qr.*

class ScanQrFragment : Fragment() {

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

        checkInBtn.setOnClickListener {
            putString("CHECK_IN")
        }
        checkBetBtn.setOnClickListener {
            putString("CHECK_IN_BETWEEN")
        }
        checkOutBtn.setOnClickListener {
            putString("CHECK_OUT")
        }
        genQr.setOnClickListener {
            val intent = Intent(activity, GenQrActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        checkButton()
    }
    private fun putString(type: String) {
        val intent = Intent(activity, ScanQrActivity::class.java)
        intent.putExtras(
            Bundle().apply {
                putString("type", type)
            }
        )
        this.startActivity(intent)
    }

    private fun checkButton() {

        CheckInTEL.checkInTEL?.getHistory(object : TypeCallback {
            override fun getType(type: String) {
                if (type == "CHECK_IN" || type == "CHECK_IN_BETWEEN") {
                    checkInBtn.isEnabled = false
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
                    }
                    checkBetBtn.isEnabled = true
                    checkOutBtn.isEnabled = true
                } else if (type == "CHECK_OUT") {
                    checkInBtn.isEnabled = true
                    checkBetBtn.isEnabled = false
                    checkOutBtn.isEnabled = false
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                    }
                } else {
                    activity?.let {
                        checkInBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkBetBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                        checkOutBtn.setBackgroundColor(ContextCompat.getColor(it, R.color.gray))
                    }
                }
            }

        })


    }
}
