package com.app.oktv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.oktv.R
import com.app.oktv.ui.adapter.ChannelCategoryAdapter
import com.app.oktv.ui.model.ChannelDataModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    /** This Variable Used for Adapter **/
    private lateinit var channelCategoryAdapter : ChannelCategoryAdapter

    /** This Variable Used for Store Api Data **/
    private var channelList = ArrayList<ChannelDataModel>()
    private var channelNameList = ArrayList<String>()


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
        return inflater.inflate(R.layout.fragment_home, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()
        changeText()
    }

    /** This Function For Initialize **/
    private fun  initData() {
        channelNameList.clear()
        channelNameList.add("All the channels")
        channelNameList.add("National")
        channelNameList.add("Films")
        channelNameList.add("News")

        channelList.clear()
        for ( i in 0 until 15) {
            channelList.add(ChannelDataModel(R.drawable.channel_icon))
        }

        channelCategoryAdapter = ChannelCategoryAdapter(channelNameList,channelList)
        rvChannel.layoutManager = LinearLayoutManager(requireContext())
        rvChannel.adapter = channelCategoryAdapter
    }

    /** This Function For Change Text **/
    private fun changeText() {
        tvTitle.text = getString(R.string.channels)
    }
}