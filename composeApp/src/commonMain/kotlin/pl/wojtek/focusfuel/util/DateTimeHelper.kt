package pl.wojtek.focusfuel.util

import kotlinx.datetime.LocalDateTime

class DateTimeHelper {

    fun getFormattedDate(date: LocalDateTime): String {
        val day = date.dayOfMonth
        val month = date.monthNumber
        val year = date.year

        return "${day.withPaddedZeros(2)}.${month.withPaddedZeros(2)}.${year}"
    }

    private fun Int.withPaddedZeros(maxLength: Int): String {
        if (this < 0 || maxLength < 1) return ""

        val string = this.toString()
        return string.padStart(maxLength, '0')
    }
}
