package com.oktv_mobile.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.ui.`interface`.ChannelItemCallback
import com.oktv_mobile.utils.show
import com.oktv_mobile.ui.model.homemodel.CategoryChannelModel
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel
import com.oktv_mobile.utils.hide
import kotlinx.android.synthetic.main.raw_channel_category_list.view.*

class ChannelCategorySecondAdapter(private val mList : ArrayList<CategoryChannelModel>,
                                   private val listener: ChannelItemCallback,
                                   private var selectedPosition: Int):
    RecyclerView.Adapter<ChannelCategorySecondAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_channel_category_list, parent, false))

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.tvChannelName.text =  mList[position].category

        holder.clChannelName.setOnClickListener {
            selectedPosition = if (position == selectedPosition) {
                -1
            } else {
                position
            }
            notifyDataSetChanged()
        }

        val innerAdapter = ChannelListAdapter(mList[position].categoryList ?: ArrayList<ChannelDataModel>(), listener, position)
        holder.rvAllChannel.layoutManager = GridLayoutManager(context,3)
        holder.rvAllChannel.adapter = innerAdapter

        if (selectedPosition == position) {
            holder.ivArrowAllChannel.setImageResource(R.drawable.ic_arrow_down_yellow)
            holder.rvAllChannel.show()
        } else {
            holder.ivArrowAllChannel.setImageResource(R.drawable.ic_arrow_right_yellow)
            holder.rvAllChannel.hide()
        }
    }

    override fun getItemCount() = mList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvAllChannel : RecyclerView = itemView.findViewById(R.id.rvAllChannel)
        val clChannelName : ConstraintLayout = itemView.findViewById(R.id.clChannelName)
        val tvChannelName : TextView = itemView.findViewById(R.id.tvChannelName)
        val ivArrowAllChannel : ImageView = itemView.findViewById(R.id.ivArrowAllChannel)
    }
}