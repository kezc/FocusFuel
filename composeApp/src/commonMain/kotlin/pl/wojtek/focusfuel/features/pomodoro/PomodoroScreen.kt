package pl.wojtek.focusfuel.features.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.ic_tomato
import focusfuel.composeapp.generated.resources.pomodoro_focus_time
import focusfuel.composeapp.generated.resources.pomodoro_long_break
import focusfuel.composeapp.generated.resources.pomodoro_reset_button_description
import focusfuel.composeapp.generated.resources.pomodoro_short_break
import focusfuel.composeapp.generated.resources.pomodoro_skip_button_description
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.ui.component.AppIconButton
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import pl.wojtek.focusfuel.util.platform.Platform
import pl.wojtek.focusfuel.util.platform.platform
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PomodoroUI(modifier: Modifier = Modifier, state: PomodoroState) {
    val backgroundColor = animateColorAsState(
        when (state.currentPhase) {
            PomodoroPhase.WORK -> MaterialTheme.colorScheme.primaryContainer
            PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondaryContainer
            PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiaryContainer
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor.value,
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor.value),
                actions = { MuteIcon(state) }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PhaseIndicator(state.currentPhase)
                Spacer(Modifier.height(16.dp))
                TimerDisplay(state.timerDisplay)
                Spacer(Modifier.height(16.dp))
                ControlButtons(state)
            }
        }
    )
}

@Composable
private fun MuteIcon(
    state: PomodoroState,
) {
    if (platform == Platform.DESKTOP) {
        val soundIcon = if (state.isSoundOn)
            Icons.AutoMirrored.Outlined.VolumeUp
        else
            Icons.AutoMirrored.Outlined.VolumeOff
        AppIconButton(
            onClick = { state.eventSink(PomodoroEvent.ToggleSound) },
            imageVector = soundIcon,
            contentDescription = null
        )
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
                PomodoroPhase.WORK -> stringResource(Res.string.pomodoro_focus_time)
                PomodoroPhase.SHORT_BREAK -> stringResource(Res.string.pomodoro_short_break)
                PomodoroPhase.LONG_BREAK -> stringResource(Res.string.pomodoro_long_break)
            },
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TimerDisplay(time: String) {
    Box {
        Card(
            modifier = Modifier
                .size(250.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp)
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
        Image(
            imageVector = vectorResource(Res.drawable.ic_tomato),
            contentDescription = null,
            modifier = Modifier.size(75.dp).rotate(30f).align(Alignment.TopEnd)
        )
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
            contentDescription = stringResource(Res.string.pomodoro_skip_button_description)
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
            contentDescription = stringResource(Res.string.pomodoro_reset_button_description)
        )
    }
}

@Composable
@Preview
fun PreviewPomodoroWork() {
    PomodoroUI(
        state = PomodoroState(
            currentPhase = PomodoroPhase.WORK,
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
            isRunning = false,
            timerDisplay = "15:00",
            eventSink = {}
        )
    )
}
