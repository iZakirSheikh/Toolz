package com.prime.toolz2

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.prime.toolz2.common.compose.hsl
import com.prime.toolz2.settings.GlobalKeys
import com.prime.toolz2.settings.FontFamily
import com.primex.preferences.Preferences
import com.primex.widgets.*
import kotlinx.coroutines.flow.map
import androidx.compose.ui.text.font.FontFamily as AndroidFontFamily

private const val TAG = "Theme"


/**
 * An Extra font family.
 */
val ProvidedFontFamily = AndroidFontFamily(
    //light
    Font(R.font.lato_light, FontWeight.Light),
    //normal
    Font(R.font.lato_regular, FontWeight.Normal),
    //medium
    Font(R.font.lato_bold, FontWeight.Medium),
)

interface Padding {

    val Small: Dp

    val Medium: Dp

    val Normal: Dp

    val Large: Dp
}

private val padding =
    object : Padding {
        override val Small: Dp = 4.dp

        override val Medium: Dp = 8.dp
        override val Normal: Dp = 16.dp
        override val Large: Dp = 32.dp
    }

val Material.padding: Padding get() = com.prime.toolz2.padding

/**
 * Constructs the typography with the [fontFamily] provided with support for capitalizing.
 */
private fun Typography(fontFamily: AndroidFontFamily): Typography {
    return Typography(
        defaultFontFamily = fontFamily,
        button = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 1.25.sp,
            // a workaround for capitalizing
            fontFeatureSettings = "c2sc, smcp"
        ),
        overline = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            // a workaround for capitalizing
            fontFeatureSettings = "c2sc, smcp"
        )
    )
}

/**
 * A variant of caption.
 */
private val caption2 = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    letterSpacing = 0.4.sp
)

/**
 * A variant of caption
 */
val Typography.caption2 get() = com.prime.toolz2.caption2


val LocalSystemUiController = staticCompositionLocalOf<SystemUiController> {
    error("No ui controller defined!!")
}

/**
 * The alpha of the container colors.
 */
val Material.CONTAINER_COLOR_ALPHA get() = 0.15f

/**
 * The default [Color] change Spec
 */
private val DefaultColorAnimSpec = tween<Color>(Anim.durationLong)

/**
 * checks If [GlobalKeys.FORCE_COLORIZE]
 */
val Material.forceColorize
    @Composable inline get() = Preferences.get(LocalContext.current).run {
        get(GlobalKeys.FORCE_COLORIZE).observeAsState().value
    }

private val small2 = RoundedCornerShape(8.dp)

/**
 * A variant of Material shape with coroner's 8 dp
 */
val Shapes.small2 get() = com.prime.toolz2.small2

/**
 * returns [primary] if [requires] is met else [elze].
 * @param requires The condition for primary to return. default value is [requiresAccent]
 * @param elze The color to return if [requires] is  not met The default value is [surface]
 */
@Composable
fun Colors.primary(requires: Boolean = Material.forceColorize, elze: Color = colors.surface) =
    if (requires) Material.colors.primary else elze

/**
 * returns [onPrimary] if [requires] is met else [otherwise].
 * @param requires The condition for onPrimary to return. default value is [requiresAccent]
 * @param otherwise The color to return if [requires] is  not met The default value is [onSurface]
 */
@Composable
fun Colors.onPrimary(requires: Boolean = Material.forceColorize, elze: Color = colors.onSurface) =
    if (requires) Material.colors.onPrimary else elze

/**
 * @see primary()
 */
@Composable
fun Colors.secondary(requires: Boolean = Material.forceColorize, elze: Color = colors.surface) =
    if (requires) Material.colors.secondary else elze

/**
 * @see onPrimary()
 */
@Composable
fun Colors.onSecondary(requires: Boolean = Material.forceColorize, elze: Color = colors.onSurface) =
    if (requires) Material.colors.onSecondary else elze

val Colors.surfaceVariant
    @Composable inline get() = colors.surface.hsl(lightness = if (isLight) 0.94f else 0.01f)

/**
 * Primary container is applied to elements needing less emphasis than primary
 */
val Colors.primaryContainer
    @Composable inline get() = colors.primary.copy(Material.CONTAINER_COLOR_ALPHA)

/**
 * On-primary container is applied to content (icons, text, etc.) that sits on top of primary container
 */
val Colors.onPrimaryContainer @Composable inline get() = colors.primary

val Colors.secondaryContainer
    @Composable inline get() = colors.secondary.copy(Material.CONTAINER_COLOR_ALPHA)

val Colors.onSecondaryContainer @Composable inline get() = colors.secondary

val Colors.errorContainer
    @Composable inline get() = colors.error.copy(Material.CONTAINER_COLOR_ALPHA)

val Colors.onErrorContainer @Composable inline get() = colors.error

/**
 * Observes the coloring [GlobalKeys.COLOR_STATUS_BAR] of status Bar.
 */
val Material.colorStatusBar
    @Composable inline get() = Preferences.get(LocalContext.current).run {
        get(GlobalKeys.COLOR_STATUS_BAR).observeAsState().value
    }

inline val Colors.overlay
    @Composable get() = (if (isLight) Color.Black else Color.White).copy(0.04f)

val Colors.onOverlay @Composable inline get() = (Material.colors.onBackground).copy(alpha = ContentAlpha.medium)

//TODO: find appropriate method to represent this.
@Composable
@ExperimentalComposeApi
private fun animate(
    palette: Colors,
    spec: AnimationSpec<Color> = tween(Anim.durationLong)
): Colors {
    return Colors(
        isLight = palette.isLight,
        primary = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.primary
        ).value,
        primaryVariant = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.primaryVariant
        ).value,
        secondary = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.secondary
        ).value,
        secondaryVariant = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.secondaryVariant
        ).value,
        background = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.background
        ).value,
        surface = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.surface
        ).value,
        error = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.error
        ).value,
        onPrimary = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.onPrimary
        ).value,
        onSecondary = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.onSecondary
        ).value,
        onBackground = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.onBackground
        ).value,
        onSurface = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.onSurface
        ).value,
        onError = animateColorAsState(
            animationSpec = spec,
            targetValue = palette.onError
        ).value,
    )
}

@OptIn(ExperimentalComposeApi::class)
@Composable
fun Material(isDark: Boolean, content: @Composable() () -> Unit) {
    val context = LocalContext.current

    //TODO: Find appropriate way to inject this
    val preferences = Preferences.get(context = context)

    val colors = with(preferences) {
        val palette =
            get(if (isDark) GlobalKeys.DARK_COLORS else GlobalKeys.LIGHT_COLORS).observeAsState().value
        animate(palette = palette, spec = tween(Anim.durationLong))
    }

    val fontFamily by with(preferences) {
        preferences[GlobalKeys.FONT_FAMILY].map { font ->
            when (font) {
                FontFamily.SYSTEM_DEFAULT -> AndroidFontFamily.Default
                FontFamily.PROVIDED -> ProvidedFontFamily
                FontFamily.SAN_SERIF -> AndroidFontFamily.SansSerif
                FontFamily.SARIF -> AndroidFontFamily.Serif
                FontFamily.CURSIVE -> AndroidFontFamily.Cursive
            }
        }.observeAsState()
    }
    Log.i(TAG, "Material: $colors")

    val systemUiController = rememberSystemUiController()
    CompositionLocalProvider(LocalSystemUiController provides systemUiController) {
        MaterialTheme(
            typography = Typography(fontFamily = fontFamily),
            content = content,
            colors = colors
        )
        val hideStatusBar by with(preferences) { preferences[GlobalKeys.HIDE_STATUS_BAR].observeAsState() }
        systemUiController.isStatusBarVisible = !hideStatusBar
    }
}

