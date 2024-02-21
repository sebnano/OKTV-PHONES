package com.app.oktv.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.oktv.R
import com.app.oktv.ui.model.ChannelDataModel
import com.app.oktv.utils.hide
import com.app.oktv.utils.show
import kotlinx.android.synthetic.main.raw_channel_category_list.view.*

class ChannelCategoryAdapter(private val channelNameList : ArrayList<String>, private val channelList : ArrayList<ChannelDataModel>): RecyclerView.Adapter<ChannelCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_channel_category_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.rvAllChannel.hide()

        val innerAdapter = ChannelListAdapter(channelList)
        holder.itemView.rvAllChannel.apply {
            layoutManager = GridLayoutManager(context,3)
            adapter = innerAdapter
        }

        holder.tvAllChannel.text = channelNameList[position]

        holder.clChannelName.setOnClickListener {
            holder.rvAllChannel.visibility = if (holder.rvAllChannel.visibility == View.VISIBLE) {
                holder.ivArrowAllChannel.setImageResource(R.drawable.ic_arrow_right_yellow)
                View.GONE
            } else {
                holder.ivArrowAllChannel.setImageResource(R.drawable.ic_arrow_down_yellow)
                View.VISIBLE
            }
        }

        if (position == 0) {
            holder.rvAllChannel.show()
            holder.ivArrowAllChannel.setImageResource(R.drawable.ic_arrow_down_yellow)
        }
    }

    override fun getItemCount() = channelNameList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clChannelName : ConstraintLayout = itemView.findViewById(R.id.clChannelName)
        val rvAllChannel : RecyclerView = itemView.findViewById(R.id.rvAllChannel)
        val ivArrowAllChannel : ImageView = itemView.findViewById(R.id.ivArrowAllChannel)
        val tvAllChannel : TextView = itemView.findViewById(R.id.tvAllChannel)
    }
}