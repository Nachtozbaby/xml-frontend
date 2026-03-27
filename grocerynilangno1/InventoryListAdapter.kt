package com.example.groceryinventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class InventoryListAdapter(
    private val onEdit: (InventoryItem) -> Unit,
    private val onDelete: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryListAdapter.InventoryViewHolder>() {

    private val items = mutableListOf<InventoryItem>()

    fun submitList(list: List<InventoryItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_row, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(items[position], onEdit, onDelete)
    }

    override fun getItemCount(): Int = items.size

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvSkuCategory: TextView = itemView.findViewById(R.id.tvSkuCategory)
        private val tvQtyPrice: TextView = itemView.findViewById(R.id.tvQtyPrice)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(item: InventoryItem, onEdit: (InventoryItem) -> Unit, onDelete: (InventoryItem) -> Unit) {
            tvName.text = item.name
            val lowStockLabel = if (item.quantity <= item.minStock) " - Low stock" else ""
            tvSkuCategory.text = "${item.sku} · ${item.category}$lowStockLabel"
            tvQtyPrice.text = "Qty: ${item.quantity} (Min: ${item.minStock}) · ₱" +
                String.format(Locale.US, "%,.2f", item.price)

            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
