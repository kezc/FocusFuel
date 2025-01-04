package pl.wojtek.focusfuel.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.database.dao.PomodoroDao
import pl.wojtek.focusfuel.database.model.PomodoroEntity
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface PomodorosRepository {
    fun addPomodoro(date: LocalDateTime = currentLocalDateTime())
    fun getTotalPomodorosFinished(): Flow<Int>
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

    override fun getTotalPomodorosFinished(): Flow<Int> =pomodoroDao.count()
}

fun currentLocalDateTime() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
