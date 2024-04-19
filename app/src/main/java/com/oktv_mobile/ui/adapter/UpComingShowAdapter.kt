package com.oktv_mobile.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.ui.model.homemodel.ChannelProgramModel

class UpComingShowAdapter(private val mList: ArrayList<ChannelProgramModel>, private val posicion: String): RecyclerView.Adapter<UpComingShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_upcoming_show, parent, false))

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.tvChannelName.text = posicion + " " + mList[position].title

        if (position == 0) {
            holder.tvChannelName.setTextColor(context.getColor(R.color.yellow_59))
        } else {
            holder.tvChannelName.setTextColor(context.getColor(R.color.white))
        }
    }

    override fun getItemCount() = mList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvChannelName : TextView = itemView.findViewById(R.id.tvChannelName)
    }
}