package com.prime.toolz2.common.compose

import android.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prime.toolz2.padding
import com.primex.widgets.Material
import com.primex.widgets.Preference
import com.primex.widgets.acquireFocusOnInteraction


@Composable
fun Preference(
    modifier: Modifier = Modifier,
    singleLineTitle: Boolean = true,
    iconSpaceReserved: Boolean = true,
    icon: ImageVector? = null,
    summery: String? = null,
    title: String,
    enabled: Boolean = true,
    lockExpanded: Boolean = false,
    innerPref: Modifier? = null,
    widget: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val source by remember {
        lazy { MutableInteractionSource() }
    }

    val expanded by when {
        lockExpanded -> remember { mutableStateOf(true) }
        else -> source.collectIsFocusedAsState()
    }

    Column(
        modifier = when {
            lockExpanded -> modifier
            else -> Modifier
                .acquireFocusOnInteraction(source, indication = LocalIndication.current)
                .wrapContentSize()
                .then(modifier)
                .animateContentSize()
        }
    ){
        // Main Preference
        Preference(
            title = title,
            summery = summery,
            icon = icon,
            enabled = enabled,
            singleLineTitle = singleLineTitle,
            iconSpaceReserved = iconSpaceReserved,
            modifier = innerPref ?: Modifier,
            widget = widget
        )


        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column() {
                content()
            }
        }
    }
}

@Composable
fun FontScalePreference(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLineTitle: Boolean = true,
    iconSpaceReserved: Boolean = true,
    icon: ImageVector? = null,
    summery: String? = null,
    title: String,
    lockExpanded: Boolean = false,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    defaultValue: Float,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
) {
    Preference(
        modifier = modifier,
        title = title,
        enabled = enabled,
        singleLineTitle = singleLineTitle,
        iconSpaceReserved = iconSpaceReserved,
        icon = icon,
        lockExpanded = lockExpanded,
        summery = summery,
        widget = null
    ) {
        var value by rememberState(initial = defaultValue)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = 16.dp + if (iconSpaceReserved) 24.dp + 16.dp else 0.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                    // top = 16.dp
                )
                .fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Outlined.TextFormat,
                contentDescription = null,
            )

            Slider(
                value = value,
                onValueChange = {
                    value = it
                },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier
                    .padding(horizontal = Material.padding.Medium)
                    .weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.TextFormat,
                contentDescription = null,
                modifier = Modifier.scale(1.5f)
            )
        }

        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp + if (iconSpaceReserved) 24.dp + 16.dp else 0.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                    // top = 16.dp
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val manager = LocalFocusManager.current
            TextButton(onClick = {
                if (!lockExpanded)
                    manager.clearFocus(true)
            }) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(onClick = {
                if (!lockExpanded)
                    manager.clearFocus(true)
                onValueChange(value)
            }) {
                Text(
                    text = stringResource(id = R.string.ok),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}