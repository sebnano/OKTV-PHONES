package com.app.oktv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.oktv.R
import com.app.oktv.ui.adapter.GuideChannelAdapter
import com.app.oktv.ui.model.ChannelDataModel
import kotlinx.android.synthetic.main.fragment_guide.*

class GuideFragment : Fragment() {

    /** This Variable Used for Adapter **/
    private lateinit var channelListAdapter: GuideChannelAdapter

    /** This Variable Used for Store Api Data **/
    private var channelList = ArrayList<ChannelDataModel>()
    private var showList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        changeText()
    }

    /** This Function For Initialize **/
    private fun initData() {
        channelList.clear()
        showList.clear()

        for (i in 0..10) {
            channelList.add(ChannelDataModel(R.drawable.channel_icon))
            showList.add("9:00 | Cartoon")
        }


        channelListAdapter = GuideChannelAdapter(channelList,showList)
        rvGuide.layoutManager = LinearLayoutManager(requireContext())
        rvGuide.adapter = channelListAdapter

    }

    /** This Function For Change Text **/
    private fun changeText() {
        tvTitle.text = getString(R.string.guide)
    }
}