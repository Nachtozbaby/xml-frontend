package com.example.groceryinventory

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import kotlin.math.max

data class InventoryItem(
    val id: String,
    var name: String,
    var sku: String,
    var category: String,
    var quantity: Int,
    var minStock: Int,
    var price: Double,
    var description: String
)

class InventoryActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var spCategory: Spinner
    private lateinit var rvInventory: RecyclerView
    private lateinit var btnAddItem: Button

    private val categories = mutableListOf("All", "Beverages", "Instant", "Canned", "Dairy", "Snacks", "Bakery")
    private val items = mutableListOf<InventoryItem>()

    private lateinit var listAdapter: InventoryListAdapter
    private lateinit var categoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        etSearch = findViewById(R.id.etSearch)
        spCategory = findViewById(R.id.spCategory)
        rvInventory = findViewById(R.id.rvInventory)
        btnAddItem = findViewById(R.id.btnAddItem)

        seedInventory()
        setupCategorySpinner()
        setupList()
        setupEvents()
        filterInventory()
    }

    private fun setupCategorySpinner() {
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = categoryAdapter
    }

    private fun setupList() {
        listAdapter = InventoryListAdapter(
            onEdit = { item -> openItemDialog(item) },
            onDelete = { item -> deleteItem(item.id) }
        )
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = listAdapter
    }

    private fun setupEvents() {
        etSearch.addTextChangedListener { filterInventory() }
        spCategory.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterInventory()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        })
        btnAddItem.setOnClickListener { openItemDialog(null) }
    }

    private fun filterInventory() {
        val query = etSearch.text.toString().trim().lowercase(Locale.getDefault())
        val selectedCategory = spCategory.selectedItem?.toString() ?: "All"

        val filtered = items.filter { item ->
            val matchesSearch = item.name.lowercase(Locale.getDefault()).contains(query) ||
                item.sku.lowercase(Locale.getDefault()).contains(query)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            matchesSearch && matchesCategory
        }
        listAdapter.submitList(filtered)
    }

    private fun openItemDialog(existing: InventoryItem?) {
        val content = LayoutInflater.from(this).inflate(R.layout.dialog_inventory_item, null, false)
        val etName = content.findViewById<EditText>(R.id.etName)
        val etSku = content.findViewById<EditText>(R.id.etSku)
        val spCategoryDialog = content.findViewById<Spinner>(R.id.spCategoryDialog)
        val etQuantity = content.findViewById<EditText>(R.id.etQuantity)
        val etMinStock = content.findViewById<EditText>(R.id.etMinStock)
        val etPrice = content.findViewById<EditText>(R.id.etPrice)
        val etDescription = content.findViewById<EditText>(R.id.etDescription)

        val dialogCategories = categories.filter { it != "All" }
        val dialogCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dialogCategories)
        dialogCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoryDialog.adapter = dialogCategoryAdapter

        if (existing != null) {
            etName.setText(existing.name)
            etSku.setText(existing.sku)
            etQuantity.setText(existing.quantity.toString())
            etMinStock.setText(existing.minStock.toString())
            etPrice.setText(existing.price.toString())
            etDescription.setText(existing.description)
            val selectedIndex = max(0, dialogCategories.indexOf(existing.category))
            spCategoryDialog.setSelection(selectedIndex)
        }

        AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Add Item" else "Edit Item")
            .setView(content)
            .setNegativeButton("Cancel", null)
            .setPositiveButton(if (existing == null) "Add" else "Update") { _, _ ->
                val name = etName.text.toString().trim()
                val sku = etSku.text.toString().trim()
                val category = spCategoryDialog.selectedItem?.toString() ?: "Uncategorized"
                val quantity = etQuantity.text.toString().toIntOrNull() ?: 0
                val minStock = etMinStock.text.toString().toIntOrNull() ?: 0
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val description = etDescription.text.toString().trim()

                if (name.isBlank() || sku.isBlank()) return@setPositiveButton

                if (existing == null) {
                    val nextId = (items.size + 1).toString()
                    items.add(
                        InventoryItem(
                            id = nextId,
                            name = name,
                            sku = sku,
                            category = category,
                            quantity = quantity,
                            minStock = minStock,
                            price = price,
                            description = description
                        )
                    )
                } else {
                    existing.name = name
                    existing.sku = sku
                    existing.category = category
                    existing.quantity = quantity
                    existing.minStock = minStock
                    existing.price = price
                    existing.description = description
                }
                filterInventory()
            }
            .show()
    }

    private fun deleteItem(itemId: String) {
        items.removeAll { it.id == itemId }
        filterInventory()
    }

    private fun seedInventory() {
        items.clear()
        items.addAll(
            listOf(
                InventoryItem("1", "Coke 1.5L", "BEV-001", "Beverages", 23, 10, 95.0, "Softdrinks"),
                InventoryItem("2", "Sprite 1.5L", "BEV-002", "Beverages", 15, 8, 92.0, "Lemon soda"),
                InventoryItem("3", "Pancit Canton", "INS-001", "Instant", 50, 20, 18.0, "Lucky Me"),
                InventoryItem("4", "Century Tuna", "CAN-001", "Canned", 12, 12, 38.0, "Hot and spicy"),
                InventoryItem("5", "Bear Brand 300g", "DAI-001", "Dairy", 9, 10, 88.0, "Powdered milk"),
                InventoryItem("6", "Piattos 85g", "SNK-001", "Snacks", 34, 15, 52.0, "Cheese flavor")
            )
        )
    }
}
