package com.prime.toolz.impl

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.primex.preferences.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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