package com.trueelogistics.checkin.service

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.model.list_hub.InDataModel
import kotlinx.android.synthetic.main.item_retrofit.view.*

class HubAdapter(private var items: ArrayList<InDataModel>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemLocationClickListener : OnItemLocationClickListener? = null
    private var oldRadioButton : RadioButton? = null

    override fun onBindViewHolder(view: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = view as ViewHolder
        viewHolder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_retrofit, viewGroup, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int) {
            val nameText = view.stockBt
            nameText.text = items[position].locationName

            view.layoutItem.setOnClickListener {
                onItemLocationClickListener?.onItemLocationClick( items[position],oldRadioButton,view.stockBt)
                oldRadioButton = view.stockBt
            }

            view.stockBt.setOnClickListener {
                onItemLocationClickListener?.onItemLocationClick( items[position],oldRadioButton,view.stockBt)
                oldRadioButton = view.stockBt
            }
        }

    }

    interface OnItemLocationClickListener{
        fun onItemLocationClick(item: InDataModel,oldRadioButton : RadioButton?,newRadioButton : RadioButton?)
    }
}