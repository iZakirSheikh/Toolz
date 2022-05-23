package com.prime.toolz2.ui.converter

import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.prime.toolz2.common.compose.ProvideTextStyle
import com.prime.toolz2.common.compose.activity
import com.prime.toolz2.common.compose.rememberState
import com.prime.toolz2.settings.PrefKeys
import com.prime.toolz2.theme.padding
import com.primex.preferences.Preferences
import com.primex.widgets.Material
import com.primex.widgets.Preference
import com.primex.widgets.PrimeDialog


@Composable
fun SettingsDialog(expanded: Boolean, onDismissRequest: () -> Unit) {
    if (expanded)
        PrimeDialog(
            title = "Settings",
            onDismissRequest = onDismissRequest,
            vectorIcon = Icons.Outlined.Settings,
            topBarContentColor = LocalContentColor.current
        ) {
            Column {
                Preference(
                    title = "Font Size",
                    icon = Icons.Outlined.TextFormat,
                    summery = "Adjust font style as per your liking."
                )
                FontScaleSlider(modifier = Modifier.padding(horizontal = Material.padding.Normal))

            }
        }
}

private const val FONT_SCALE_LOWER_BOUND = 0.5f
private const val FONT_SCALE_UPPER_BOUND = 2.0f

private const val SLIDER_STEPS = 10


@Composable
private fun FontScaleSlider(modifier: Modifier = Modifier) {

    val defaultStyle = Material.typography.body2
    val prefs = Preferences.get(LocalContext.current)

    val saved = remember {
        with(prefs) { get(PrefKeys.FONT_SCALE).obtain() }
    }

    var multiplier by rememberState(initial = saved)

    val fontSize = defaultStyle.fontSize

    val activity = LocalContext.current.activity

    val onRequestApply = {
        if (saved != multiplier) {
            prefs[PrefKeys.FONT_SCALE] = multiplier
            activity?.recreate()
        }
    }

    ProvideTextStyle(style = defaultStyle) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "A",
                fontSize = ((fontSize / saved) * FONT_SCALE_LOWER_BOUND),
                fontWeight = FontWeight.Bold
            ) // original size
            Slider(
                value = multiplier,
                onValueChange = { multiplier = it },
                valueRange = 0.5f..2.0f,
                steps = 7,
                modifier = Modifier
                    .padding(horizontal = Material.padding.Medium)
                    .weight(1f)
            )
            Text(
                text = "A",
                fontSize = ((fontSize / saved) * FONT_SCALE_UPPER_BOUND),
                fontWeight = FontWeight.Bold
            ) // final size.
        }
        Row(horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onRequestApply) {
                Text(text = "OK")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Box(modifier = Modifier.fillMaxSize()) {
        FontScaleSlider()
    }
}