package com.trueelogistics.example

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trueelogistics.checkin.nearby.NearByActivity
import kotlinx.android.synthetic.main.fragment_near_by.*

class NearByFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_near_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        toolbar.setOnClickListener {
            mainActivity.actionToolbar()
        }
        nearby_phone_fine.setOnClickListener {
            val intent = Intent(activity, NearByActivity::class.java)
            this.startActivity(intent)
        }
    }
}
