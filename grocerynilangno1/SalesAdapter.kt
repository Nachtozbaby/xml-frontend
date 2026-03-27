package com.example.grocerymanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class SalesAdapter : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {
    private val items = mutableListOf<SaleOrder>()
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun submitList(list: List<SaleOrder>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sale_row, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(items[position], dateFormat)
    }

    override fun getItemCount(): Int = items.size

    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvOrderItems: TextView = itemView.findViewById(R.id.tvOrderItems)
        private val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)

        fun bind(item: SaleOrder, dateFormat: SimpleDateFormat) {
            tvOrderNumber.text = item.orderNumber
            tvOrderDate.text = dateFormat.format(item.date)
            tvOrderItems.text = "${item.totalItems} items"
            tvOrderTotal.text = "₱" + String.format(Locale.US, "%,.2f", item.total)
        }
    }
}
