package pl.wojtek.focusfuel.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.database.PomodoroDao
import pl.wojtek.focusfuel.database.PomodoroEntity
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface PomodorosRepository {
    fun addPomodoro(date: LocalDateTime = currentLocalDateTime())
    suspend fun getTotalPomodorosFinished(): Int
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PomodorosRepositoryImpl(
    private val pomodoroDao: PomodoroDao,
    private val coroutineScope: CoroutineScope,
) : PomodorosRepository {
    override fun addPomodoro(date: LocalDateTime) {
        coroutineScope.launch {
            pomodoroDao.insert(PomodoroEntity(date = date))
        }
    }

    override suspend fun getTotalPomodorosFinished(): Int {
        return pomodoroDao.count()
    }

}

fun currentLocalDateTime() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
