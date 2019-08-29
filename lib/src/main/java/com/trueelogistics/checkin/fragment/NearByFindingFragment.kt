package com.trueelogistics.checkin.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.activity.NearByActivity
import kotlinx.android.synthetic.main.fragment_near_by_finding.*

class NearByFindingFragment : Fragment() {

    private var nearbyAnimation: AnimationDrawable? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by_finding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_page.setOnClickListener {
            activity?.onBackPressed()
        }
        nearByAnimation()
        activity?.let {
            NearByActivity().itemNearBy(it, object : NearByActivity.NearByCallback {
                override fun onFoundNearBy(hubId: String?) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.frag_nearby, NearByHubFragment())
                        ?.addToBackStack(null)
                        ?.commit()
                }

                override fun onLostNearBy(hubId: String?) {

                }

            })
        }

    }

    private fun nearByAnimation() {
        loading_hub_nearby.setBackgroundResource(R.drawable.nearby_finding)
        nearbyAnimation = loading_hub_nearby.background as AnimationDrawable
        nearbyAnimation?.start()
    }

}
