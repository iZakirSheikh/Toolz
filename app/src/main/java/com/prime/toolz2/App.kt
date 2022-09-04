package com.prime.toolz2

import android.app.Application
import androidx.compose.material.MaterialTheme
import dagger.hilt.android.HiltAndroidApp
import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.primex.preferences.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class App : Application()



@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the Singleton Implementation of Preferences DataStore.
     */
    @Provides
    @Singleton
    fun preferences(@ApplicationContext context: Context) = Preferences(context)
}

