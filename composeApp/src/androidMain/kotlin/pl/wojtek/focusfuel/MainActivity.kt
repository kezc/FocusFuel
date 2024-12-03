package pl.wojtek.focusfuel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.russhwolf.settings.Settings
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val circuit = AppComponent::class.create().circuit

        enableEdgeToEdge()

        setContent {
            App(circuit)
        }
    }
}

@Component
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent : AppComponentMerged {
    abstract val presenterFactories: Set<Presenter.Factory>
    abstract val uiFactories: Set<Ui.Factory>

    @SingleIn(AppScope::class)
    @Provides
    fun circuit(presenterFactories: Set<Presenter.Factory>, uiFactories: Set<Ui.Factory>): Circuit {
        return Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideSettings(): Settings = Settings()

    abstract val circuit: Circuit
}
