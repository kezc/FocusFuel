package pl.wojtek.focusfuel.repository

import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.database.PomodoroDao
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
class PomodorosRepository(
    pomodoroDao: PomodoroDao,
){

}
