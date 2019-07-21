package com.trueelogistics.checkin.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.adapter.HubAdapter
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.model.HubInDataModel
import kotlinx.android.synthetic.main.fragment_stock_dialog.*

class StockDialogFragment : BottomSheetDialogFragment(), HubAdapter.OnItemLocationClickListener {

    private var doSomething: ((item: HubInDataModel) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRetrofit()
        choiceStock.setOnClickListener {
            if (choiceStock.background is ColorDrawable)
                dismiss()
        }
    }

    private fun getRetrofit() {
        CheckInTEL.checkInTEL?.hubGenerater(object : ArrayListGenericCallback<HubInDataModel> {
            override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                activity?.also {
                    recycleView?.layoutManager = LinearLayoutManager(it)
                    recycleView.adapter = HubAdapter(dataModel ?: arrayListOf(), it).apply {
                        onItemLocationClickListener = this@StockDialogFragment
                    }
                }
            }

            override fun onFailure(message: String?) {

            }
        })
    }

    override fun onItemLocationClick(item: HubInDataModel, oldRadioButton: RadioButton?, newRadioButton: RadioButton?) {
        oldRadioButton?.isChecked = false
        newRadioButton?.isChecked = true
        activity?.let {
            choiceStock.setBackgroundColor(ContextCompat.getColor(it, R.color.purple))
            choiceStock.isEnabled = true
        }
        doSomething?.let {
            it(item)
        }
    }

    fun setOnItemLocationClick(doSomething: ((item: HubInDataModel) -> Unit)? = null) { // save stucture from stock to value name doSomething
        this.doSomething = doSomething  //save class who call this function
    }
}
