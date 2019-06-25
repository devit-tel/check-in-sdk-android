package com.trueelogistics.checkin.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.generate_qr.RootModel
import com.trueelogistics.checkin.service.GetRetrofit
import com.trueelogistics.checkin.service.GenQrService
import kotlinx.android.synthetic.main.activity_gen_qr.*
import net.glxn.qrgen.android.QRCode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gen_qr)

        getQr()

        val countTime = object : CountDownTimer(10000, 500) { // 1 second to onTick & 1 minit to onFinish

            override fun onTick(millisUntilFinished: Long) {
                timeCount.text = (millisUntilFinished/1000).toString()
            }
            override fun onFinish() {
                getQr()
                this.start()
            }
        }.start()

        refreshTime.setOnClickListener {
            getQr()
            countTime.start()
        }
    }
    fun getQr()
    {
        val retrofit = GetRetrofit.getRetrofit?.build()?.create(GenQrService::class.java)
        val call = retrofit?.getData("Leader","5d01d417136e06003c23024e")

        call?.enqueue( object : Callback<RootModel>{
            override fun onFailure(call: Call<RootModel>, t: Throwable) {
                Log.e(" onFailure !!"," Something wrong")
            }

            override fun onResponse(call: Call<RootModel>, response: Response<RootModel>) {
                if ( response.code() == 200){
                    val root : RootModel? = response.body()
                    if(root?.status == "OK"){
                        val qrText = root.data.qrcodeUniqueKey.toString()
                        Log.e(" == root. ==", qrText)
                        val result = QRCode.from(qrText).withSize(1000, 1000).bitmap()
                        qrCode.setImageBitmap(result)
                    }
                }
                else{
                    response.errorBody()
                }
            }
        })
    }

}
