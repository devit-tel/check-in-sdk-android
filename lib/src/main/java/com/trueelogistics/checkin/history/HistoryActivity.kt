package com.trueelogistics.checkin.history

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.HistoryRootModel
import com.trueelogistics.checkin.service.RetrofitGenerater
import com.trueelogistics.checkin.service.HistoryService
import kotlinx.android.synthetic.main.activity_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {
    private var adaptor = HistoryAdaptor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        toolBar.setOnClickListener{

        }
        dateRecycleView.adapter = adaptor
        val retrofit = RetrofitGenerater().build().create(HistoryService::class.java)
        val call = retrofit?.getData()
        call?.enqueue(object : Callback<HistoryRootModel> {
            override fun onFailure(call: Call<HistoryRootModel>, t: Throwable) {
                print("hello")
            }
            override fun onResponse(call: Call<HistoryRootModel>, response: Response<HistoryRootModel>) {
                if (response.code() == 200) {
                    val logModel: HistoryRootModel? = response.body()
                    dateRecycleView?.layoutManager = LinearLayoutManager(this@HistoryActivity)
                    if (logModel != null) {
                            adaptor.items.addAll(logModel.data.data)
                            adaptor.notifyDataSetChanged()
                    }
                } else {
                    response.errorBody()
                }
            }
        })
    }
}
