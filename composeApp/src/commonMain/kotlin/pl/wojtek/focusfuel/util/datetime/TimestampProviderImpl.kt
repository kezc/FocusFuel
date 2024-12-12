package pl.wojtek.focusfuel.util.datetime

import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding


@Inject
@ContributesBinding(AppScope::class)
class TimestampProviderImpl : TimestampProvider {
    override fun getTimestamp() = Clock.System.now().toEpochMilliseconds()
}

interface TimestampProvider {
    fun getTimestamp(): Long
}
