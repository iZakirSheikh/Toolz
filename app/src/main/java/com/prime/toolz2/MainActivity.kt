package com.prime.toolz2

import LocalWindowSizeClass
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.prime.toolz2.settings.GlobalKeys
import com.prime.toolz2.settings.NightMode
import com.prime.toolz2.ui.Home
import com.primex.preferences.LocalPreferenceStore
import com.primex.preferences.Preferences
import dagger.hilt.android.AndroidEntryPoint
import rememberWindowSizeClass
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fAnalytics: FirebaseAnalytics

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtain the FirebaseAnalytics instance.
        fAnalytics = Firebase.analytics

        // first thing first install
        // splash screen
        initSplashScreen(
            savedInstanceState == null //why?
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)


        // actual compose content.
        setContent {

            val sWindow = rememberWindowSizeClass()
            CompositionLocalProvider(
                LocalElevationOverlay provides null,
                LocalWindowSizeClass provides sWindow,
                LocalPreferenceStore provides preferences,
                LocalSystemUiController provides rememberSystemUiController()
            ) {
                Material(isDark = resolveAppThemeState()) {
                    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                        Home()
                    }
                }
            }
        }
    }
}


/**
 * Manages SplashScreen
 */
fun MainActivity.initSplashScreen(isColdStart: Boolean) {
    // Install Splash Screen and Play animation when cold start.
    installSplashScreen().let { splashScreen ->
        // Animate entry of content
        // if cold start
        if (isColdStart)
            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                val splashScreenView = splashScreenViewProvider.view
                // Create your custom animation.
                val alpha = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.ALPHA,
                    1f,
                    0f
                )
                alpha.interpolator = AnticipateInterpolator()
                alpha.duration = 700L

                // Call SplashScreenView.remove at the end of your custom animation.
                alpha.doOnEnd { splashScreenViewProvider.remove() }

                // Run your animation.
                alpha.start()
            }
    }
}


@Composable
private fun resolveAppThemeState(): Boolean {
    val preferences = LocalPreferenceStore.current
    val mode by with(preferences) {
        preferences[GlobalKeys.NIGHT_MODE].observeAsState()
    }
    return when (mode) {
        NightMode.YES -> true
        else -> false
    }
}