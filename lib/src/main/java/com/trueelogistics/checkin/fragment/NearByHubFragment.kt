package com.trueelogistics.checkin.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.adapter.NearByAdapter
import kotlinx.android.synthetic.main.fragment_near_by_hub.*

class NearByHubFragment : Fragment() {
    private var adapter = NearByAdapter()
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
        nearbyRecycle.adapter = adapter
        activity?.let {
            nearbyRecycle?.layoutManager = LinearLayoutManager(it)
            adapter.items.add(arguments?.getString(HUB_ID).toString())
        }
    }


}
