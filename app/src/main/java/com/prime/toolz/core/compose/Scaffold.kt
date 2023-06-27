package com.prime.toolz.core.compose

import androidx.annotation.FloatRange
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prime.toolz.core.ContentPadding
import com.primex.material3.Text
import kotlin.math.roundToInt

private const val LAYOUT_ID_PROGRESS_BAR = "_layout_id_progress_bar"
private const val LAYOUT_ID_NAV_BAR = "_layout_id_nav_bar"

@Composable
private fun BottomBarItem(
    title: CharSequence,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        selected = checked,
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier,
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        contentColor = if (checked) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = ContentPadding.medium)
                .animateContentSize(),
        ) {
            Icon(imageVector = icon, contentDescription = null)
            if (checked) Text(
                text = title,
                modifier = Modifier.padding(start = ContentPadding.medium),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NavigationRailItem(
    title: CharSequence,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        selected = checked,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(2.dp),
        color = if (checked) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        contentColor = if (checked) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize()
        ) {
            Icon(imageVector = icon, contentDescription = null)
            if (checked) Text(
                text = title,
                modifier = Modifier.padding(start = ContentPadding.medium),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
@NonRestartableComposable
fun Route(
    title: CharSequence,
    icon: ImageVector,
    vertical: Boolean,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onClick: () -> Unit
) {
    when (vertical) {
        true -> BottomBarItem(title = title, icon = icon, modifier, checked, onClick)
        false -> NavigationRailItem(
            checked = checked,
            onClick = onClick,
            icon = icon,
            modifier = modifier,
            title = title
        )
    }
}

@Composable
private inline fun Vertical(
    noinline content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    var orgNavBarHeightPx by remember { mutableFloatStateOf(Float.NaN) }
    var navBarHeight by remember { mutableFloatStateOf(0f) }
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                // if not init return
                if (orgNavBarHeightPx.isNaN()) return Offset.Zero
                val newOffset = navBarHeight - delta.roundToInt()
                // calculate how much height should be reduced or increased.
                navBarHeight = newOffset.coerceIn(0f, orgNavBarHeightPx)
                // return nothing consumed.
                return Offset.Zero
            }
        }
    }

    Layout(
        content = content,
        modifier = modifier
            .nestedScroll(connection)
            .fillMaxSize(),
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        // create duplicate constants to measure the contents as per their wishes.
        val unrestricted = constraints.copy(minHeight = 0)
        val contentPlaceable = measurables[0].measure(
            // The content's length should be equal to height - navBar suggested length.
            constraints.copy(minHeight = 0, maxHeight = height -  (orgNavBarHeightPx - navBarHeight).coerceAtLeast(0f).toInt())
        )
        val channelPlaceable = measurables[1].measure(unrestricted)

        val measurable1 = measurables.getOrNull(2)
        val measurable2 = measurables.getOrNull(3)

        val bottomBarPlaceable =
            (if (measurable1?.layoutId == LAYOUT_ID_NAV_BAR) measurable1 else measurable2)
                ?.measure(unrestricted)
        val progressBarPlaceable =
            (if (measurable1?.layoutId == LAYOUT_ID_PROGRESS_BAR) measurable1 else measurable2)
                ?.measure(unrestricted)

        // update the height etc.
        orgNavBarHeightPx = bottomBarPlaceable?.height?.toFloat() ?: Float.NaN
        // The offset must be equal to orgHeight - navbarHeight.
        val navBarOffsetY = if (orgNavBarHeightPx.isNaN()) 0f else orgNavBarHeightPx - navBarHeight
        // place on the screen
        layout(width, height) {
            var x: Int = 0
            var y: Int = 0
            contentPlaceable.placeRelative(0, 0)
            // Place Channel at the centre bottom of the screen
            // remove nav bar offset from it.
            x = width / 2 - channelPlaceable.width / 2   // centre
            // full height - toaster height - navbar - 16dp padding + navbar offset.
            y = (height - channelPlaceable.height - navBarOffsetY).roundToInt()
            channelPlaceable.placeRelative(x, y)
            // NavBar
            x = width / 2 - (bottomBarPlaceable?.width ?: 0) / 2
            y = (height - navBarOffsetY).roundToInt()
            bottomBarPlaceable?.placeRelative(x, y)
            // the progress bar
            x = width / 2 - (progressBarPlaceable?.width ?: 0) / 2
            y = (height - (progressBarPlaceable?.height ?: 0) - navBarOffsetY).roundToInt()
            progressBarPlaceable?.placeRelative(x, y)
        }
    }
}

@Composable
private inline fun Horizontal(
    noinline content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            // create duplicate constants to measure the contents as per their wishes.
            val unrestricted = constraints.copy(0, minHeight = 0)
            val measurable1 = measurables.getOrNull(2)
            val measurable2 = measurables.getOrNull(3)

            val navRailBarPlaceable =
                (if (measurable1?.layoutId == LAYOUT_ID_NAV_BAR) measurable1 else measurable2)
                    ?.measure(unrestricted)

            val duplicate =
                constraints.copy(0, maxWidth = width - (navRailBarPlaceable?.width ?: 0))
            val contentPlaceable = measurables[0].measure(duplicate)
            val channelPlaceable = measurables[1].measure(duplicate)
            val progressBarPlaceable =
                (if (measurable1?.layoutId == LAYOUT_ID_PROGRESS_BAR) measurable1 else measurable2)
                    ?.measure(duplicate)

            layout(width, height) {
                var x: Int = 0
                var y: Int = 0
                contentPlaceable.placeRelative(x, y)
                // Place toaster at the centre bottom of the screen
                // remove nav bar offset from it.
                x = (navRailBarPlaceable?.width
                    ?: 0) + (contentPlaceable.width / 2) - channelPlaceable.width / 2   // centre
                // full height - toaster height - navbar - 16dp padding + navbar offset.
                y = (height - channelPlaceable.height)
                channelPlaceable.placeRelative(x, y)
                // NavBar place at the start of the screen.
                x = width - (navRailBarPlaceable?.width ?: 0)
                y = 0
                navRailBarPlaceable?.placeRelative(x, y)
                // Place ProgressBar at the bottom of the screen.
                x = contentPlaceable.width / 2 - (progressBarPlaceable?.width ?: 0) / 2
                y = height - (progressBarPlaceable?.height ?: 0)
                progressBarPlaceable?.placeRelative(x, y)
            }
        }
    )
}

/**
 * Scaffold implements the top-level visual layout structure.
 *
 * This component provides an API to assemble multiple components into a screen, ensuring proper
 * layout strategy and coordination between the components.
 *
 * @param vertical Determines the layout structure, allowing either vertical or horizontal
 *                 orientation. When set to true, a vertical layout is used, and a navRail is used
 *                 instead of a navbar in the horizontal layout.
 * @param content The main content of the screen to be displayed. The context is automatically
 *                boxed, so manual boxing is not required.
 * @param modifier Optional [Modifier] to be applied to the composable.
 * @param channel Optional [SnackbarHostState] object to handle displaying [Snack] messages.
 * @param progress Optional progress value to show a linear progress bar. Pass [Float.NaN] to hide
 *                 the progress bar, -1 to show an indeterminate progress bar, or a value between 0 and 1 to show a determinate progress bar.
 * @param tabs Optional [Composable] function to display a navigation bar or toolbar.
 * @param hideNavBar Optional value to force hiding the navigation bar.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Scaffold(
    vertical: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    hideNavigationBar: Boolean = false,
    channel: SnackbarHostState = remember(::SnackbarHostState),
    @FloatRange(0.0, 1.0) progress: Float = Float.NaN,
    tabs: @Composable () -> Unit,
) {
    val composed =
        @Composable {
            // The main content. Autoboxed inside surface.
            Surface(content = content)
            // The SnackBar
            SnackbarHost(hostState = channel, Modifier.imePadding())
            // Don't show the NavigationBar if hideNavigationBar
            // Show BottomBar when vertical else show NavigationRail.
            when {
                // Don't show anything.
                hideNavigationBar -> Unit
                // Hide BottomBar when vertical and imeIsVisible
                vertical && WindowInsets.isImeVisible -> Unit
                // Show BottomAppBar
                // Push content to centre of the screen.
                vertical -> BottomAppBar(modifier = Modifier.layoutId(LAYOUT_ID_NAV_BAR)) {
                    Spacer(modifier = Modifier.weight(1f))
                    tabs()
                    Spacer(modifier = Modifier.weight(1f))
                }

                !vertical -> NavigationRail(
                    modifier = Modifier.layoutId(LAYOUT_ID_NAV_BAR),
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    tabs()
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            // ProgressBar
            // Don't draw when progress is Float.NaN
            when {
                // special value indicating that the progress is about to start.
                progress == -1f -> LinearProgressIndicator(
                    modifier = Modifier.layoutId(
                        LAYOUT_ID_PROGRESS_BAR
                    )
                )
                // draw the progress bar at the bottom of the screen when is not a NAN.
                !progress.isNaN() -> LinearProgressIndicator(
                    progress = progress, modifier = Modifier.layoutId(
                        LAYOUT_ID_PROGRESS_BAR
                    )
                )
            }
        }
    when (vertical) {
        true -> Vertical(content = composed, modifier = modifier)
        else -> Horizontal(content = composed, modifier = modifier)
    }
}


