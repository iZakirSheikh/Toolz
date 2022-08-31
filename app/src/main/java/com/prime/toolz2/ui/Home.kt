package com.prime.toolz2.ui


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.prime.toolz2.common.compose.LocalNavController
import com.prime.toolz2.common.compose.LocalWindowPadding
import com.prime.toolz2.settings.Settings
import com.prime.toolz2.settings.SettingsViewModel
import com.prime.toolz2.ui.converter.MainGraphRoutes
import com.prime.toolz2.ui.converter.UnitConverter
import com.prime.toolz2.ui.converter.UnitConverterViewModel
import cz.levinzonr.saferoute.core.ProvideRouteSpecArgs
import cz.levinzonr.saferoute.core.RouteSpec


@OptIn(ExperimentalAnimationApi::class)
private val DefaultEnterTransition =  scaleIn(
    initialScale = 0.98f,
    animationSpec = tween(220, delayMillis = 90)
) + fadeIn(animationSpec = tween(700))

private val DefaultExitTransition = fadeOut(tween(700))

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Home(){
    // Currently; supports only 1 Part
    // add others in future
    // including support for more tools, like direction, prime factorization etc.
    // also support for navGraph.
    val controller = rememberAnimatedNavController()

    // The padding suggested by the wrapper.
    val padding = LocalWindowPadding.current
    CompositionLocalProvider(
        LocalNavController provides controller
    ) {
        AnimatedNavHost(
            navController = LocalNavController.current,
            startDestination = MainGraphRoutes.UnitConverter.route,
            enterTransition = { DefaultEnterTransition},
            exitTransition = { DefaultExitTransition },
            popEnterTransition = {DefaultEnterTransition},
            popExitTransition = { DefaultExitTransition },
            modifier = Modifier.navigationBarsPadding().padding(padding),
        ) {
            composable(MainGraphRoutes.UnitConverter) {
                val viewModel = hiltViewModel<UnitConverterViewModel>()
                UnitConverter(viewModel = viewModel)
            }

            composable(MainGraphRoutes.Settings) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                Settings(viewModel = viewModel)
            }
        }
    }
}


///missing fun
@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.composable(
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