package com.prime.toolz.core.compose

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

private val PLACE_HOLDER_ICON_BOX_SIZE = 192.dp
private val PLACE_HOLDER_ICON_BOX_DEFAULT_SIZE = 56.dp

// FIXME: Update Placeholder to handle orientation changes and fill the available height instead of
//  occupying the entire screen.

/**
 * Composes a vertical [Placeholder] layout.
 */
@Composable
fun VPlaceholder(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    message: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit),
) {
    Column(
        modifier = Modifier

            // optional full max size
            .fillMaxSize()

            // add a padding normal
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .then(modifier),

        // The Placeholder will be Placed in the middle of the available space
        // both h/v
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // place icon if available
        if (icon != null) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    //  .align(Alignment.CenterHorizontally)
                    .size(PLACE_HOLDER_ICON_BOX_SIZE),
                propagateMinConstraints = true
            ) {
                icon()
            }
        }

        // Place Title
        ProvideTextStyle(
            value = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            content = title,
        )

        //Message
        if (message != null) {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            ProvideTextStyle(
                value = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                content = message,
            )
        }

        //Action
        if (action != null) {
            Spacer(modifier = Modifier.padding(vertical = 32.dp))
            action()
        }
    }
}

/**
 * Composes a vertical [Placeholder] layout.
 */
@Composable
fun HPlaceholder(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    message: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit),
) {
    Row(
        modifier = Modifier
            // optional full max size
            .fillMaxSize()
            // add a padding normal
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.weight(0.15f))

        Column(
            modifier = Modifier
                .padding(end = 32.dp)
                .weight(0.7f, fill = false)
        ) {

            // Place Title
            ProvideTextStyle(
                value = MaterialTheme.typography.headlineMedium,
                content = title
            )

            //Message
            if (message != null) {
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodyMedium,
                    content = message
                )
            }

            //Action
            if (action != null) {
                Spacer(modifier = Modifier.padding(top = 32.dp))
                action()
            }
        }

        // place icon if available
        if (icon != null) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    //  .align(Alignment.CenterHorizontally)
                    .size(PLACE_HOLDER_ICON_BOX_SIZE),
                propagateMinConstraints = true
            ) {
                icon()
            }
        }

        Spacer(modifier = Modifier.weight(0.15f))
    }
}

@Composable
@NonRestartableComposable
fun Placeholder(
    vertical: Boolean,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    message: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit),
) {
    when (vertical) {
        true -> VPlaceholder(modifier, icon, message, action, title)
        else -> HPlaceholder(modifier, icon, message, action, title)
    }
}

/**
 * Composes placeholder with lottie icon.
 */
@Composable
inline fun Placeholder(
    title: String,
    @RawRes iconResId: Int,
    modifier: Modifier = Modifier,
    vertical: Boolean = true,
    message: String? = null,
    noinline action: @Composable (() -> Unit)? = null
) {
    Placeholder(
        modifier = modifier,
        vertical = vertical,
        message = { if (message != null) Text(text = message) },
        title = { Text(text = title.ifEmpty { " " }, maxLines = 2) },

        icon = {
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    iconResId
                )
            )
            LottieAnimation(
                composition = composition, iterations = Int.MAX_VALUE
            )
        },
        action = action,
    )
}

