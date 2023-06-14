package com.prime.toolz

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.Purchase
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.prime.toolz.core.billing.Advertiser
import com.prime.toolz.core.billing.BillingManager
import com.prime.toolz.core.billing.Product
import com.prime.toolz.core.billing.get
import com.prime.toolz.core.billing.observeAsState
import com.prime.toolz.core.billing.purchased
import com.primex.preferences.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "MainActivity"

/**
 * An interface defining the methods and properties needed for common app functionality,
 * such as in-app updates, showing ads, and launching the app store.
 *
 * This interface is intended to be implemented by a class that is scoped to the entire app,
 * and is accessible from all parts of the app hierarchy.
 *
 * @see DefaultProvider
 */
@Stable
interface Provider {

    /**
     * A simple property that represents the progress of the in-app update.
     *
     * The progress is represented as a [State] object, which allows you to observe changes to the
     * progress value.
     *
     * The progress value is a float between 0.0 and 1.0, indicating the percentage of the update
     * that has been completed. The Float.NaN represents a default value when no update is going on.
     *
     */
    val inAppUpdateProgress: State<Float>

    /**
     * A simple channel for sending messages
     */
    val channel: SnackbarHostState

    /**
     * A utility extension function for showing interstitial ads.
     * * Note: The ad will not be shown if the app is adFree Version.
     *
     * @param force If `true`, the ad will be shown regardless of the AdFree status.
     * @param action A callback to be executed after the ad is shown.
     */
    fun showAd(force: Boolean = false, action: (() -> Unit)? = null)

    /**
     * This uses the provider to submit message to [ToastHostState]
     *
     * @see ToastHostState.show
     */
    fun snack(
        title: String,
        action: String? = null,
        duration: SnackbarDuration = if (action == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    )


    /**
     * A utility method to launch the in-app update flow, with an option to report low-priority
     * issues to the user via a Toast.
     *
     * @param report If `true`, low-priority issues will be reported to the user using the
     *               ToastHostState channel.
     */
    fun launchUpdateFlow(report: Boolean = false)

    /**
     * This is a convenient method for launching an in-app review process, with some built-in
     * conditions and guardrails.
     * Specifically, this method will only launch the review dialog if certain criteria are met,
     * as follows:
     *
     * - The app has been launched at least [MIN_LAUNCH_COUNT] times.
     * - At least [MAX_DAYS_BEFORE_FIRST_REVIEW] days have passed since the first launch.
     * - If a review has already been prompted, at least [MAX_DAYS_AFTER_FIRST_REVIEW] days have
     * passed since the last review prompt.
     *
     * These criteria are designed to ensure that the review prompt is only shown at appropriate
     * intervals, and that users are not repeatedly prompted to leave a review.
     *
     * Note that this method should not be used to prompt for a review after every cold boot or launch of the app.
     */
    fun launchReviewFlow()

    /**
     * Launches the Google Play Store app for this app's package.
     *
     * This function creates an intent to open the Google Play Store app for this app's package.
     * If the Google Play Store app is not installed, the intent will open the Play Store website instead.
     *
     * Note: This function requires the `android.permission.INTERNET` permission to be declared in your app's manifest file.
     */
    fun launchAppStore()

    /**
     * @see Preferences.observeAsState
     */
    @Composable
    @NonRestartableComposable
    fun <S, O> observeAsState(key: Key.Key1<S, O>): State<O?>

    /**
     * @see Preferences.observeAsState
     */
    @Composable
    @NonRestartableComposable
    fun <S, O> observeAsState(key: Key.Key2<S, O>): State<O>

    /**
     * @see BillingManager.observeAsState
     */
    @Composable
    @NonRestartableComposable
    fun observeAsState(product: String): State<Purchase?>

    /**
     * @see BillingManager.launchBillingFlow
     */
    fun launchBillingFlow(id: String)
}

/**
 * A [staticCompositionLocalOf] variable that provides access to the [Provider] interface.
 *
 * The [Provider] interface defines common methods that can be implemented by an activity that
 * uses a single view with child views.
 * This local composition allows child views to access the implementation of the [Provider]
 * interface provided by their parent activity.
 *
 * If the [Provider] interface is not defined, an error message will be thrown.
 */
val LocalsProvider = staticCompositionLocalOf<Provider> {
    error("Provider not defined.")
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

private const val MIN_LAUNCH_COUNT = 10
private val MAX_DAYS_BEFORE_FIRST_REVIEW = TimeUnit.DAYS.toMillis(3)
private val MAX_DAY_AFTER_FIRST_REVIEW = TimeUnit.DAYS.toMillis(5)

private val KEY_LAST_REVIEW_TIME = longPreferenceKey(TAG + "_last_review_time")

private const val FLEXIBLE_UPDATE_MAX_STALENESS_DAYS = 2
private const val RESULT_CODE_APP_UPDATE = 1000

/**
 * Manages SplashScreen
 */
private fun ComponentActivity.initSplashScreen(isColdStart: Boolean) {
    // Install Splash Screen and Play animation when cold start.
    installSplashScreen().let { splashScreen ->
        // Animate entry of content
        // if cold start
        if (isColdStart) splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashScreenView = splashScreenViewProvider.view
            // Create your custom animation.
            val alpha = ObjectAnimator.ofFloat(
                splashScreenView, View.ALPHA, 1f, 0f
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

/**
 * A composable function that uses the [LocalsProvider] to fetch [Preference] as state.
 * @param key A key to identify the preference value.
 * @return A [State] object that represents the current value of the preference identified by the provided key.
 * The value can be null if no preference value has been set for the given key.
 */
@Composable
inline fun <S, O> preference(key: Key.Key1<S, O>): State<O?> {
    val provider = LocalsProvider.current
    return provider.observeAsState(key = key)
}

/**
 * @see [preference]
 */
@Composable
inline fun <S, O> preference(key: Key.Key2<S, O>): State<O> {
    val provider = LocalsProvider.current
    return provider.observeAsState(key = key)
}

/**
 * A composable function that uses the [LocalsProvider] to fetch the purchase state of a product.
 * @param id The product ID to identify the purchase state.
 * @return A [State] object that represents the current purchase state of the provided product ID.
 * The value can be null if there is no purchase associated with the given product ID.
 */
@Composable
inline fun purchase(id: String): State<Purchase?> {
    val provider = LocalsProvider.current
    return provider.observeAsState(product = id)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity(), Provider {

    private val fAnalytics by lazy { FirebaseAnalytics.getInstance(this) }
    private val advertiser by lazy { Advertiser(this) }
    private val billingManager by lazy { BillingManager(this, arrayOf(Product.DISABLE_ADS)) }


    override val inAppUpdateProgress: State<Float> = mutableStateOf(Float.NaN)

    // injectable code.
    @Inject
    lateinit var preferences: Preferences

    @Inject
    override lateinit var channel: SnackbarHostState

    override fun onResume() {
        super.onResume()
        billingManager.refresh()
    }

    override fun onDestroy() {
        billingManager.release()
        super.onDestroy()
    }

    override fun showAd(force: Boolean, action: (() -> Unit)?) {
        val isAdFree = billingManager[Product.DISABLE_ADS].purchased
        if (isAdFree) return // don't do anything
        advertiser.show(this, force, action)
    }

    override fun snack(title: String, action: String?, duration: SnackbarDuration) {
        lifecycleScope.launch {
            channel.showSnackbar(title, action, duration = duration)
        }
    }

    override fun launchAppStore() {
        val result = kotlin.runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Toolz.GOOGLE_STORE)).apply {
                setPackage(Toolz.PKG_GOOGLE_PLAY_STORE)
                addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
            }
            startActivity(intent)
        }
        // if failed start in webview.
        if (result.isFailure) startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Toolz.FALLBACK_GOOGLE_STORE)
            )
        )
    }

    override fun launchBillingFlow(id: String) {
        billingManager.launchBillingFlow(this, id)
    }

    @Composable
    @NonRestartableComposable
    override fun <S, O> observeAsState(key: Key.Key1<S, O>): State<O?> =
        preferences.observeAsState(key = key)

    @Composable
    @NonRestartableComposable
    override fun <S, O> observeAsState(key: Key.Key2<S, O>): State<O> =
        preferences.observeAsState(key = key)

    @Composable
    @NonRestartableComposable
    override fun observeAsState(product: String): State<Purchase?> =
        billingManager.observeAsState(id = product)

    override fun launchReviewFlow() {
        lifecycleScope.launch {
            val count = preferences.value(Toolz.KEY_LAUNCH_COUNTER) ?: 0
            // the time when lastly asked for review
            val lastAskedTime = preferences.value(KEY_LAST_REVIEW_TIME)
            // obtain teh first install time.
            val firstInstallTime = com.primex.core.runCatching(TAG + "_review") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) packageManager.getPackageInfo(
                    packageName, PackageManager.PackageInfoFlags.of(0)
                )
                else packageManager.getPackageInfo(packageName, 0)
            }?.firstInstallTime
            // obtain the current time.
            val currentTime = System.currentTimeMillis()
            // Only first time we should not ask immediately
            // however other than this whenever we do some thing of appreciation.
            // we should ask for review.
            var ask =
                (lastAskedTime == null && firstInstallTime != null && count >= MIN_LAUNCH_COUNT && currentTime - firstInstallTime >= MAX_DAYS_BEFORE_FIRST_REVIEW)
            // check for other condition as well
            // if this is not the first review; ask only if after time passed.
            ask =
                ask || (lastAskedTime != null && count >= MIN_LAUNCH_COUNT && currentTime - lastAskedTime >= MAX_DAY_AFTER_FIRST_REVIEW)
            // return from here if not required to ask
            if (!ask) return@launch
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            com.primex.core.runCatching(TAG) {
                val reviewManager = ReviewManagerFactory.create(this@MainActivity)
                // update the last asking
                preferences[KEY_LAST_REVIEW_TIME] = System.currentTimeMillis()
                val info = reviewManager.requestReview()
                reviewManager.launchReviewFlow(this@MainActivity, info)
                //host.fAnalytics.
            }
        }
    }

    override fun launchUpdateFlow(report: Boolean) {
        lifecycleScope.launch {
            com.primex.core.runCatching(TAG) {
                val manager = AppUpdateManagerFactory.create(this@MainActivity)
                manager.requestUpdateFlow().collect { result ->
                    when (result) {
                        AppUpdateResult.NotAvailable -> if (report) channel.showSnackbar("The app is already updated to the latest version.")

                        is AppUpdateResult.InProgress -> {
                            val state = result.installState

                            val total = state.totalBytesToDownload()
                            val downloaded = state.bytesDownloaded()

                            val progress = when {
                                total <= 0 -> -1f
                                total == downloaded -> Float.NaN
                                else -> downloaded / total.toFloat()
                            }
                            (inAppUpdateProgress as MutableState).value = progress
                            Log.i(TAG, "check: $progress")
                        }

                        is AppUpdateResult.Downloaded -> {
                            val info = manager.requestAppUpdateInfo()
                            //when update first becomes available
                            //don't force it.
                            // make it required when staleness days overcome allowed limit
                            val isFlexible = (info.clientVersionStalenessDays()
                                ?: -1) <= FLEXIBLE_UPDATE_MAX_STALENESS_DAYS

                            // forcefully update; if it's flexible
                            if (!isFlexible) {
                                manager.completeUpdate()
                                return@collect
                            }
                            // else show the toast.
                            val res = channel.showSnackbar(
                                message = "An update has just been downloaded.",
                                actionLabel = "RESTART",
                                duration = SnackbarDuration.Indefinite,
                            )
                            // complete update when ever user clicks on action.
                            if (res == SnackbarResult.ActionPerformed) manager.completeUpdate()
                        }

                        is AppUpdateResult.Available -> {
                            // if user choose to skip the update handle that case also.
                            val isFlexible = (result.updateInfo.clientVersionStalenessDays()
                                ?: -1) <= FLEXIBLE_UPDATE_MAX_STALENESS_DAYS
                            if (isFlexible) result.startFlexibleUpdate(
                                activity = this@MainActivity, RESULT_CODE_APP_UPDATE
                            )
                            else result.startImmediateUpdate(
                                activity = this@MainActivity, RESULT_CODE_APP_UPDATE
                            )
                            // no message needs to be shown
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app has started from scratch if savedInstanceState is null.
        val isColdStart = savedInstanceState == null //why?
        // show splash screen
        initSplashScreen(isColdStart)
        // only run this piece of code if cold start.
        if (isColdStart) {
            val counter = preferences.value(Toolz.KEY_LAUNCH_COUNTER) ?: 0
            // update launch counter if
            // cold start.
            preferences[Toolz.KEY_LAUNCH_COUNTER] = counter + 1
            // check for updates on startup
            // don't report
            // check silently
            launchUpdateFlow()
            // TODO: Try to reconcile if it is any good to ask for reviews here.
            // launchReviewFlow()
        }
        //manually handle decor.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(activity = this)
            CompositionLocalProvider(
                LocalsProvider provides this,
                LocalWindowSizeClass provides windowSizeClass
            ) {
                Home(channel = channel)
            }
        }
    }
}