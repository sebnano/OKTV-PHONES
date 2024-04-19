package com.oktv_mobile.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.model.loginmodel.UserOperatorModel
import com.oktv_mobile.utils.SharedPreferencesHelper
import com.oktv_mobile.utils.inivisible
import com.oktv_mobile.utils.show
import com.oktv_mobile.ui.`interface`.UpdateDevice
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.raw_oprator_list.view.*
import kotlinx.android.synthetic.main.raw_oprator_list.view.clMain

class OperatorsAdapter(private val mList: ArrayList<UserOperatorModel>,
                       private val listener: UpdateDevice,
                       operatorId :String,
                       private var selectedText: String
                      ): RecyclerView.Adapter<OperatorsAdapter.ViewHolder>() {
    var isSelected = operatorId
    var operatorName = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.raw_oprator_list, parent, false))

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("isSelected","------isSelected"+isSelected)
        holder.tvOperatorName.text = mList[position].operatorName

        holder.itemView.clMain.setOnClickListener {
            listener.deviceData(mList[position].operador, mList[position].maxDevices,mList[position].operatorName)
            notifyDataSetChanged()
        }

        if (mList[position].operador == isSelected) {
            holder.itemView.tvOperatorSelected.show()
            (holder.itemView.context as HomeActivity).tvChannelName.text = operatorName
        } else {
            holder.itemView.tvOperatorSelected.inivisible()
        }

        holder.itemView.tvOperatorSelected.text = selectedText
    }

    override fun getItemCount() = mList.size

    @SuppressLint("NotifyDataSetChanged")
    fun operaterId(operatorId: String,operator: String) {
        Log.e("isSelected","------isSelected222"+isSelected)
        isSelected = operatorId
        operatorName = operator
        notifyDataSetChanged()
    }

    fun changeSelected(selectedText: String) {
        this.selectedText = selectedText
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOperatorName : TextView = itemView.findViewById(R.id.tvOperatorName)
    }
}