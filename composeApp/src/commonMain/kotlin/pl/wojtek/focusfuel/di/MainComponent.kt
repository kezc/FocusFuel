package pl.wojtek.focusfuel.di

import com.russhwolf.settings.Settings
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.features.pomodoro.PomodoroNotificationsManager
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface MainComponent {
    val pomodoroTimer: PomodoroTimer
    val pomodoroNotificationsManager: PomodoroNotificationsManager
    val presenterFactories: Set<Presenter.Factory>
    val uiFactories: Set<Ui.Factory>
    val circuit: Circuit

    @SingleIn(AppScope::class)
    @Provides
    fun coroutineScope() = MainScope() + CoroutineName("Supervisor Scope")

    @SingleIn(AppScope::class)
    @Provides
    fun provideSettings(): Settings = Settings()

    @SingleIn(AppScope::class)
    @Provides
    fun circuit(presenterFactories: Set<Presenter.Factory>, uiFactories: Set<Ui.Factory>): Circuit {
        return Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()
    }
}
