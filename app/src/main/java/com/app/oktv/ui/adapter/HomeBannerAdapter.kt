package com.app.oktv.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.oktv.R
import com.bumptech.glide.Glide

class HomeBannerAdapter(private val mList: ArrayList<Int>): RecyclerView.Adapter<HomeBannerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_banner, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
       Glide.with(context).load(mList[position]).into(holder.ivBanner)
    }

    override fun getItemCount() = mList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner : ImageView = itemView.findViewById(R.id.ivBanner)
    }
}