package com.trueelogistics.checkin.scanqr

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.item_history_retrofit.view.*

class HistoryStaffAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val items: ArrayList<HistoryInDataModel> = arrayListOf()
    override fun onCreateViewHolder(viewGroup: ViewGroup, position : Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_history_retrofit, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder( view : RecyclerView.ViewHolder, position: Int) {
        val viewHolder = view as ViewHolder
        viewHolder.bind(position)
    }
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            val type = view.typeCheckIn
            val hub = view.hubCheckIn
            val time = view.timeCheckIn
            type.text = items[position].eventType ?: "old"
            hub.text = items[position].qrcodeId?.locationId
            time.text = items[position].updatedAt?.substring(11,16)
        }
    }

}