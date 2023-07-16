package com.prime.toolz.core.compose

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * Used to provide access to the [NavHostController] through composition without needing to pass it down the tree.
 *
 * To use this composition local, you can call [LocalNavController.current] to get the [NavHostController].
 * If no [NavHostController] has been set, an error will be thrown.
 *
 * Example usage:
 *
 * ```
 * val navController = LocalNavController.current
 * navController.navigate("destination")
 * ```
 */
val LocalNavController =
    staticCompositionLocalOf<NavHostController> {
        // FIXME: Maybe: Replace with some concrete controller; so that no error is thrown.
        error("no local nav host controller found")
    }

/**
 * [CompositionLocal] containing the [WindowSizeClass].
 *
 * This [CompositionLocal] is used to access the current [WindowSizeClass] within a composition.
 * If no [WindowSizeClass] is found in the composition hierarchy, a default [WindowSizeClass]
 * will be calculated based on the provided size.
 *
 * Usage:
 *
 * ```
 * val windowSizeClass = LocalWindowSizeClass.current
 * // Use the windowSizeClass value within the composition
 * ```
 * @optIn ExperimentalMaterial3WindowSizeClassApi
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    WindowSizeClass.calculateFromSize(DpSize(367.dp, 900.dp))
}