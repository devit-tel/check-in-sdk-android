package com.trueelogistics.checkin.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
        CheckInTEL.checkInTEL?.hubGenerator(object : ArrayListGenericCallback<HubInDataModel> {
            override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                activity?.also {
                    recycleView?.layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(it)
                    recycleView.adapter = HubAdapter(dataModel ?: arrayListOf(), it).apply {
                        onItemLocationClickListener = this@StockDialogFragment
                    }
                }
            }

            override fun onFailure(message: String?) {

            }
        })
    }

    override fun onItemLocationClick(
        item: HubInDataModel,
        oldRadioButton: RadioButton?,
        newRadioButton: RadioButton?
    ) {
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

    fun setOnItemLocationClick(doSomething: ((item: HubInDataModel) -> Unit)? = null) {
        this.doSomething = doSomething
    }
}
