package pl.wojtek.focusfuel.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DateConverter {
    @TypeConverter
    fun fromLongToDate(timeInMillis: Long): LocalDateTime =
        Instant.fromEpochMilliseconds(timeInMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())

    @TypeConverter
    fun fromDateToLong(localDateTime: LocalDateTime): Long =
        localDateTime.toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
}
