package com.trueelogistics.checkin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.extensions.formatISO
import com.trueelogistics.checkin.model.HistoryInDataModel
import kotlinx.android.synthetic.main.history_retrofit.view.*

class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val items: ArrayList<HistoryInDataModel> = arrayListOf()
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_retrofit, viewGroup, false)
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
            val date = view.date
            val format = items[position].updatedAt
            val day = format?.formatISO("dd")
            val mouth = format?.formatISO("MM")
            val year = format?.formatISO("yyyy")
            date.text = String.format(view.context.getString(R.string.date_history), day, mouth, year)
        }
    }
}