package com.example.grocerydashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvCart: RecyclerView
    private lateinit var rvCategories: RecyclerView

    private val products = mutableListOf<Product>()
    private val cart = mutableListOf<CartItem>()
    private var selectedCategory: String = "All"

    private lateinit var productAdapter: ProductAdapter
    private lateinit var cartAdapter: CartAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            val current = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
            tvTime.text = current
            timerHandler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bindViews()
        seedProducts()
        setupRecyclerViews()
        refreshProducts()
        updateCartUi()

        btnCheckout.setOnClickListener {
            // Frontend-only placeholder action.
            btnCheckout.text = "Checkout UI Ready"
        }
    }

    override fun onStart() {
        super.onStart()
        timerHandler.post(timerRunnable)
    }

    override fun onStop() {
        super.onStop()
        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun bindViews() {
        tvTime = findViewById(R.id.tvTime)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        rvProducts = findViewById(R.id.rvProducts)
        rvCart = findViewById(R.id.rvCart)
        rvCategories = findViewById(R.id.rvCategories)
    }

    private fun setupRecyclerViews() {
        productAdapter = ProductAdapter { product ->
            addToCart(product)
        }

        cartAdapter = CartAdapter()

        categoryAdapter = CategoryAdapter(
            categories = buildCategories(),
            selectedCategory = selectedCategory
        ) { clickedCategory ->
            selectedCategory = clickedCategory
            categoryAdapter.setSelected(clickedCategory)
            refreshProducts()
        }

        rvProducts.layoutManager = GridLayoutManager(this, 2)
        rvProducts.adapter = productAdapter

        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = cartAdapter

        rvCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = categoryAdapter
    }

    private fun seedProducts() {
        products.clear()
        products.addAll(
            listOf(
                Product("1", "Coke 1.5L", "Beverages", 95.0),
                Product("2", "Sprite 1.5L", "Beverages", 92.0),
                Product("3", "Lucky Me Pancit Canton", "Instant", 18.0),
                Product("4", "Century Tuna", "Canned", 38.0),
                Product("5", "Bear Brand 300g", "Dairy", 88.0),
                Product("6", "Piattos 85g", "Snacks", 52.0),
                Product("7", "Gardenia Bread", "Bakery", 78.0),
                Product("8", "Argentina Corned Beef", "Canned", 45.0)
            )
        )
    }

    private fun buildCategories(): List<String> {
        val dynamicCategories = products.map { it.category }.distinct().sorted()
        return listOf("All") + dynamicCategories
    }

    private fun refreshProducts() {
        val filtered = if (selectedCategory == "All") {
            products
        } else {
            products.filter { it.category == selectedCategory }
        }
        productAdapter.submitList(filtered)
    }

    private fun addToCart(product: Product) {
        val existing = cart.firstOrNull { it.product.id == product.id }
        if (existing == null) {
            cart.add(CartItem(product, 1))
        } else {
            existing.quantity += 1
        }
        updateCartUi()
    }

    private fun updateCartUi() {
        cartAdapter.submitList(cart.map { it.copy() })

        val subtotal = cart.sumOf { it.product.price * it.quantity }
        tvSubtotal.text = "Subtotal: ${formatPeso(subtotal)}"
        tvTotal.text = "Total: ${formatPeso(subtotal)}"

        btnCheckout.isEnabled = cart.isNotEmpty()
        if (cart.isEmpty()) {
            btnCheckout.text = "Add items to order"
        } else {
            btnCheckout.text = "Proceed to Checkout (${cart.sumOf { it.quantity }})"
        }
    }

    private fun formatPeso(value: Double): String {
        return "₱" + String.format(Locale.US, "%,.2f", value)
    }
}
