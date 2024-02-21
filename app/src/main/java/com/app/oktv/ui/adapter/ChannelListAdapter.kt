package com.app.oktv.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.oktv.R
import com.app.oktv.ui.activity.VideoLiveActivity
import com.app.oktv.ui.model.ChannelDataModel

class ChannelListAdapter(private val mList: ArrayList<ChannelDataModel>): RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
        R.layout.raw_channel_list, parent, false))

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.cardView.setOnClickListener {
            context.startActivity(Intent(context,VideoLiveActivity::class.java))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.ivChannelImage)
        val cardView : CardView = itemView.findViewById(R.id.cvImageBackGround)
    }
}