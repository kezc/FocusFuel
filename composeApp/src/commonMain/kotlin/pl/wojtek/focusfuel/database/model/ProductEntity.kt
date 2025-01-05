package pl.wojtek.focusfuel.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val costInPomodoros: Int,
    val hidden: Boolean = false,
    val originalId: Long? = null
)

