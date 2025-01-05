package pl.wojtek.focusfuel.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
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
    }
    val density = LocalDensity.current

    MeasureViewSize(
        viewToMeasure = { PriceText(price) }
    ) { measuredSize ->
        val inlineContent = mapOf(
            Pair(
                myId,
                InlineTextContent(
                    with(density) {
                        Placeholder(
                            width = measuredSize.width.toSp(),
                            height = measuredSize.height.toSp(),
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    }
                ) {
                    PriceText(price)
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
}

@Composable
private fun PriceText(price: Int) {
    Row {
        Icon(
            painter = painterResource(Res.drawable.ic_tomato),
            contentDescription = "Pomodoros",
            modifier = Modifier.padding(end = 2.dp)
        )
        Text(text = "$price")
    }
}
