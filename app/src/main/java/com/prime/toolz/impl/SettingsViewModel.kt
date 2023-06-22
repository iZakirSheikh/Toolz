package com.prime.toolz.impl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.toolz.R
import com.prime.toolz.settings.Preference
import com.prime.toolz.settings.Settings
import com.primex.core.Text
import com.primex.preferences.Key
import com.primex.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: Preferences,
    private val channel: SnackbarHostState,
) : ViewModel(), Settings {

    @Deprecated("Find new solution.")
    private fun <T> Flow<T>.asComposeState(): State<T> {
        val state = mutableStateOf(runBlocking { first() })
        onEach { state.value = it }.launchIn(viewModelScope)
        return state
    }


    override val nightMode by
    preferences[Settings.KEY_NIGHT_MODE].map {
        Preference(
            value = it,
            title = Text(R.string.dark_mode),
            summery = Text(R.string.dark_mode_summery),
            vector = Icons.Outlined.Lightbulb
        )
    }.asComposeState()
    override val colorSystemBars by
    preferences[Settings.KEY_COLOR_STATUS_BAR].map {
        Preference(
            vector = null,
            title = Text(R.string.color_system_bars),
            summery = Text(R.string.color_system_bars_summery),
            value = it
        )
    }.asComposeState()

    override val hideStatusBar by
    preferences[Settings.KEY_HIDE_STATUS_BAR].map {
        Preference(
            value = it,
            title = Text(R.string.hide_status_bar),
            summery = Text(R.string.hide_status_bar_summery),
            vector = Icons.Outlined.HideImage
        )
    }.asComposeState()

    override val dynamicColors by with(preferences) {
        preferences[Settings.KEY_DYNAMIC_COLORS].map {
            Preference(
                value = it,
                title = Text(R.string.dynamic_colors),
                summery = Text(R.string.dynamic_colors_summery),
                vector = Icons.Outlined.HideImage
            )
        }.asComposeState()
    }

    override val numberGroupSeparator by
    preferences[Settings.KEY_GROUP_SEPARATOR].map {
        Preference(
            value = it,
            title = Text(R.string.group_separator),
            summery = Text(R.string.group_separator_summery)
        )
    }.asComposeState()

    override fun <S, O> set(key: Key<S, O>, value: O) {
        viewModelScope.launch { preferences[key] = value }
    }
}