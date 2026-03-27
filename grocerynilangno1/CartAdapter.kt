package com.example.grocerydashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class CartAdapter : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val items = mutableListOf<CartItem>()

    fun submitList(list: List<CartItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCartName: TextView = itemView.findViewById(R.id.tvCartName)
        private val tvCartPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        private val tvCartQty: TextView = itemView.findViewById(R.id.tvCartQty)

        fun bind(item: CartItem) {
            tvCartName.text = item.product.name
            tvCartQty.text = "x${item.quantity}"
            val lineTotal = item.product.price * item.quantity
            tvCartPrice.text = "₱" + String.format(Locale.US, "%,.2f", lineTotal)
        }
    }
}
