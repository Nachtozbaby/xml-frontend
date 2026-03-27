package com.example.grocerydashboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<String>,
    private var selectedCategory: String,
    private val onCategoryClicked: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    fun setSelected(category: String) {
        selectedCategory = category
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val tv = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return CategoryViewHolder(tv)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], categories[position] == selectedCategory, onCategoryClicked)
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView as TextView

        fun bind(category: String, isSelected: Boolean, onCategoryClicked: (String) -> Unit) {
            textView.text = category
            textView.textSize = 13f
            textView.setPadding(30, 12, 30, 12)
            textView.layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { rightMargin = 12 }

            val bg = GradientDrawable().apply {
                cornerRadius = 24f
                setColor(if (isSelected) Color.parseColor("#22C55E") else Color.parseColor("#1A1A2E"))
            }
            textView.background = bg
            textView.setTextColor(if (isSelected) Color.WHITE else Color.parseColor("#8888A0"))
            textView.setOnClickListener { onCategoryClicked(category) }
        }
    }
}
