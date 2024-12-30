import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFD32F2F),
            primaryContainer = Color(0xFFB71C1C),
            secondary = Color(0xFFEF5350),
            secondaryContainer = Color(0xFFEF9A9A),
            tertiary = Color(0xFFFFCDD2),
            tertiaryContainer = Color(0xFFFFEBEE)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFD32F2F),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEF5350),
            onPrimaryContainer = Color.White,
            inversePrimary = Color(0xFFFFC107),
            secondary = Color(0xFFB71C1C),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFEF9A9A),
            onSecondaryContainer = Color.Black,
            tertiary = Color(0xFFFFCDD2),
            onTertiary = Color.Black,
            tertiaryContainer = Color(0xFFFFEBEE),
            onTertiaryContainer = Color.Black,
            background = Color(0xFFFFE0E0),
            onBackground = Color.Black,
            surface = Color(0xFFFFC0C0),
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFFFC1C1),
            onSurfaceVariant = Color.Black,
            error = Color(0xFFD32F2F),
            onError = Color.White,
            outline = Color(0xFFB71C1C),
            surfaceContainer = Color(0xFFFFB2B2),
            surfaceContainerHigh = Color(0xFFFF9999),
            surfaceContainerHighest = Color(0xFFFF7F7F),
            surfaceContainerLow = Color(0xFFFFC1C1),
            surfaceContainerLowest = Color(0xFFFFE0E0),
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
} 
