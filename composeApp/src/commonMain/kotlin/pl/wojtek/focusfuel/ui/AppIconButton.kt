package pl.wojtek.focusfuel.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.common_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun AppCloseIcon(onClick: () -> Unit) {
    AppIconButton(
        onClick = onClick,
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(Res.string.common_back)
    )
}
