package com.trueelogistics.checkin.history

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.check_in_retrofit.view.*

class TypeHistoryAdapter  : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val items: ArrayList<HistoryInDataModel> = arrayListOf()
    var datePresent : String? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
        R.layout.check_in_retrofit, viewGroup, false)
        return ViewHolder(view) }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = view as ViewHolder
        viewHolder.bind(position)
    }
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int) {
            val dateCheck = items[position].updatedAt?.substring(0,10)
            if (datePresent.equals(dateCheck)){
                Log.e(" position ==",position.toString())
                view.typeCheckIn.text = items[position].type
                view.hubName.text = items[position].qrcodeId.locationId
                view.timeHistory.text = items[position].updatedAt?.substring(11,16)
            }
            else{
                items[position]
            }
        }

    }
}