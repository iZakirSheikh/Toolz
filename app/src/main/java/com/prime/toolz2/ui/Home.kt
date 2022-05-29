package com.prime.toolz2.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.prime.toolz2.R
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.settings.Settings
import com.prime.toolz2.settings.SettingsViewModel
import com.prime.toolz2.ui.converter.Routes
import com.prime.toolz2.ui.converter.UnitConverter
import com.prime.toolz2.ui.converter.UnitConverterViewModel
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
    val snackbar = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(key1 = channel) {

        channel.receiveAsFlow().collect {
            // dismantle the given snack and use the corresponding components
            with(it.dismantle(context)) {
                val result = snackbar.showSnackbar(
                    message = message,
                    actionLabel = label.ifBlank { context.getString(R.string.dismiss) },
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
    }

    CompositionLocalProvider(
        LocalNavController provides controller,
        LocalSnackDataChannel provides channel
    ) {
        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbar)) { inner ->
            AnimatedNavHost(
                navController = LocalNavController.current,
                startDestination = Routes.UnitConverter.route,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(inner),
                enterTransition = { scaleIn(initialScale = 0.96f) + fadeIn(tween(700)) },
                exitTransition = { scaleOut(targetScale = 0.96f) + fadeOut(tween(700)) },
                popEnterTransition = { fadeIn(animationSpec = tween(700)) },
                popExitTransition = { fadeOut(animationSpec = tween(700)) }
            ) {
                composable(Routes.UnitConverter) {
                    val viewModel = hiltViewModel<UnitConverterViewModel>()
                    UnitConverter(viewModel = viewModel)
                }

                composable(Routes.Settings) {
                    val viewModel = hiltViewModel<SettingsViewModel>()
                    Settings(viewModel = viewModel)
                }

            }
        }
    }
}


private fun Snack.dismantle(context: Context) =
    object {
        val label: String
        val message: String
        val action: (() -> Unit)? = this@dismantle.action
        val duration: SnackbarDuration = this@dismantle.duration

        // init variables from the received snack
        init {
            when (this@dismantle) {
                is Snack.Resource -> {
                    label =
                        if (this@dismantle.label != ResourcesCompat.ID_NULL) context.getString(this@dismantle.label) else ""
                    message =
                        if (this@dismantle.message != ResourcesCompat.ID_NULL) context.getString(
                            this@dismantle.message
                        ) else ""
                }
                is Snack.Text -> {
                    label = this@dismantle.label
                    message = this@dismantle.message
                }
            }
        }
    }