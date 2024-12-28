package pl.wojtek.focusfuel.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity
data class PomodoroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDateTime
)
