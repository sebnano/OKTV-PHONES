package com.oktv_mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel

class GuideChannelAdapter(private val mList: ArrayList<ChannelDataModel>, private val showList : ArrayList<String>): RecyclerView.Adapter<GuideChannelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_guide_channel_list, parent, false))

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val innerAdapter = GuideShowAdapter(showList)
        holder.rvChannelShow.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = innerAdapter
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvChannelShow : RecyclerView = itemView.findViewById(R.id.rvChannelShow)
    }
}