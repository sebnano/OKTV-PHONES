package com.app.oktv.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.oktv.R
import com.app.oktv.ui.model.DeviceDataModel

class DeviceListAdapter(private val mList: ArrayList<DeviceDataModel>): RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_device_list, parent, false))

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]

        holder.tvDeviceName.text = data.title
        holder.tvDeviceCode.text = data.code
        holder.tvDeviceId.text = data.id
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeviceName : TextView = itemView.findViewById(R.id.tvDeviceName)
        val tvDeviceCode : TextView = itemView.findViewById(R.id.tvDeviceCode)
        val tvDeviceId : TextView = itemView.findViewById(R.id.tvDeviceId)
    }
}