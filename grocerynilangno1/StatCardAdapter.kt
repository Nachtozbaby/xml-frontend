package com.example.grocerymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatCardAdapter : RecyclerView.Adapter<StatCardAdapter.StatViewHolder>() {
    private val items = mutableListOf<StatCard>()

    fun submitList(list: List<StatCard>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stat_card, parent, false)
        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    class StatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStatTitle: TextView = itemView.findViewById(R.id.tvStatTitle)
        private val tvStatValue: TextView = itemView.findViewById(R.id.tvStatValue)

        fun bind(item: StatCard, position: Int) {
            tvStatTitle.text = item.title
            tvStatValue.text = item.value
            val color = when (position) {
                0 -> "#22C55E"
                1 -> "#3B82F6"
                else -> "#8B5CF6"
            }
            tvStatValue.setTextColor(android.graphics.Color.parseColor(color))
        }
    }
}
