package com.trueelogistics.checkin.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.extensions.formatISO
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.item_history_retrofit.view.*

class HistoryStaffAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val items: ArrayList<HistoryInDataModel> = arrayListOf()
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_history_retrofit, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = view as ViewHolder
        viewHolder.bind(position)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            val type = view.typeCheckIn
            val hub = view.hubCheckIn
            val time = view.timeCheckIn
            val typeScan = view.type_scan
            when (val eventType = items[position].eventType ?: "old") {
                CheckInTELType.CheckIn.value -> {
                    view.iconTypeCheckIn.setImageResource(R.drawable.ic_check_in)
                    type.text = view.context.getString(R.string.full_checkin_text)
                }
                CheckInTELType.CheckBetween.value -> {
                    view.iconTypeCheckIn.setImageResource(R.drawable.ic_check_between)
                    type.text = view.context.getString(R.string.full_check_between_text)
                }
                CheckInTELType.CheckOut.value -> {
                    view.iconTypeCheckIn.setImageResource(R.drawable.ic_check_out)
                    type.text = view.context.getString(R.string.full_checkout_text)
                }
                else -> {
                    view.iconTypeCheckIn.setImageResource(R.drawable.ic_checkin_gray)
                    type.text = eventType
                }
            }
            hub.text = items[position].locationName
            time.text = items[position].updatedAt?.formatISO("HH:mm")
            typeScan.text = when (items[position].checkinType) {
                "NORMAL" -> {
                    view.context.getString(R.string.statusNormal)
                }
                "MANUAL" -> {
                    view.context.getString(R.string.statusManual)
                }
                "AUTO" -> {
                    view.context.getString(R.string.statusAuto)
                }
                else -> {
                    if (items[position].checkinType.isNullOrEmpty())
                        view.context.getString(R.string.statusManual)
                    else
                        items[position].checkinType.toString()
                }
            }
        }
    }
}