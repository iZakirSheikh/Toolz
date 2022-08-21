package com.prime.toolz2

import LocalWindowSizeClass
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.prime.toolz2.common.billing.BillingManager
import com.prime.toolz2.common.compose.*
import com.prime.toolz2.settings.GlobalKeys
import com.prime.toolz2.settings.NightMode
import com.prime.toolz2.ui.Home
import com.primex.core.rememberState
import com.primex.preferences.LocalPreferenceStore
import com.primex.preferences.Preferences
import com.primex.preferences.longPreferenceKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rememberWindowSizeClass
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "MainActivity"

private const val RESULT_CODE_APP_UPDATE = 1000

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var fAnalytics: FirebaseAnalytics

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The app has started from scratch if savedInstanceState is null.
        val isColdStart = savedInstanceState == null //why?
        // Obtain the FirebaseAnalytics instance.
        fAnalytics = Firebase.analytics
        // show splash screen
        initSplashScreen(
            isColdStart
        )
        //init
        val channel = SnackDataChannel()
        if (isColdStart) {
            val counter =
                with(preferences) { preferences[GlobalKeys.KEY_LAUNCH_COUNTER].obtain() } ?: 0
            // update launch counter if
            // cold start.
            preferences[GlobalKeys.KEY_LAUNCH_COUNTER] = counter + 1
            // check for updates on startup
            // don't report
            // check silently
            launchUpdateFlow(channel)
            // TODO: Try to reconcile if it is any good to ask for reviews here.
            // launchReviewFlow()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // setup billing manager

        val billingManager =
            BillingManager(
                context = this,
                products = arrayOf(
                    BillingTokens.DISABLE_ASD_IN_APP_PRODUCT
                )
            )
        lifecycle.addObserver(billingManager)

        setContent {
            val sWindow = rememberWindowSizeClass()

            // observe the change to density
            val density = LocalDensity.current
            val fontScale by with(preferences) { get(GlobalKeys.FONT_SCALE).observeAsState() }
            val modified = Density(density = density.density, fontScale = fontScale)
            // The state of the Snackbar
            val snackbar = remember(::SnackbarHostState)
            // observe the channel
            // emit the updates
            val resource = LocalContext.current.resources
            LaunchedEffect(key1 = channel) {
                channel.receiveAsFlow().collect { (label, message, duration, action) ->
                    // dismantle the given snack and use the corresponding components
                    val result = snackbar.showSnackbar(
                        message = resource.stringResource(message).text,
                        actionLabel = resource.stringResource(label)?.text
                            ?: resource.getString(R.string.dismiss),
                        duration = duration
                    )
                    // action based on
                    when (result) {
                        SnackbarResult.ActionPerformed -> action?.invoke()
                        SnackbarResult.Dismissed -> { /*do nothing*/
                        }
                    }
                }
            }
            var windowPadding by rememberState(initial = PaddingValues(0.dp))
            CompositionLocalProvider(
                LocalWindowPadding provides windowPadding,
                LocalElevationOverlay provides null,
                LocalWindowSizeClass provides sWindow,
                LocalPreferenceStore provides preferences,
                LocalSystemUiController provides rememberSystemUiController(),
                LocalDensity provides modified,
                LocalSnackDataChannel provides channel,
                LocalBillingManager provides billingManager
            ) {
                Material(isDark = resolveAppThemeState()) {
                    val state = rememberScaffoldState(snackbarHostState = snackbar)
                    // scaffold
                    // FixMe: Re-design the SnackBar Api.
                    // Introduce: SideBar and Bottom Bar.
                    Scaffold(scaffoldState = state) { inner ->
                        windowPadding = inner
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
private fun MainActivity.initSplashScreen(isColdStart: Boolean) {
    // Install Splash Screen and Play animation when cold start.
    installSplashScreen()
        .let { splashScreen ->
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

private val KEY_LAST_REVIEW_TIME =
    longPreferenceKey(
        TAG + "_last_review_time"
    )

private const val MIN_LAUNCH_COUNT = 20
private val MAX_DAYS_BEFORE_FIRST_REVIEW = TimeUnit.DAYS.toMillis(7)
private val MAX_DAY_AFTER_FIRST_REVIEW = TimeUnit.DAYS.toMillis(10)

/**
 * A convince method for launching an in-app review.
 * The review API is guarded by some conditions which are
 * * The first review will be asked when launchCount is > [MIN_LAUNCH_COUNT] and daysPassed >=[MAX_DAYS_BEFORE_FIRST_REVIEW]
 * * After asking first review then after each [MAX_DAY_AFTER_FIRST_REVIEW] a review dialog will be showed.
 * Note: The review should not be asked after every coldBoot.
 */
fun Activity.launchReviewFlow() {
    require(this is MainActivity)
    val count =
        with(preferences) { preferences[GlobalKeys.KEY_LAUNCH_COUNTER].obtain() } ?: 0

    // the time when lastly asked for review
    val lastAskedTime =
        with(preferences) { preferences[KEY_LAST_REVIEW_TIME].obtain() }

    val firstInstallTime =
        com.primex.core.runCatching(TAG + "_review") {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        }

    val currentTime = System.currentTimeMillis()

    // Only first time we should not ask immediately
    // however other than this whenever we do some thing of appreciation.
    // we should ask for review.
    val askFirstReview =
        lastAskedTime == null &&
                firstInstallTime != null &&
                count >= MIN_LAUNCH_COUNT &&
                currentTime - firstInstallTime >= MAX_DAYS_BEFORE_FIRST_REVIEW

    val askNormalOne =
        lastAskedTime != null &&
                count >= MIN_LAUNCH_COUNT &&
                currentTime - lastAskedTime >= MAX_DAY_AFTER_FIRST_REVIEW

    // The flow has finished. The API does not indicate whether the user
    // reviewed or not, or even whether the review dialog was shown. Thus, no
    // matter the result, we continue our app flow.
    lifecycleScope.launch {
        if (askFirstReview || askNormalOne) {
            val reviewManager = ReviewManagerFactory.create(this@launchReviewFlow)
            com.primex.core.runCatching(TAG) {
                // update the last asking
                preferences[KEY_LAST_REVIEW_TIME] = System.currentTimeMillis()
                val info = reviewManager.requestReview()
                reviewManager.launchReviewFlow(this@launchReviewFlow, info)
                //host.fAnalytics.
            }
        }
    }
}

private const val FLEXIBLE_UPDATE_MAX_STALENESS_DAYS = 2

/**
 * A utility method to check for updates.
 * @param channel [SnackDataChannel] to report errors, inform users about the availability of update.
 * @param report simple messages.
 */
fun Activity.launchUpdateFlow(
    channel: SnackDataChannel,
    report: Boolean = false,
) {
    require(this is MainActivity)
    lifecycleScope.launch {
        val manager = AppUpdateManagerFactory.create(this@launchUpdateFlow)
        with(manager) {
            val result =
                kotlin.runCatching {
                    requestUpdateFlow()
                        .collect { result ->
                            when (result) {
                                AppUpdateResult.NotAvailable -> if (report)
                                    channel.send("The app is already updated to the latest version.")
                                is AppUpdateResult.InProgress -> {
                                    //FixMe: Publish progress
                                    val state = result.installState
                                    val progress =
                                        state.bytesDownloaded() / (state.totalBytesToDownload() + 0.1) * 100
                                    Log.i(TAG, "check: $progress")
                                    // currently don't show any message
                                    // future version find ways to show progress.
                                }
                                is AppUpdateResult.Downloaded -> {
                                    val info = requestAppUpdateInfo()
                                    //when update first becomes available
                                    //don't force it.
                                    // make it required when staleness days overcome allowed limit
                                    val isFlexible =
                                        (info.clientVersionStalenessDays() ?: -1) <=
                                                FLEXIBLE_UPDATE_MAX_STALENESS_DAYS

                                    // forcefully update; if it's flexible
                                    if (!isFlexible)
                                        completeUpdate()
                                    else
                                    // ask gracefully
                                        channel.send(
                                            message = "An update has just been downloaded.",
                                            label = "RESTART",
                                            action = this::completeUpdate,
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    // no message needs to be shown
                                }
                                is AppUpdateResult.Available -> {
                                    // if user choose to skip the update handle that case also.
                                    val isFlexible =
                                        (result.updateInfo.clientVersionStalenessDays() ?: -1) <=
                                                FLEXIBLE_UPDATE_MAX_STALENESS_DAYS
                                    if (isFlexible)
                                        result.startFlexibleUpdate(
                                            activity = this@launchUpdateFlow,
                                            RESULT_CODE_APP_UPDATE
                                        )
                                    else
                                        result.startImmediateUpdate(
                                            activity = this@launchUpdateFlow,
                                            RESULT_CODE_APP_UPDATE
                                        )
                                    // no message needs to be shown
                                }
                            }
                        }
                }
            Log.d(TAG, "launchUpdateFlow() returned: $result")
        }
    }
}