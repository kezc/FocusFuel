package pl.wojtek.focusfuel.di

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface MainComponent {
    val pomodoroTimer: PomodoroTimer

    @SingleIn(AppScope::class)
    @Provides
    fun coroutineScope() = MainScope() + CoroutineName("Supervisor Scope")

    @SingleIn(AppScope::class)
    @Provides
    fun provideSettings(): Settings = Settings()
}
