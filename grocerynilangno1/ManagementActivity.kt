package com.example.grocerymanagement

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

data class SaleOrder(
    val orderNumber: String,
    val date: Date,
    val totalItems: Int,
    val total: Double,
    val lines: List<SaleLine>
)

data class SaleLine(
    val itemName: String,
    val quantity: Int
)

data class ItemStock(
    val id: String,
    val name: String,
    val quantity: Int,
    val minStock: Int
)

data class StatCard(
    val title: String,
    val value: String
)

data class RestockSuggestion(
    val itemName: String,
    val message: String
)

data class DayRevenue(
    val label: String,
    val revenue: Double
)

class ManagementActivity : AppCompatActivity() {
    private lateinit var btnRestock: Button
    private lateinit var tvRestockBadge: TextView
    private lateinit var rvStats: RecyclerView
    private lateinit var rvSimpleChart: RecyclerView
    private lateinit var rvSales: RecyclerView

    private val sales = mutableListOf<SaleOrder>()
    private val stockItems = mutableListOf<ItemStock>()

    private lateinit var statsAdapter: StatCardAdapter
    private lateinit var chartAdapter: SimpleChartAdapter
    private lateinit var salesAdapter: SalesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management)

        bindViews()
        seedData()
        setupLists()
        renderUi()

        btnRestock.setOnClickListener { openRestockDialog() }
    }

    private fun bindViews() {
        btnRestock = findViewById(R.id.btnRestock)
        tvRestockBadge = findViewById(R.id.tvRestockBadge)
        rvStats = findViewById(R.id.rvStats)
        rvSimpleChart = findViewById(R.id.rvSimpleChart)
        rvSales = findViewById(R.id.rvSales)
    }

    private fun setupLists() {
        statsAdapter = StatCardAdapter()
        chartAdapter = SimpleChartAdapter()
        salesAdapter = SalesAdapter()

        rvStats.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvStats.adapter = statsAdapter

        rvSimpleChart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSimpleChart.adapter = chartAdapter

        rvSales.layoutManager = LinearLayoutManager(this)
        rvSales.adapter = salesAdapter
    }

    private fun renderUi() {
        val totalRevenue = sales.sumOf { it.total }
        val totalOrders = sales.size
        val averageOrder = if (totalOrders == 0) 0.0 else totalRevenue / totalOrders

        statsAdapter.submitList(
            listOf(
                StatCard("Total Revenue", formatPeso(totalRevenue)),
                StatCard("Total Orders", totalOrders.toString()),
                StatCard("Avg Order Value", formatPeso(averageOrder))
            )
        )
        salesAdapter.submitList(sales.sortedByDescending { it.date }.take(10))
        chartAdapter.submitList(buildLast7DaysRevenue())

        val suggestions = buildRestockSuggestions()
        if (suggestions.isEmpty()) {
            tvRestockBadge.visibility = android.view.View.GONE
            btnRestock.setTextColor(android.graphics.Color.parseColor("#8888A0"))
        } else {
            tvRestockBadge.visibility = android.view.View.VISIBLE
            tvRestockBadge.text = suggestions.size.toString()
            btnRestock.setTextColor(android.graphics.Color.parseColor("#EF4444"))
        }
    }

    private fun buildLast7DaysRevenue(): List<DayRevenue> {
        val formatter = SimpleDateFormat("MMM d", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val days = mutableListOf<Pair<Long, String>>()
        for (i in 6 downTo 0) {
            val c = calendar.clone() as Calendar
            c.add(Calendar.DATE, -i)
            days.add(c.timeInMillis to formatter.format(c.time))
        }

        return days.map { day ->
            val start = day.first
            val end = start + 24L * 60L * 60L * 1000L
            val revenue = sales.filter { it.date.time in start until end }.sumOf { it.total }
            DayRevenue(day.second, revenue)
        }
    }

    private fun buildRestockSuggestions(): List<RestockSuggestion> {
        val cutoff = Date().time - 7L * 24L * 60L * 60L * 1000L
        val demand = mutableMapOf<String, Int>()

        sales.filter { it.date.time >= cutoff }.forEach { order ->
            order.lines.forEach { line ->
                demand[line.itemName] = (demand[line.itemName] ?: 0) + line.quantity
            }
        }

        return stockItems.mapNotNull { stock ->
            val demand7d = demand[stock.name] ?: 0
            val lowStock = stock.quantity <= stock.minStock
            val highDemand = demand7d >= stock.minStock
            if (!lowStock && !highDemand) return@mapNotNull null

            val suggestedQty = max(0, ceil(max((stock.minStock * 2 - stock.quantity).toDouble(), (demand7d - stock.quantity + stock.minStock).toDouble())).toInt())
            if (suggestedQty <= 0) return@mapNotNull null

            val reason = when {
                lowStock && highDemand -> "stock is low and demand is high"
                lowStock -> "stock is low"
                else -> "demand is high"
            }
            RestockSuggestion(
                itemName = stock.name,
                message = "Order $suggestedQty because $reason. Current: ${stock.quantity}, 7d demand: $demand7d"
            )
        }.sortedByDescending { it.message.length }
    }

    private fun openRestockDialog() {
        val suggestions = buildRestockSuggestions()
        val rv = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@ManagementActivity)
            adapter = RestockAdapter().also { it.submitList(suggestions) }
            setPadding(24, 10, 24, 10)
        }

        AlertDialog.Builder(this)
            .setTitle("Restock Bot Suggestions")
            .setView(rv)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun seedData() {
        stockItems.clear()
        stockItems.addAll(
            listOf(
                ItemStock("1", "Coke 1.5L", 11, 10),
                ItemStock("2", "Sprite 1.5L", 7, 8),
                ItemStock("3", "Pancit Canton", 42, 20),
                ItemStock("4", "Century Tuna", 9, 12),
                ItemStock("5", "Bear Brand 300g", 8, 10),
                ItemStock("6", "Piattos 85g", 18, 15)
            )
        )

        val today = Calendar.getInstance()
        sales.clear()
        sales.addAll(
            listOf(
                SaleOrder("ORD-120341", addDays(today, 0), 7, 784.0, listOf(SaleLine("Coke 1.5L", 2), SaleLine("Pancit Canton", 5))),
                SaleOrder("ORD-120340", addDays(today, 0), 3, 135.0, listOf(SaleLine("Century Tuna", 2), SaleLine("Piattos 85g", 1))),
                SaleOrder("ORD-120339", addDays(today, -1), 4, 276.0, listOf(SaleLine("Bear Brand 300g", 1), SaleLine("Pancit Canton", 3))),
                SaleOrder("ORD-120338", addDays(today, -2), 9, 1024.0, listOf(SaleLine("Coke 1.5L", 4), SaleLine("Sprite 1.5L", 3))),
                SaleOrder("ORD-120337", addDays(today, -3), 5, 338.0, listOf(SaleLine("Century Tuna", 3), SaleLine("Piattos 85g", 2))),
                SaleOrder("ORD-120336", addDays(today, -4), 8, 480.0, listOf(SaleLine("Pancit Canton", 8))),
                SaleOrder("ORD-120335", addDays(today, -5), 2, 190.0, listOf(SaleLine("Coke 1.5L", 2))),
                SaleOrder("ORD-120334", addDays(today, -6), 6, 552.0, listOf(SaleLine("Sprite 1.5L", 6)))
            )
        )
    }

    private fun addDays(base: Calendar, delta: Int): Date {
        val c = base.clone() as Calendar
        c.add(Calendar.DATE, delta)
        c.set(Calendar.HOUR_OF_DAY, 10)
        c.set(Calendar.MINUTE, 30)
        return c.time
    }

    private fun formatPeso(value: Double): String {
        return "₱" + String.format(Locale.US, "%,.2f", value)
    }
}
