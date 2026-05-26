package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AnnualTarget
import com.example.data.Product
import com.example.data.Transaction
import com.example.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TransactionRepository(
            database.transactionDao(),
            database.productDao(),
            database.annualTargetDao()
        )
        
        // Setup a default goal on start if none exists
        viewModelScope.launch {
            if (repository.getAnnualTarget() == null) {
                repository.updateAnnualTarget(AnnualTarget(1, 2026, 50000.0, "الهدف المالي السنوي"))
            }
        }
    }

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val annualTarget: StateFlow<AnnualTarget?> = repository.annualTargetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived states for analytics
    val totalIncome: StateFlow<Double> = transactions
        .combine(MutableStateFlow(0.0)) { txs, _ ->
            txs.filter { it.type == "INCOME" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = transactions
        .combine(MutableStateFlow(0.0)) { txs, _ ->
            txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netProfit: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Calculates monthly profits for graph/chart visualization
    val monthlyAnalysis: StateFlow<Map<Int, Double>> = transactions
        .combine(MutableStateFlow(0.0)) { txs, _ ->
            val calendar = Calendar.getInstance()
            val monthlyProfits = mutableMapOf<Int, Double>()
            // Init months (0 to 11 for Jan to Dec) with 0.0
            for (month in 0..11) {
                monthlyProfits[month] = 0.0
            }
            for (tx in txs) {
                calendar.timeInMillis = tx.timestamp
                val month = calendar.get(Calendar.MONTH)
                val currentVal = monthlyProfits[month] ?: 0.0
                val delta = if (tx.type == "INCOME") tx.amount else -tx.amount
                monthlyProfits[month] = currentVal + delta
            }
            monthlyProfits
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Actions
    fun addTransaction(type: String, amount: Double, description: String, category: String, productId: Int? = null, customTimestamp: Long? = null) {
        viewModelScope.launch {
            val ts = customTimestamp ?: System.currentTimeMillis()
            val tx = Transaction(
                type = type,
                amount = amount,
                description = description,
                category = category,
                timestamp = ts,
                productId = productId
            )
            repository.insertTransaction(tx)

            // Business rule: If we linked a sale to an inventory item, update product stock
            if (productId != null && type == "INCOME") {
                val prod = repository.getProductById(productId)
                if (prod != null && prod.stock > 0) {
                    repository.updateProduct(prod.copy(stock = prod.stock - 1))
                }
            }
        }
    }

    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(tx.id)
            // Restore inventory stock if linked product was sold
            if (tx.productId != null && tx.type == "INCOME") {
                val prod = repository.getProductById(tx.productId)
                if (prod != null) {
                    repository.updateProduct(prod.copy(stock = prod.stock + 1))
                }
            }
        }
    }

    fun addProduct(name: String, stock: Int, costPrice: Double, sellPrice: Double, sku: String) {
        viewModelScope.launch {
            val prod = Product(
                name = name,
                stock = stock,
                costPrice = costPrice,
                sellPrice = sellPrice,
                sku = sku
            )
            repository.insertProduct(prod)
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun updateProductStock(product: Product, newStock: Int) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(stock = newStock))
        }
    }

    fun quickSellProduct(product: Product, sellQuantity: Int) {
        viewModelScope.launch {
            if (product.stock >= sellQuantity) {
                // Deduct stock
                repository.updateProduct(product.copy(stock = product.stock - sellQuantity))
                
                // Record sale transaction
                val totalSale = product.sellPrice * sellQuantity
                addTransaction(
                    type = "INCOME",
                    amount = totalSale,
                    description = "مبيعات: ${product.name} (عدد ${sellQuantity})",
                    category = "بيع منتج",
                    productId = product.id
                )
            }
        }
    }

    fun updateAnnualTargetAmount(amount: Double) {
        viewModelScope.launch {
            val current = repository.getAnnualTarget() ?: AnnualTarget()
            repository.updateAnnualTarget(current.copy(targetAmount = amount))
        }
    }
}
