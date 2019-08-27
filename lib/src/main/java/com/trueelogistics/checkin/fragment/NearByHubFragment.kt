package com.trueelogistics.checkin.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.adapter.NearByAdapter
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
import com.trueelogistics.checkin.interfaces.OnClickItemCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.HubInDataModel
import com.trueelogistics.checkin.model.NearByHubModel
import kotlinx.android.synthetic.main.fragment_near_by_hub.*

class NearByHubFragment : Fragment(), OnClickItemCallback {

    private var adapter = NearByAdapter(this)
    private var mMessageListener: MessageListener? = null


    companion object {
        const val HUB_ID = "HUB_ID"
        fun newInstance(hubId: String): NearByHubFragment {
            val fragment = NearByHubFragment()
            val bundle = Bundle().apply {
                putString("HUB_ID", hubId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by_hub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var hubNameFromService: String? = null
        val arrayItem = arrayListOf<NearByHubModel>()
        CheckInTEL.checkInTEL?.hubGenerater(object : ArrayListGenericCallback<HubInDataModel> {
            override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                dataModel?.forEach {
                    if (it._id == arguments?.getString(HUB_ID).toString())
                        hubNameFromService = it.locationName
                }
                arrayItem.add(
                    NearByHubModel(
                        arguments?.getString(HUB_ID).toString()
                        , hubNameFromService
                    )
                )
                setAdapter(arrayItem)
            }

            override fun onFailure(message: String?) {
                Toast.makeText(
                    view.context, " onFailure : $message "
                    , Toast.LENGTH_LONG
                ).show()
            }

        })

        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                arrayItem.add(
                    NearByHubModel(
                        content.toString()
                        , hubNameFromService
                    )
                )
                setAdapter(arrayItem)
            }

            override fun onLost(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                val lostItem = arrayItem.filter {
                    id -> id.hubId == content
                }
                arrayItem.remove(lostItem[0])
                if (adapter.items.size == 0) {
                    activity?.supportFragmentManager?.fragments?.remove(this@NearByHubFragment)
                }
                setAdapter(arrayItem)
            }
        }

    }

    fun setAdapter(  arrayItem : ArrayList<NearByHubModel> ){
        nearbyRecycle.adapter = adapter
        nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
        adapter.items.addAll(arrayItem)
        val num = adapter.itemCount
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
