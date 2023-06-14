package com.prime.toolz

import android.app.Application
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.google.firebase.FirebaseApp
import com.primex.preferences.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TAG = "Gallery"

@Module
@InstallIn(SingletonComponent::class)
object Singleton {
    @Provides
    @Singleton
    fun preferences(@ApplicationContext context: Context) =
        Preferences(context, "shared_preferences")
}

@Module
@InstallIn(ActivityRetainedComponent::class)
object Activity {
    @ActivityRetainedScoped
    @Provides
    fun channel() = SnackbarHostState()
}

@HiltAndroidApp
class Toolz : Application() {
    override fun onCreate() {
        super.onCreate()
        // initialize firebase
        FirebaseApp.initializeApp(this)
    }

    companion object {
        /**
         * The counter counts the number of times this app was launched.
         */
        val KEY_LAUNCH_COUNTER = intPreferenceKey(TAG + "_launch_counter")

        /**
         * The link to PlayStore Market.
         */
        const val GOOGLE_STORE = "market://details?id=" + BuildConfig.APPLICATION_ID

        /**
         * If PlayStore is not available in Users Phone. This will be used to redirect to the
         * WebPage of the app.
         */
        const val FALLBACK_GOOGLE_STORE =
            "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID

        /**
         * Package name for the Google Play Store. Value can be verified here:
         * https://developers.google.com/android/reference/com/google/android/gms/common/GooglePlayServicesUtil.html#GOOGLE_PLAY_STORE_PACKAGE
         */
        const val PKG_GOOGLE_PLAY_STORE = "com.android.vending"
    }
}