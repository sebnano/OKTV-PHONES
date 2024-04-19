package com.oktv_mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R

class GuideShowAdapter(private val mList: ArrayList<String>): RecyclerView.Adapter<GuideShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_show_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvShowName.text = mList[position]
    }

    override fun getItemCount() = mList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvShowName : TextView = itemView.findViewById(R.id.tvShowName)
    }
}