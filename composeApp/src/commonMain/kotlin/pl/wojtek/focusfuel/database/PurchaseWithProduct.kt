package pl.wojtek.focusfuel.database

import kotlinx.datetime.LocalDateTime

data class PurchaseWithProduct(
    val purchaseId: Int,
    val productId: String,
    val productName: String,
    val date: LocalDateTime,
    val costInPomodoros: Int
) 
