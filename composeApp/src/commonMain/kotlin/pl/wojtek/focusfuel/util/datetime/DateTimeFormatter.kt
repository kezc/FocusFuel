package pl.wojtek.focusfuel.util.datetime

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import me.tatarka.inject.annotations.Inject

@Inject
class DateTimeFormatter {

    fun getFormattedDateTime(date: LocalDateTime): String {
        val format = LocalDateTime.Format {
            hour()
            char(':')
            minute()

            char(' ')

            dayOfMonth()
            char('.')
            monthNumber()
            char('.')
            year()
        }
        return format.format(date)
    }
}
