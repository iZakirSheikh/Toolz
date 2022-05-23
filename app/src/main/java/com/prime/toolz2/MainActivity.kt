package com.prime.toolz2

import LocalWindowSizeClass
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.prime.toolz2.settings.PrefKeys
import com.prime.toolz2.theme.Material
import com.prime.toolz2.ui.Home
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

            Material(isDark = isSystemInDarkTheme()) {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    CompositionLocalProvider(
                        LocalElevationOverlay provides null,
                        LocalWindowSizeClass provides sWindow
                    ) {
                        Home()
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        preferences = Preferences.get(newBase!!)
        val configuration = Configuration()
        configuration.setToDefaults()
        configuration.fontScale = with(preferences){get(PrefKeys.FONT_SCALE).obtain()}
        applyOverrideConfiguration(configuration)
        super.attachBaseContext(newBase)
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