package com.trueelogistics.checkin.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.interfaces.OnClickItemCallback
import com.trueelogistics.checkin.model.GenerateItemHubModel
import kotlinx.android.synthetic.main.item_nearby.view.*

class GenerateHubAdapter(val onClickItem: OnClickItemCallback) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    var items: ArrayList<GenerateItemHubModel> = arrayListOf()
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_nearby, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val viewHolder = view as ViewHolder
        viewHolder.bind(position)
    }

    inner class ViewHolder(private val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            val hubName = view.hub_name
            hubName.text = items[position].hubName

            view.setOnClickListener {
                onClickItem.onClickItem(items[position])
            }
        }
    }
}