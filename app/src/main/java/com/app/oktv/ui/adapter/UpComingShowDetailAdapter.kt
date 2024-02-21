package com.app.oktv.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.oktv.R
import com.app.oktv.ui.model.UpComingShowModel

class UpComingShowDetailAdapter(private val mList: ArrayList<UpComingShowModel>): RecyclerView.Adapter<UpComingShowDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_upcoming_show_details, parent, false))

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvShowName.text = mList[position].title
        holder.tvShowTime.text = mList[position].time
    }

    override fun getItemCount() = mList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvShowName : TextView = itemView.findViewById(R.id.tvShowName)
        val tvShowTime : TextView = itemView.findViewById(R.id.tvShowTime)
    }
}