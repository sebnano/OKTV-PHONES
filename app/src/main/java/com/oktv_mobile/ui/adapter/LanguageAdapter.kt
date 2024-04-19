package com.oktv_mobile.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.utils.Constant
import com.oktv_mobile.utils.MyPreferences
import com.oktv_mobile.ui.`interface`.LanguageCallBack
import com.oktv_mobile.ui.model.loginmodel.ConfiguracionModel
import kotlinx.android.synthetic.main.raw_language_list.view.*

class LanguageAdapter(private val mList: ArrayList<ConfiguracionModel>, private val listener: LanguageCallBack): RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.raw_language_list, parent, false))

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]

        holder.itemView.tvLanguageName.text = data.value
        MyPreferences.saveStringInPreference(holder.itemView.context, Constant.languageCode,data.key)

        holder.itemView.tvLanguageName.setOnClickListener {
            listener.language(position)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}