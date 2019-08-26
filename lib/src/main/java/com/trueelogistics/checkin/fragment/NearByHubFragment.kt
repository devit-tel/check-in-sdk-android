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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.adapter.NearByAdapter
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.GenerateQrCallback
import com.trueelogistics.checkin.interfaces.OnClickItemCallback
import com.trueelogistics.checkin.model.NearByHubModel
import kotlinx.android.synthetic.main.fragment_near_by_hub.*

class NearByHubFragment : Fragment() , OnClickItemCallback {

    private var adapter = NearByAdapter( this )
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
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                getQr( content.toString() )
            }

            override fun onLost(message: Message?) {
                nearbyRecycle.adapter = adapter
                nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
                adapter.items.remove( NearByHubModel(arguments?.getString(HUB_ID).toString()) )
            }
        }
            getQr(arguments?.getString(HUB_ID).toString())

    }

    override fun onClickItem(view: View, dataModel: NearByHubModel) {

    }

    fun getQr( hubId: String) {
        activity?.let { activity ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_COARSE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    ?.addOnSuccessListener { location: Location? ->
                        if (location?.isFromMockProvider == false) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            CheckInTEL.checkInTEL?.qrGenerate( hubId , hubId ,
                                latitude.toString(), longitude.toString(),
                                object : GenerateQrCallback {
                                    override fun onFailure(message: String?) {

                                    }

                                    override fun onResponse(hubName: String?, qrCodeText: String?, time: String?) {
                                        nearbyRecycle.adapter = adapter
                                        nearbyRecycle?.layoutManager = LinearLayoutManager(activity)
                                        adapter.items.add(NearByHubModel(hubId,hubName,qrCodeText))
                                    }
                                })
                        } else {
                            MockDialogFragment().show(activity.supportFragmentManager, "show")
                        }
                    }
            }
        }
    }
}
