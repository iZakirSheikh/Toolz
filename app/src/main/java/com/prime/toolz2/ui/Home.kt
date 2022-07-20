package com.prime.toolz2.ui


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.LocalNavController
import com.prime.toolz2.common.compose.LocalSnackDataChannel
import com.prime.toolz2.common.compose.SnackDataChannel
import com.prime.toolz2.common.compose.stringResource
import com.prime.toolz2.settings.Settings
import com.prime.toolz2.settings.SettingsViewModel
import com.prime.toolz2.ui.converter.MainGraphRoutes
import com.prime.toolz2.ui.converter.UnitConverter
import com.prime.toolz2.ui.converter.UnitConverterViewModel
import cz.levinzonr.saferoute.core.ProvideRouteSpecArgs
import cz.levinzonr.saferoute.core.RouteSpec
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Home() {
    // Currently; supports only 1 Part
    // add others in future
    // including support for more tools, like direction, prime factorization etc.
    // also support for navGraph.
    val controller = rememberAnimatedNavController()

    // channel/Messenger to handle events using SnackBar
    // A message channel
    val channel = remember { SnackDataChannel() }

    // The state of the Snackbar
    val snackbar = remember {
        SnackbarHostState()
    }
    val resource = LocalContext.current.resources
    LaunchedEffect(key1 = channel) {
        channel.receiveAsFlow().collect { (label, message, duration, action) ->
            // dismantle the given snack and use the corresponding components
            val result = snackbar.showSnackbar(
                message = resource.stringResource(message).text,
                actionLabel = resource.stringResource(label)?.text ?: resource.getString(R.string.dismiss),
                duration = duration
            )
            // action based on
            when (result) {
                SnackbarResult.ActionPerformed -> action?.invoke()
                SnackbarResult.Dismissed -> {
                    //do nothing
                }
            }
        }
    }


    CompositionLocalProvider(
        LocalNavController provides controller,
        LocalSnackDataChannel provides channel
    ) {
        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbar)) { inner ->
            AnimatedNavHost(
                navController = LocalNavController.current,
                startDestination = MainGraphRoutes.UnitConverter.route,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(inner),
                enterTransition = {
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(220, delayMillis = 90)
                    )
                },
                exitTransition = { fadeOut(tween(700)) },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(220, delayMillis = 90)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = { fadeOut(animationSpec = tween(700)) }
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

