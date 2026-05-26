package com.example.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val annualTargetDao: AnnualTargetDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val annualTargetFlow: Flow<AnnualTarget?> = annualTargetDao.getAnnualTargetFlow()

    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Int) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun insertProduct(product: Product): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(id: Int) {
        productDao.deleteProductById(id)
    }

    suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)
    }

    suspend fun getAnnualTarget(): AnnualTarget? {
        return annualTargetDao.getAnnualTarget()
    }

    suspend fun updateAnnualTarget(annualTarget: AnnualTarget) {
        annualTargetDao.insertAnnualTarget(annualTarget)
    }
}
