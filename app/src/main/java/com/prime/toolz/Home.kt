package com.prime.toolz

import android.app.Activity
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.twotone.Forum
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.prime.toolz.chatbot.ChatBot
import com.prime.toolz.converter.UnitConverter
import com.prime.toolz.core.ContentPadding
import com.prime.toolz.core.NightMode
import com.prime.toolz.core.billing.Product
import com.prime.toolz.core.billing.purchased
import com.prime.toolz.core.compose.Route
import com.prime.toolz.core.compose.Scaffold
import com.prime.toolz.impl.ChatBotViewModel
import com.prime.toolz.impl.SettingsViewModel
import com.prime.toolz.impl.UnitConverterViewModel
import com.prime.toolz.settings.Settings
import com.primex.material3.IconButton

private const val TAG = "Home"

/**
 * A short-hand alias of [MaterialTheme]
 */
typealias Material = MaterialTheme

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
 * A simple composable that helps in resolving the current app theme as suggested by the [Gallery.NIGHT_MODE]
 */
@Composable
@NonRestartableComposable
private fun resolveAppThemeState(): Boolean {
    val mode by preference(key = Settings.KEY_NIGHT_MODE)
    return when (mode) {
        NightMode.YES -> true
        NightMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        else -> false
    }
}

// Default Enter/Exit Transitions.
private val EnterTransition =
    scaleIn(tween(220, 90), 0.98f) + fadeIn(tween(700))
private val ExitTransition =
    fadeOut(tween(700))

private val DarkColorScheme =
    darkColorScheme(background = Color(0xFF0E0E0F))
private val LightColorScheme =
    lightColorScheme()

@Composable
@NonRestartableComposable
private fun Material(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // Dynamic color is available on Android 12+
    content: @Composable () -> Unit,
) {
    // compute the color scheme.
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Pass values to the actual composable.
    MaterialTheme(
        colorScheme = colorScheme, content = content
    )
}

/**
 * A simple structure of the NavGraph.
 */
@OptIn(ExperimentalAnimationApi::class)
@NonRestartableComposable
@Composable
private fun NavGraph(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {
    // In order to navigate and remove the need to pass controller below UI components.
    // pass controller as composition local.
    CompositionLocalProvider(LocalNavController provides controller, content = {
        // actual paragraph
        AnimatedNavHost(navController = controller,
            modifier = modifier,
            startDestination = UnitConverter.route, //
            enterTransition = { EnterTransition },
            exitTransition = { ExitTransition },
            builder = {
                //UnitConverter
                composable(UnitConverter.route) {
                    val viewModel = hiltViewModel<UnitConverterViewModel>()
                    UnitConverter(state = viewModel)
                }

                //Settings
                composable(Settings.route) {
                    val viewModel = hiltViewModel<SettingsViewModel>()
                    Settings(state = viewModel)
                }

                //ChatBot
                composable(ChatBot.route){
                    val viewModel = hiltViewModel<ChatBotViewModel>()
                    ChatBot(state = viewModel)
                }
            })
    })
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Home(channel: SnackbarHostState) {
    val navController = rememberAnimatedNavController()
    val darkTheme = resolveAppThemeState()
    // Observe if the user wants dynamic light.
    // Supports only above android 12+
    val dynamicColor by preference(key = Settings.KEY_DYNAMIC_COLORS)

    // current route.
    val current by navController.currentBackStackEntryAsState()
    val hideNavigationBar = when (current?.destination?.route) {
        Settings.route -> true
        else -> false
    }
    Material(darkTheme, dynamicColor) {
        // handle the color of navBars.
        // handle the color of navBars.
        val view = LocalView.current
        // Observe if the user wants to color the SystemBars
        val colorSystemBars by preference(key = Settings.KEY_COLOR_STATUS_BAR)
        val systemBarsColor =
            if (colorSystemBars) Material.colorScheme.primary else Color.Transparent
        val hideStatusBar by preference(key = Settings.KEY_HIDE_STATUS_BAR)
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.navigationBarColor = systemBarsColor.toArgb()
                window.statusBarColor = systemBarsColor.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !darkTheme && !colorSystemBars
                //
                if (hideStatusBar) WindowCompat.getInsetsController(window, view)
                    .hide(WindowInsetsCompat.Type.statusBars())
                else WindowCompat.getInsetsController(window, view)
                    .show(WindowInsetsCompat.Type.statusBars())
            }
        }
        //Place the content.
        val vertical = LocalWindowSizeClass.current.widthSizeClass < WindowWidthSizeClass.Medium
        val progress by LocalsProvider.current.inAppUpdateProgress
        Scaffold(
            vertical = vertical,
            channel = channel,
            content = { NavGraph(controller = navController) },
            progress = progress,
            hideNavigationBar = hideNavigationBar,
            tabs = {

                // Unit Converter.
                Route(
                    title = "Converter",
                    icon = Icons.Outlined.ChangeCircle,
                    checked = current?.destination?.route == UnitConverter.route,
                    onClick = {
                        navController.navigate(UnitConverter.direction()){
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    vertical = vertical,
                )

                // ChatBot
                Route(
                    title = "ChatBot",
                    icon = Icons.TwoTone.Forum,
                    checked = current?.destination?.route == ChatBot.route,
                    onClick = {
                        navController.navigate(ChatBot.direction()){
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    vertical = vertical,
                )

                // Settings
                val padding = if (vertical)
                    PaddingValues(start = ContentPadding.xLarge)
                else
                    PaddingValues(top = ContentPadding.xLarge)

                IconButton(
                    icon = Icons.Outlined.Tune,
                    contentDescription = "Settings",
                    onClick = { navController.navigate(Settings.direction()) },
                    modifier = Modifier.padding(padding)
                )

                // Buy app
                val disableAds by purchase(id = Product.DISABLE_ADS)
                val provider = LocalsProvider.current
                if (!disableAds.purchased)
                    IconButton(
                        icon = Icons.Outlined.ShoppingCart,
                        contentDescription = "Buy app",
                        onClick = { provider.launchBillingFlow(Product.DISABLE_ADS) },
                    )
            },
        )
    }
}
