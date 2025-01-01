package pl.wojtek.focusfuel.util.datetime

import kotlin.math.roundToInt

object PomodoroTimeFormat {
    fun formatPomodoroTime(timeRemainingMs: Long): String {
        val seconds = (timeRemainingMs / 1000f).roundToInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
    }
}
