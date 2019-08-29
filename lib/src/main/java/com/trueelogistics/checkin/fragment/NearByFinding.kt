package com.trueelogistics.checkin.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

import com.trueelogistics.checkin.R
import kotlinx.android.synthetic.main.fragment_near_by_finding.*

class NearByFinding : Fragment() {

    private var mMessageListener: MessageListener? = null
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
        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message?) {
                val content = message?.content?.toString(
                    Charsets.UTF_8
                )
                loading_hub_nearby.visibility = View.GONE
                finding_text.visibility = View.GONE
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.frag_nearby, NearByHubFragment.newInstance(content ?: ""))
                    ?.commit()
                mMessageListener?.let { mML ->
                    activity?.let {
                        Nearby.getMessagesClient(it).unsubscribe(mML)
                    }
                }
            }

        }
    }

    private fun nearByAnimation() {
        loading_hub_nearby.setBackgroundResource(R.drawable.nearby_finding)
        nearbyAnimation = loading_hub_nearby.background as AnimationDrawable
        nearbyAnimation?.start()
    }

    override fun onStart() {
        super.onStart()
        mMessageListener?.let { mML ->
            activity?.let {
                Nearby.getMessagesClient(it).subscribe(mML)
            }
        }
    }
}
