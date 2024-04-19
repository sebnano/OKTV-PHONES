package com.oktv_mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.ui.model.homemodel.UserDeviceModel

class DeviceListAdapter(private val mList: ArrayList<UserDeviceModel>): RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_device_list, parent, false))

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]

        holder.tvDeviceName.text = data.deviceTitle
        holder.tvDeviceCode.text = data.deviceIp
        holder.tvDeviceId.text = data.deviceMac


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeviceName : TextView = itemView.findViewById(R.id.tvDeviceName)
        val tvDeviceCode : TextView = itemView.findViewById(R.id.tvDeviceCode)
        val tvDeviceId : TextView = itemView.findViewById(R.id.tvDeviceId)
    }
}