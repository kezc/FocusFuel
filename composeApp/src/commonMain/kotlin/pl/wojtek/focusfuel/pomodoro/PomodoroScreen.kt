package pl.wojtek.focusfuel.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object PomodoroScreen : Screen

@CircuitInject(PomodoroScreen::class, AppScope::class)
class PomodoroUI : Ui<PomodoroState> {
    @Composable
    override fun Content(state: PomodoroState, modifier: Modifier) {
        PomodoroUI(modifier, state)
    }
}

@Composable
private fun PomodoroUI(modifier: Modifier = Modifier, state: PomodoroState) {
    val backgroundColor = when (state.currentPhase) {
        PomodoroPhase.WORK -> MaterialTheme.colorScheme.primaryContainer
        PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondaryContainer
        PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PhaseIndicator(state.currentPhase)

            TimerDisplay(state.timerDisplay)

            ControlButtons(state)
        }
    }
}

@Composable
private fun PhaseIndicator(phase: PomodoroPhase) {
    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = when (phase) {
                PomodoroPhase.WORK -> "Focus Time"
                PomodoroPhase.SHORT_BREAK -> "Short Break"
                PomodoroPhase.LONG_BREAK -> "Long Break"
            },
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TimerDisplay(time: String) {
    Card(
        modifier = Modifier
            .size(250.dp)
            .clip(CircleShape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ControlButtons(state: PomodoroState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        ResetButton(state)

        PlayOrPauseButton(state)

        SkipButton(state)
    }
}

@Composable
fun PlayOrPauseButton(state: PomodoroState) {
    FilledIconButton(
        onClick = { state.eventSink(PomodoroEvent.ToggleTimer) },
        modifier = Modifier.size(72.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = if (state.isRunning)
                Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = if (state.isRunning) "Pause" else "Start",
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun SkipButton(
    state: PomodoroState,
) {
    FilledTonalIconButton(
        onClick = { state.eventSink(PomodoroEvent.Skip) },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.SkipNext,
            contentDescription = "Skip"
        )
    }
}

@Composable
private fun ResetButton(state: PomodoroState) {
    FilledTonalIconButton(
        onClick = { state.eventSink(PomodoroEvent.Reset) },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = "Reset"
        )
    }
}

@Composable
@Preview
fun PreviewPomodoroWork() {
    PomodoroUI(
        state = PomodoroState(
            currentPhase = PomodoroPhase.WORK,
            timeRemainingSeconds = 1500,
            isRunning = true,
            timerDisplay = "25:00",
            eventSink = {}
        )
    )
}

@Composable
@Preview
fun PreviewPomodoroShortBreak() {
    PomodoroUI(
        state = PomodoroState(
            currentPhase = PomodoroPhase.SHORT_BREAK,
            timeRemainingSeconds = 300,
            isRunning = false,
            timerDisplay = "05:00",
            eventSink = {}
        )
    )
}

@Composable
@Preview
fun PreviewPomodoroLongBreak() {
    PomodoroUI(
        state = PomodoroState(
            currentPhase = PomodoroPhase.LONG_BREAK,
            timeRemainingSeconds = 900,
            isRunning = false,
            timerDisplay = "15:00",
            eventSink = {}
        )
    )
}
