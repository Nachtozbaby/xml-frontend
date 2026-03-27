package com.example.grocerymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RestockAdapter : RecyclerView.Adapter<RestockAdapter.RestockViewHolder>() {
    private val items = mutableListOf<RestockSuggestion>()

    fun submitList(list: List<RestockSuggestion>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restock_row, parent, false)
        return RestockViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestockViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class RestockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRestockName: TextView = itemView.findViewById(R.id.tvRestockName)
        private val tvRestockMessage: TextView = itemView.findViewById(R.id.tvRestockMessage)

        fun bind(item: RestockSuggestion) {
            tvRestockName.text = item.itemName
            tvRestockMessage.text = item.message
        }
    }
}
