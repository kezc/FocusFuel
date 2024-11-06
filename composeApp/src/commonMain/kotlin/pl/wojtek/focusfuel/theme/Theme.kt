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
            primary = Color(0xFF86CEFF),
            primaryContainer = Color(0xFF004B70),
            secondary = Color(0xFF97CBFF),
            secondaryContainer = Color(0xFF004B70),
            tertiary = Color(0xFFBBC7DB),
            tertiaryContainer = Color(0xFF254B6E)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF006590),
            primaryContainer = Color(0xFFC9E6FF),
            secondary = Color(0xFF006590),
            secondaryContainer = Color(0xFFD1E4FF),
            tertiary = Color(0xFF3B5C7E),
            tertiaryContainer = Color(0xFFD3E4FF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
} 
