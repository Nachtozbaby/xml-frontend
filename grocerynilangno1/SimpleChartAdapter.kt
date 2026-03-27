package com.example.grocerymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleChartAdapter : RecyclerView.Adapter<SimpleChartAdapter.ChartViewHolder>() {
    private val items = mutableListOf<DayRevenue>()
    private var maxRevenue: Double = 1.0

    fun submitList(list: List<DayRevenue>) {
        items.clear()
        items.addAll(list)
        maxRevenue = (items.maxOfOrNull { it.revenue } ?: 1.0).coerceAtLeast(1.0)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_chart_bar, parent, false)
        return ChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        holder.bind(items[position], maxRevenue)
    }

    override fun getItemCount(): Int = items.size

    class ChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vBar: View = itemView.findViewById(R.id.vBar)
        private val tvBarLabel: TextView = itemView.findViewById(R.id.tvBarLabel)

        fun bind(item: DayRevenue, maxRevenue: Double) {
            tvBarLabel.text = item.label

            val minHeight = 20
            val maxHeight = 110
            val ratio = (item.revenue / maxRevenue).toFloat().coerceIn(0f, 1f)
            val targetHeight = (minHeight + ((maxHeight - minHeight) * ratio)).toInt()

            val params = vBar.layoutParams
            params.height = targetHeight
            vBar.layoutParams = params
        }
    }
}
