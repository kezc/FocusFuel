package pl.wojtek.focusfuel.database.model

import kotlinx.datetime.LocalDateTime

data class PurchaseWithProduct(
    val purchaseId: Int,
    val productId: Long,
    val productName: String,
    val date: LocalDateTime,
    val costInPomodoros: Int
) 
