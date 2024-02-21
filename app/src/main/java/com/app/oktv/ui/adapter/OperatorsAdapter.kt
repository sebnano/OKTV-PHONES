package com.app.oktv.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.oktv.R
import com.app.oktv.ui.model.OperatorModel
import com.app.oktv.utils.hide
import com.app.oktv.utils.show
import kotlinx.android.synthetic.main.raw_oprator_list.view.*

@Suppress("KotlinConstantConditions")
class OperatorsAdapter(private val mList: ArrayList<OperatorModel>): RecyclerView.Adapter<OperatorsAdapter.ViewHolder>() {

    var selectedOperator = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_oprator_list, parent, false))

    override fun getItemCount() = mList.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val data = mList[position]
        holder.tvOperatorName.text = data.name
        holder.tvOperatorSelected.text =  context.getString(R.string.selected)
        holder.itemView.clMain.setOnClickListener {
            selectedOperator = position
            notifyDataSetChanged()
        }

        if (position == selectedOperator) {
            holder.tvOperatorSelected.show()
        } else {
            holder.tvOperatorSelected.hide()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOperatorName : TextView = itemView.findViewById(R.id.tvOperatorName)
        val tvOperatorSelected : TextView = itemView.findViewById(R.id.tvOperatorSelected)
    }
}