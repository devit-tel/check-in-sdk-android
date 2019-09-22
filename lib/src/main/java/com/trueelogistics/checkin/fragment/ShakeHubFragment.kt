package com.trueelogistics.checkin.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.ShakeActivity
import com.trueelogistics.checkin.adapter.GenerateHubAdapter
import com.trueelogistics.checkin.enums.CheckInTELType
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.OnClickItemCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.GenerateItemHubModel
import kotlinx.android.synthetic.main.fragment_shake_hub.*

class ShakeHubFragment : Fragment(), OnClickItemCallback {

    private var adapter = GenerateHubAdapter(this)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shake_hub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        nearbyRecycle.adapter = adapter
        nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
        activity?.let {
            if (it is ShakeActivity) {
                it.itemShake(object : ShakeActivity.ShakeCallback {
                    override fun onFound(hubId: String?, hubName: String?) {
                        if (hubId != null && hubName !== null)
                            insertItem(hubId, hubName)
                        else {
                            activity?.setResult(
                                    Activity.RESULT_OK,
                                    Intent(activity, CheckInTEL::class.java).apply {
                                        this.putExtras(
                                                Bundle().apply {
                                                    putString(
                                                            CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                            , " HubId and HubName is null"
                                                    )
                                                }
                                        )
                                    })
                            activity?.finish()
                        }
                    }
                })
            }
        }
    }

    fun insertItem(hubId: String, hubName: String) {
        val value = GenerateItemHubModel(hubId, hubName)
        val insertIndex = adapter.items.size
        adapter.items.add(value)
        adapter.notifyItemInserted(insertIndex)
    }

    override fun onClickItem(dataModel: GenerateItemHubModel) {
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun onResponse(type: String?, today: Boolean) {
                val newType = when (type) {
                    CheckInTELType.CheckOut.value, CheckInTELType.CheckOutOverTime.value -> {
                        CheckInTELType.CheckIn.value
                    }
                    else -> {
                        CheckInTELType.CheckBetween.value
                    }
                }
                activity?.supportFragmentManager?.also {
                    CheckInDialogFragment().apply {
                        this.checkinType = "SHAKE"
                        this.typeFromLastCheckIn = newType
                        this.item = dataModel
                    }.show(it, CheckInDialogFragment.TAG)
                }
            }

            override fun onFailure(message: String?) {
                activity?.setResult(
                        Activity.RESULT_OK,
                        Intent(activity, CheckInTEL::class.java).apply {
                            this.putExtras(
                                    Bundle().apply {
                                        putString(
                                                CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                                , " getLastCheck.onFail : $message"
                                        )
                                    }
                            )
                        })
                activity?.finish()
            }
        })
    }
}

