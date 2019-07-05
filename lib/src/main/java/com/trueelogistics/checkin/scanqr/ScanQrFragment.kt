package com.trueelogistics.checkin.scanqr

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.generate_qr.RootModel
import com.trueelogistics.checkin.service.GetScanQrRetrofit
import com.trueelogistics.checkin.service.ScanQrService
import kotlinx.android.synthetic.main.fragment_scan_qrcode.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanQrFragment : Fragment() {
    private var isScan = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_qrcode, container, false)
    }

    companion object {
        const val TYPE_KEY = "TYPE_KEY"

        fun newInstance(type: String): ScanQrFragment {
            val fragment = ScanQrFragment()

            val bundle = Bundle().apply {
                putString(TYPE_KEY, type)
            }
            fragment.arguments = bundle

            return fragment
        }
    }

    private val callback = object : BarcodeCallback {
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {  // do noting in action
        }

        override fun barcodeResult(result: BarcodeResult) {
            result.text.also {
                sentQr(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scanner_fragment?.setStatusText("")
        scanner_fragment?.decodeContinuous(callback)
        self_checkin.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment,
                ManualCheckinFragment()
            )?.addToBackStack(ManualCheckinFragment::class.java.name)?.commit()
        }
    }

    private fun sentQr(result: String) {
        //start dialog and stop camera
        if (isScan) {
            isScan = false
            val loadingDialog = ProgressDialog.show(context, "Checking Qr code", "please wait...", true, false)
            GetScanQrRetrofit.initial()
            val retrofit = GetScanQrRetrofit.getRetrofit?.build()?.create(ScanQrService::class.java)
            val type = arguments?.getString(TYPE_KEY).toString()
            val call = retrofit?.getData(type, result)
            call?.enqueue(object : Callback<RootModel> {
                override fun onFailure(call: Call<RootModel>, t: Throwable) {
                    //stop dialog and start camera
                    loadingDialog.dismiss()

                    isScan = true
                }

                override fun onResponse(call: Call<RootModel>, response: Response<RootModel>) {
                    //stop dialog
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
                                Toast.makeText(it,"Server Error",Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        else -> {
                            response.errorBody()
                            isScan = true
                        }
                    }
                }
            })
        }
    }

    override fun onResume() {
        scanner_fragment?.resume()
        super.onResume()
    }

    override fun onPause() {
        scanner_fragment?.pause()
        super.onPause()
    }
}
