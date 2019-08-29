package com.trueelogistics.checkin.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.NearByActivity
import com.trueelogistics.checkin.adapter.NearByAdapter
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.interfaces.OnClickItemCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.HubInDataModel
import com.trueelogistics.checkin.model.NearByHubModel
import kotlinx.android.synthetic.main.fragment_near_by_hub.*


class NearByHubFragment : Fragment(), OnClickItemCallback {
    private var adapter = NearByAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by_hub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_page.setOnClickListener {
            activity?.finish()
        }
        fine_nearby.text = getString(R.string.nearby_fine)
        nearbyRecycle.adapter = adapter
        nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
        activity?.let {
            NearByActivity().itemNearBy(it, object : NearByActivity.NearByCallback {
                override fun onFoundNearBy(hubId: String?) {
                    getHubNameFromService(hubId.toString(), true)
                }

                override fun onLostNearBy(hubId: String?) {
                    getHubNameFromService(hubId.toString(), false)
                }
            })
        }
    }

    fun getHubNameFromService(hubId: String, foundItem: Boolean) {
        CheckInTEL.checkInTEL?.hubGenerater(object :
            ArrayListGenericCallback<HubInDataModel> {
            override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                var hubNameFromService: String? = ""
                dataModel?.forEach {
                    if (it._id == hubId)
                        hubNameFromService = it.locationName
                }
                if (foundItem)
                    insertItem(hubId, hubNameFromService.toString())
                else
                    removeItem(hubId, hubNameFromService.toString())
            }

            override fun onFailure(message: String?) {
                Toast.makeText(
                    activity, " name of Hub onFailure : $message "
                    , Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    fun insertItem(hubId: String, hubName: String) {
        val value = NearByHubModel(hubId, hubName)
        val insertIndex = adapter.items.size
        adapter.items.add(value)
        adapter.notifyItemInserted(insertIndex)
    }

    private fun removeItem(hubId: String, hubName: String) {
        val value = NearByHubModel(hubId, hubName)
        var removeIndex = adapter.items.size
        for (i in 0 until adapter.itemCount - 1) {
            if (adapter.items[i].hubId == hubId)
                removeIndex = i
        }
        adapter.items.remove(value)
        adapter.notifyItemRemoved(removeIndex)
        if (adapter.itemCount == 0) {
            NearByFindingFragment.showView = true
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onClickItem(dataModel: NearByHubModel) {
        val nearByDialog = NearByCheckInDialogFragment()
        nearByDialog.item = dataModel
        CheckInTEL.checkInTEL?.getLastCheckInHistory(object : TypeCallback {
            override fun onResponse(type: String?) {
                nearByDialog.typeFromLastCheckIn = type
                nearByDialog.show(activity?.supportFragmentManager, "show")
            }

            override fun onFailure(message: String?) {
                Toast.makeText(view?.context, " Error $message ", Toast.LENGTH_LONG).show()
            }

        })
    }

}
