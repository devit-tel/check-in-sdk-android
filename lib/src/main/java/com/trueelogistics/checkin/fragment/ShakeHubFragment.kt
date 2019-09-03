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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shake_hub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_page.setOnClickListener {
            activity?.let {
                val intent = Intent(it, CheckInTEL::class.java)
                CheckInTEL.checkInTEL?.onActivityResult(
                    1750,
                    Activity.RESULT_CANCELED, intent
                )
                it.onBackPressed()
            }
        }
        nearbyRecycle.adapter = adapter
        nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
        activity?.let {
            ShakeActivity().itemShake( it , object : ShakeActivity.ShakeCallback {
                override fun onFound(hubId: String?, hubName: String?) {
                    if (hubId != null && hubName !== null)
                        insertItem(hubId, hubName)
                    else {
                        val intent = Intent(activity, CheckInTEL::class.java)
                        intent.putExtras(
                            Bundle().apply {
                                putString("error", " HubId and HubName is null")
                            }
                        )
                        CheckInTEL.checkInTEL?.onActivityResult(
                            1750,
                            Activity.BIND_NOT_FOREGROUND, intent
                        )
                        it.onBackPressed()
                    }
                }

            })
        }
    }

    fun insertItem(hubId: String, hubName: String) {
        val value = GenerateItemHubModel(hubId, hubName)
        val insertIndex = adapter.items.size
        adapter.items.add(value)
        adapter.notifyItemInserted(insertIndex)
    }

    override fun onClickItem(dataModel: GenerateItemHubModel) {
        val shakeDialog = SelectHubCheckInDialogFragment()
        shakeDialog.item = dataModel
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun onResponse(type: String? , today : Boolean) {
                val newType = when(type){
                    CheckInTELType.CheckOut.value -> {
                        CheckInTELType.CheckIn.value
                    }
                    else -> {
                        CheckInTELType.CheckBetween.value
                    }
                }
                shakeDialog.typeFromLastCheckIn = newType
                shakeDialog.show(activity?.supportFragmentManager, "show")
            }

            override fun onFailure(message: String?) {
                val intent = Intent(activity, CheckInTEL::class.java)
                intent.putExtras(
                    Bundle().apply {
                        putString("error"," getLastCheck.onFail : $message ")
                    }
                )
                CheckInTEL.checkInTEL?.onActivityResult(
                    1750,
                    Activity.BIND_NOT_FOREGROUND, intent
                )
            }

        })
    }
}

