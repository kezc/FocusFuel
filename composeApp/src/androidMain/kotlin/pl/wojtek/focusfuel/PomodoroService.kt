package pl.wojtek.focusfuel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.features.pomodoro.PomodoroPhase
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimerState
import pl.wojtek.focusfuel.util.datetime.PomodoroTimeFormat
import kotlin.time.Duration.Companion.seconds

class PomodoroService : Service() {
    companion object {
        private const val TAG = "PomodoroService"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_RESUME = "RESUME"
        private const val ACTION_PAUSE = "PAUSE"
        fun getIntent(context: Context): Intent {
            return Intent(context, PomodoroService::class.java)
        }
    }

    private lateinit var pomodoroTimer: PomodoroTimer
    private val notificationChannelId = "PomodoroServiceChannel"
    private val scope = MainScope()

    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG) { "onCreate" }

        pomodoroTimer = (applicationContext as FocusFuelApp).appComponent.pomodoroTimer

        pomodoroTimer.state
            .map { it.isRunning }
            .debounce(10.seconds.inWholeMilliseconds)
            .filter { it.not() }
            .onEach { stopSelf() }
            .launchIn(scope)

        pomodoroTimer.state
            .onEach { updateNotification(it) }
            .launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d(TAG) { "onStartCommand intent:${intent}, flags:$flags sid:$startId" }
        createNotificationChannel()
        startForegroundService()
        when (intent?.action) {
            ACTION_PAUSE -> {
                pomodoroTimer.toggleTimer()
            }

            ACTION_RESUME -> {
                pomodoroTimer.toggleTimer()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Logger.d(TAG) { "onDestroy" }
        scope.cancel()
        super.onDestroy()
    }

    private fun startForegroundService() {
        updateNotification(pomodoroTimer.state.value)
    }

    private fun updateNotification(state: PomodoroTimerState) {
        val notification = createNotification(state)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(state: PomodoroTimerState): Notification {
        val buttonAction =
            if (state.isRunning) createPendingIntent(ACTION_PAUSE)
            else createPendingIntent(ACTION_RESUME)
        val buttonIcon = if (state.isRunning) R.drawable.ic_pause else R.drawable.ic_play
        val buttonText = if (state.isRunning) getString(R.string.pause) else getString(R.string.resume)

        val phaseText = when (state.currentPhase) {
            PomodoroPhase.WORK -> getString(R.string.pomodoro_focus_time)
            PomodoroPhase.LONG_BREAK -> getString(R.string.pomodoro_short_break)
            PomodoroPhase.SHORT_BREAK -> getString(R.string.pomodoro_long_break)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(phaseText)
            .setContentText(getString(R.string.time_remaining, PomodoroTimeFormat.formatPomodoroTime(state.timeRemainingMs)))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(buttonIcon, buttonText, buttonAction)
            .build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PomodoroService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                getString(R.string.timer_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

@Inject
class PomodoroServiceManager(
    private val context: Context,
    private val pomodoroTimer: PomodoroTimer,
    private val scope: CoroutineScope,
) {
    fun init() {
        pomodoroTimer.state
            .map { it.isRunning }
            .distinctUntilChanged()
            .filter { it }
            .onEach { context.startService(PomodoroService.getIntent(context)) }
            .launchIn(scope)
    }
}
