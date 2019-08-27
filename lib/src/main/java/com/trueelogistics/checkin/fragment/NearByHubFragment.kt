package com.trueelogistics.checkin.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.adapter.NearByAdapter
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.OnClickItemCallback
import com.trueelogistics.checkin.interfaces.TypeCallback
import com.trueelogistics.checkin.model.NearByHubModel
import kotlinx.android.synthetic.main.fragment_near_by_hub.*

class NearByHubFragment : Fragment(), OnClickItemCallback {

    private var adapter = NearByAdapter(this)


    companion object {
        var arrayItem = arrayListOf<NearByHubModel>()
        const val HUB_ID = "HUB_ID"
        fun newInstance(hubId: String): NearByHubFragment {
            val fragment = NearByHubFragment()
            val bundle = Bundle().apply {
                putString(HUB_ID, hubId)
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

        nearbyRecycle.adapter = adapter
        nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
        adapter.items.addAll(arrayItem)
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
