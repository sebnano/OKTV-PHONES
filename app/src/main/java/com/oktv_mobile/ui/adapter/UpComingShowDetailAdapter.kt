package com.oktv_mobile.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.ui.model.homemodel.ChannelProgramModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpComingShowDetailAdapter(private val mList: ArrayList<ChannelProgramModel>): RecyclerView.Adapter<UpComingShowDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_upcoming_show_details, parent, false))

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val formattedTimeStart = outputFormat.format(inputFormat.parse(mList[position].start.toString()))
        val formattedTimeStop = outputFormat.format(inputFormat.parse(mList[position].stop.toString()))
        holder.tvShowName.text = mList[position].title
        holder.tvShowTime.text = "$formattedTimeStart to $formattedTimeStop"
    }

    override fun getItemCount() = mList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvShowName : TextView = itemView.findViewById(R.id.tvShowName)
        val tvShowTime : TextView = itemView.findViewById(R.id.tvShowTime)
    }
}