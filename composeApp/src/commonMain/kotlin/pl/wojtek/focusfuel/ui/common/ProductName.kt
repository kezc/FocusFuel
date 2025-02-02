package pl.wojtek.focusfuel.ui.common

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.em
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.ic_tomato
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductName(
    price: Int,
    productName: String,
    modifier: Modifier = Modifier
) {
    val myId = "inlineContent"
    val text = buildAnnotatedString {
        append(AnnotatedString("$productName "))
        appendInlineContent(myId, "[myBox]")
            append(AnnotatedString("\u00A0$price"))
    }

    val inlineContent = mapOf(
        Pair(
            myId,
            InlineTextContent(
                Placeholder(
                    width = 1.em,
                    height = 1.em,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_tomato),
                    contentDescription = null,
                )
            }
        )
    )

    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        inlineContent = inlineContent,
    )
}
