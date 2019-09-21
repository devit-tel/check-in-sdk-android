package com.trueelogistics.checkin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.HubInDataModel
import kotlinx.android.synthetic.main.item_retrofit.view.*

class HubAdapter(private var items: ArrayList<HubInDataModel>, private val context: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    var onItemLocationClickListener: OnItemLocationClickListener? = null
    private var oldRadioButton: RadioButton? = null
    override fun onBindViewHolder(
        view: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int
    ) {
        val viewHolder = view as ViewHolder
        viewHolder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        position: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_retrofit, viewGroup, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(private val view: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            val nameText = view.stockBt
            nameText.text = items[position].locationName
            view.layoutItem.setOnClickListener {
                onItemLocationClickListener?.onItemLocationClick(
                    items[position],
                    oldRadioButton,
                    view.stockBt
                )
                oldRadioButton = view.stockBt
            }
            view.stockBt.setOnClickListener {
                onItemLocationClickListener?.onItemLocationClick(
                    items[position],
                    oldRadioButton,
                    view.stockBt
                )
                oldRadioButton = view.stockBt
            }
        }
    }

    interface OnItemLocationClickListener {
        fun onItemLocationClick(
            item: HubInDataModel,
            oldRadioButton: RadioButton?,
            newRadioButton: RadioButton?
        )
    }
}