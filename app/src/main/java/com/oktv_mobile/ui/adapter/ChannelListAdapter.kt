package com.oktv_mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oktv_mobile.R
import com.oktv_mobile.ui.`interface`.ChannelItemCallback
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel
import com.oktv_mobile.utils.Constant

class ChannelListAdapter(private val arrChannels: ArrayList<ChannelDataModel>,
                         private val listener: ChannelItemCallback,
                         private var parentPosition: Int):
    RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
        R.layout.raw_channel_list, parent, false))

    override fun getItemCount() = arrChannels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(Constant.BaseUrl + arrChannels[position].fieldLogo)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.ic_ok_tv_logo)
            .into(holder.image)
        holder.cardView.setOnClickListener {
            listener.onItemClick(parentPosition, position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.ivChannelImage)
        val cardView : CardView = itemView.findViewById(R.id.cvImageBackGround)
    }

}