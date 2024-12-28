package pl.wojtek.focusfuel.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(foreignKeys = [ForeignKey(entity = ProductEntity::class, parentColumns = ["id"], childColumns = ["productId"])])
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val date: LocalDateTime
)
