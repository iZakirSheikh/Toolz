package com.prime.toolz2

import android.content.Context
import com.primex.preferences.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the Singleton Implementation of Preferences DataStore.
     */
    // TODO: Try to implement Preferences using non-singleton Implementation.
    @Provides
    @Singleton
    fun preferences(@ApplicationContext context: Context) = Preferences.get(context)
}