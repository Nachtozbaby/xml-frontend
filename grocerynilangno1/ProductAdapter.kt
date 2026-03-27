package com.example.grocerydashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ProductAdapter(
    private val onAddClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val items = mutableListOf<Product>()

    fun submitList(list: List<Product>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onAddClicked)
    }

    override fun getItemCount(): Int = items.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val btnAdd: Button = itemView.findViewById(R.id.btnAdd)

        fun bind(product: Product, onAddClicked: (Product) -> Unit) {
            tvProductName.text = product.name
            tvProductCategory.text = product.category
            tvProductPrice.text = "₱" + String.format(Locale.US, "%,.2f", product.price)
            btnAdd.setOnClickListener { onAddClicked(product) }
        }
    }
}
