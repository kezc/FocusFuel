package pl.wojtek.focusfuel.di

import com.russhwolf.settings.Settings
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface MainComponent {

    @SingleIn(AppScope::class)
    @Provides
    fun provideSettings(): Settings = Settings()
}
