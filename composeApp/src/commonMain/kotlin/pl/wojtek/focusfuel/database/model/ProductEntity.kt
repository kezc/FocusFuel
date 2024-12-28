package pl.wojtek.focusfuel.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val costInPomodoros: Int
)

