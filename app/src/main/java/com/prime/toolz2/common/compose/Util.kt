package com.prime.toolz2.common.compose


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.primex.widgets.Material
import cz.levinzonr.saferoute.core.ProvideRouteSpecArgs
import cz.levinzonr.saferoute.core.RouteSpec


@Composable
fun <T> rememberState(initial: T): MutableState<T> = remember {
    mutableStateOf(initial)
}


@Composable
operator fun PaddingValues.plus(value: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(direction) + value.calculateStartPadding(direction),
        top = this.calculateTopPadding() + value.calculateTopPadding(),
        bottom = this.calculateBottomPadding() + value.calculateBottomPadding(),
        end = this.calculateEndPadding(direction) + value.calculateEndPadding(direction)
    )
}

@Composable
fun ProvideTextStyle(
    style: TextStyle,
    alpha: Float = LocalContentAlpha.current,
    color: Color = LocalContentColor.current,
    textSelectionColors: TextSelectionColors = LocalTextSelectionColors.current,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(style)
    CompositionLocalProvider(
        LocalContentColor provides color,
        LocalContentAlpha provides alpha,
        LocalTextSelectionColors provides textSelectionColors,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}

@Composable
fun ProvideTextStyle(
    style: TextStyle = LocalTextStyle.current,
    alpha: Float = LocalContentAlpha.current,
    color: Color = LocalContentColor.current,
    textSelectionColors: TextSelectionColors = LocalTextSelectionColors.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    content: @Composable () -> Unit,
) {
    // NOTE(text-perf-review): It might be worthwhile writing a bespoke merge implementation that
    // will avoid reallocating if all of the options here are the defaults
    val mergedStyle = style.merge(
        TextStyle(
            color = color.copy(alpha),
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing
        )
    )

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}

/**
 * The utility function rotates transforms the composable to clockwise and anti-clockwise.
 */
fun Modifier.rotate(clockwise: Boolean): Modifier {
    val transform = Modifier.layout { measurable, constraints ->
        // as rotation is taking place
        // the height becomes so construct new set of construnts from old one.
        val newConstraints = constraints.copy(
            minWidth = constraints.minHeight,
            minHeight = constraints.minWidth,
            maxHeight = constraints.maxWidth,
            maxWidth = constraints.maxHeight
        )

        // measure measurable with new constraints.
        val placeable = measurable.measure(newConstraints)

        layout(placeable.height, placeable.width) {

            //Compute where to place the measurable.
            // TODO needs to rethink these
            val x = -(placeable.width / 2 - placeable.height / 2)
            val y = -(placeable.height / 2 - placeable.width / 2)

            placeable.place(x = x, y = y)
        }
    }

    val rotated = Modifier.rotate(if (clockwise) 90f else -90f)

    // transform and then apply rotation.
    return this
        .then(transform)
        .then(rotated)
}


/**
 * A utility to get/find host activity
 */
val Context.activity: Activity?
    get() = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.activity
        else -> null
    }


/**
 * Request screen [orientation]
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.activity ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

/**
 * A Utility extension function for managing status bar UI.
 *
 * @param color: The background color of the statusBar. if [Color.Unspecified] the status bar will
 * be painted by primaryVariant.
 * @param darkIcons: same as name suggests works in collaboration with color. if it is unspecified; uses
 * light icons as we will use primaryVariant as background.
 */
fun Modifier.statusBarsPadding2(
    color: Color = Color.Unspecified,
    darkIcons: Boolean = false,
) =
    composed {

        val controller = rememberSystemUiController()
        // invoke but control only icons not color.
        SideEffect {
            controller.setStatusBarColor(
                //INFO we are not going to change the background of the statusBar here.
                // Reasons are.
                //  * It adds a delay and the change becomes ugly.
                //  * animation to color can't be added.
                Color.Transparent,

                // dark icons only when requested by user and color is unSpecified.
                // because we are going to paint status bar with primaryVariant if unspecified.
                darkIcons && !color.isUnspecified
            )
        }


        val paint = color.takeOrElse { Material.colors.primaryVariant }
        // add padding

        // add background
        background(color = paint)
            // the padding
            .statusBarsPadding()
            // then the rest of the composable.
            .then(this@composed)
    }

/**
 * Return a copy of [Color] from [hue], [saturation], and [lightness] (HSL representation).
 *
 * @param hue The color value in the range (0..360), where 0 is red, 120 is green, and
 * 240 is blue; default value is null; which makes is unaltered.
 * @param saturation The amount of [hue] represented in the color in the range (0..1),
 * where 0 has no color and 1 is fully saturated; default value is null; which makes is unaltered.
 * @param lightness A range of (0..1) where 0 is black, 0.5 is fully colored, and 1 is
 * white; default value is null; which makes is unaltered.
 */
fun Color.hsl(
    hue: Float? = null,
    saturation: Float? = null,
    lightness: Float? = null,
    alpha: Float? = null
): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(toArgb(), hsl)

    // use value or default.
    return Color.hsl(
        hue = hue ?: hsl[0],
        saturation = saturation ?: hsl[1],
        lightness = lightness ?: hsl[2],
        alpha = alpha ?: this.alpha,
    )
}


// Nav Host Controller
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("no local nav host controller found")
}

val NavHostController.current
    @Composable
    get() = currentBackStackEntryAsState().value?.destination?.route


///missing fun

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composable(
    spec: RouteSpec<*>,
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?
    )? = enterTransition,
    popExitTransition: (
    AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?
    )? = exitTransition,
    content: @Composable (NavBackStackEntry) -> Unit
) = composable(
    spec.route,
    spec.navArgs,
    spec.deepLinks,
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition
) {
    ProvideRouteSpecArgs(spec = spec, entry = it) {
        content.invoke(it)
    }
}
