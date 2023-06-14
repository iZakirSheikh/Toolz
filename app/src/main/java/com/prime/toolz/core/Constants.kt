package com.prime.toolz.core

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Alias for [AnimationConstants] to provide shorter name for animation related constants.
 */
typealias Anim = AnimationConstants

/**
 * Long duration time in milliseconds, used for animations.
 */
private const val LONG_DURATION_TIME = 500

/**
 * The [Anim.LongDurationMills] is a constant value that returns the [LONG_DURATION_TIME].
 */
val Anim.LongDurationMills get() = LONG_DURATION_TIME

/**
 * Medium duration time in milliseconds, used for animations.
 */
private const val MEDIUM_DURATION_TIME = 400

/**
 * The [Anim.MediumDurationMills] is a constant value that returns the [MEDIUM_DURATION_TIME].
 */
val Anim.MediumDurationMills get() = MEDIUM_DURATION_TIME

/**
 * Short duration time in milliseconds, used for animations.
 */
private const val SHORT_DURATION_TIME = 200

/**
 * The [Anim.ShortDurationMills] is a constant value that returns the [SHORT_DURATION_TIME].
 */
val Anim.ShortDurationMills get() = SHORT_DURATION_TIME

/**
 * Short duration time in milliseconds, used for activity transitions.
 */
private const val ACTIVITY_SHORT_DURATION = 150

/**
 * The [Anim.ActivityShortDurationMills] is a constant value that returns the [ACTIVITY_SHORT_DURATION].
 */
val Anim.ActivityShortDurationMills get() = ACTIVITY_SHORT_DURATION

/**
 * Long duration time in milliseconds, used for activity transitions.
 */
private const val ACTIVITY_LONG_DURATION = 220

/**
 * The [Anim.ActivityLongDurationMills] is a constant value that returns the [ACTIVITY_LONG_DURATION].
 */
val Anim.ActivityLongDurationMills get() = ACTIVITY_LONG_DURATION

/**
 * Padding values used in [androidx.compose.foundation.layout.PaddingValues] and [androidx.compose.foundation.layout.PaddingModifier].
 */
object ContentPadding {
    /**
     * A small padding value of 4dp.
     */
    val small: Dp = 4.dp

    /**
     * A medium padding value of 8dp.
     */
    val medium: Dp = 8.dp

    /**
     * A normal padding value of 16dp.
     */
    val normal: Dp = 16.dp

    /**
     * A large padding value of 22dp.
     */
    val large: Dp = 22.dp

    /**
     * A large padding value of 32dp.
     */
    val xLarge: Dp = 32.dp

}

/**
 * Elevation values used in [androidx.compose.foundation.layout.Box] and [androidx.compose.material.MaterialTheme].
 */
object ContentElevation {
    /**
     * Zero elevation.
     */
    val none = 0.dp

    /**
     * A low elevation value of 6dp.
     */
    val low = 6.dp

    /**
     * A medium elevation value of 12dp.
     */
    val medium = 12.dp

    /**
     * A high elevation value of 20dp.
     */
    val high = 20.dp

    /**
     * An extra high elevation value of 30dp.
     */
    val xHigh = 30.dp
}

/**
 * Default alpha levels used by Material components.
 */
object ContentAlpha {

    /**
     * Alpha levels for high luminance content in light theme, or low luminance content in dark theme.
     *
     * This content will typically be placed on colored surfaces, so it is important that the
     * contrast here is higher to meet accessibility standards, and increase legibility.
     *
     * These levels are typically used for text / iconography in primary colored tabs /
     * bottom navigation / etc.
     */
    private object HighContrastContentAlpha {
        const val high: Float = 1.00f
        const val medium: Float = 0.74f
        const val disabled: Float = 0.38f
    }

    /**
     * Alpha levels for low luminance content in light theme, or high luminance content in dark theme.
     *
     * This content will typically be placed on grayscale surfaces, so the contrast here can be lower
     * without sacrificing accessibility and legibility.
     *
     * These levels are typically used for body text on the main surface (white in light theme, grey
     * in dark theme) and text / iconography in surface colored tabs / bottom navigation / etc.
     */
    private object LowContrastContentAlpha {
        const val high: Float = 0.87f
        const val medium: Float = 0.60f
        const val disabled: Float = 0.38f
    }

    /**
     * This default implementation uses separate alpha levels depending on the luminance of the
     * incoming color, and whether the theme is light or dark. This is to ensure correct contrast
     * and accessibility on all surfaces.
     *
     * See [HighContrastContentAlpha] and [LowContrastContentAlpha] for what the levels are
     * used for, and under what circumstances.
     */
    @Composable
    private fun contentAlpha(
        /*@FloatRange(from = 0.0, to = 1.0)*/
        highContrastAlpha: Float,
        /*@FloatRange(from = 0.0, to = 1.0)*/
        lowContrastAlpha: Float
    ): Float {
        val contentColor = LocalContentColor.current
        val lightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5
        return if (lightTheme) {
            if (contentColor.luminance() > 0.5) highContrastAlpha else lowContrastAlpha
        } else {
            if (contentColor.luminance() < 0.5) highContrastAlpha else lowContrastAlpha
        }
    }

    /**
     * A low level of content alpha used to represent disabled components, such as text in a
     * disabled [Button].
     */
    val disabled: Float
        @Composable
        get() = contentAlpha(
            highContrastAlpha = HighContrastContentAlpha.disabled,
            lowContrastAlpha = LowContrastContentAlpha.disabled
        )

    /**
     * A high level of content alpha, used to represent high emphasis text such as input text in a
     * selected [TextField].
     */
    val high: Float
        @Composable
        get() = contentAlpha(
            highContrastAlpha = HighContrastContentAlpha.high,
            lowContrastAlpha = LowContrastContentAlpha.high
        )

    /**
     * A medium level of content alpha, used to represent medium emphasis text such as
     * placeholder text in a [TextField].
     */
    val medium: Float
        @Composable
        get() = contentAlpha(
            highContrastAlpha = HighContrastContentAlpha.medium,
            lowContrastAlpha = LowContrastContentAlpha.medium
        )

    /**
     * The default color opacity used for an [OutlinedButton]'s border color
     */
    const val OutlinedBorderOpacity = 0.12f

    /**
     * The default alpha value of a divider is 0.12f.
     */
    const val Divider = 0.12f
}