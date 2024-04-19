/*
 * Copyright (c) 2020, Egeniq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.egeniq.androidtvprogramguide.row

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.egeniq.androidtvprogramguide.*
import com.egeniq.androidtvprogramguide.entity.ProgramGuideChannel
import com.egeniq.androidtvprogramguide.entity.ProgramGuideSchedule
import java.util.*

/**
 * Adapts the [ProgramGuideListAdapter] list to the body of the program guide table.
 */
internal class ProgramGuideRowAdapter(
    private val context: Context,
    private val programGuideHolder: ProgramGuideHolder<*>
) :
    RecyclerView.Adapter<ProgramGuideRowAdapter.ProgramRowViewHolder>(),
    ProgramGuideManager.Listener {
    private val programManager: ProgramGuideManager<*> = programGuideHolder.programGuideManager
    private val programListAdapters = ArrayList<ProgramGuideListAdapter<*>>()
    private val recycledViewPool: RecyclerView.RecycledViewPool =
        RecyclerView.RecycledViewPool().also {
            it.setMaxRecycledViews(
                R.layout.programguide_item_row,
                context.resources.getInteger(R.integer.programguide_max_recycled_view_pool_table_item)
            )
        }

    companion object {
        private val TAG: String = ProgramGuideRowAdapter::class.java.name
    }

    init {
        update()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        programListAdapters.clear()
        val channelCount = programManager.channelCount
        for (i in 0 until channelCount) {
            val listAdapter = ProgramGuideListAdapter(context.resources, programGuideHolder, i)
            programListAdapters.add(listAdapter)
        }
        Log.i(TAG, "Updating program guide with $channelCount channels.")
        notifyDataSetChanged()
    }

    fun updateProgram(program: ProgramGuideSchedule<*>): Int? {
        // Find the match in the row adapters
        programListAdapters.forEachIndexed { index, adapter ->
            if (adapter.updateProgram(program)) {
                return index
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        return programListAdapters.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.programguide_item_row
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ProgramRowViewHolder, position: Int) {
        holder.onBind(position, programManager, programListAdapters, programGuideHolder)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramRowViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val gridView = itemView.findViewById<ProgramGuideRowGridView>(R.id.row)
        gridView.setRecycledViewPool(recycledViewPool)
        return ProgramRowViewHolder(itemView)
    }

    override fun onTimeRangeUpdated() {
        // Do nothing
    }

    override fun onSchedulesUpdated() {
        // Do nothing
    }

    internal class ProgramRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val container: ViewGroup = itemView as ViewGroup
//        val touchInterceptLayout : TouchInterceptLayout = container.findViewById(R.id.touch_intercept_layout)
        private val rowGridView: ProgramGuideRowGridView = container.findViewById(R.id.row)

        private val channelNameView: TextView = container.findViewById(R.id.programguide_channel_name)
        private val channelLogoView: ImageView = container.findViewById(R.id.programguide_channel_logo)

        init {
            val channelContainer = container.findViewById<ViewGroup>(R.id.programguide_channel_container)
            channelContainer.viewTreeObserver.addOnGlobalFocusChangeListener { _, _ ->
                channelContainer.isActivated = rowGridView.hasFocus()
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun onBind(
            position: Int,
            programManager: ProgramGuideManager<*>,
            programListAdapters: List<RecyclerView.Adapter<*>>,
            programGuideHolder: ProgramGuideHolder<*>
        ) {
            onBindChannel(programGuideHolder, programManager.getChannel(position))
            rowGridView.swapAdapter(programListAdapters[position], true)
            rowGridView.setProgramGuideFragment(programGuideHolder)
            rowGridView.setChannel(programManager.getChannel(position)!!)
            rowGridView.stopScroll()

            rowGridView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                val dy = scrollX - oldScrollX
                programGuideHolder.onRowHorizontalScrolled(dy)
            }

//            touchInterceptLayout.setProgramGuideGridView(rowGridView)
//            rowGridView.resetScroll(programGuideHolder.getTimelineRowScrollOffset())
        }

        private fun onBindChannel(
            programGuideHolder: ProgramGuideHolder<*>, channel: ProgramGuideChannel?) {
            if (channel == null) {
                channelNameView.visibility = View.GONE
                channelLogoView.visibility = View.GONE
                return
            }
            val imageUrl = channel.imageUrl
            if (imageUrl == null) {
                channelLogoView.visibility = View.GONE
            } else {
                Glide.with(channelLogoView)
                    .load(imageUrl)
                    .fitCenter()
                    .into(channelLogoView)
                channelLogoView.visibility = View.VISIBLE
            }
            channelNameView.text = channel.name
            channelNameView.visibility = View.GONE

            channelLogoView.setOnClickListener {
                programGuideHolder.onChannelLogoClicked(channel)
            }
        }

        internal fun updateLayout() {
            rowGridView.post {
                rowGridView.updateChildVisibleArea()
            }
        }
    }
}