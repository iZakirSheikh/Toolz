@file:Suppress("NOTHING_TO_INLINE")

package com.prime.toolz2.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primex.preferences.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Preference<out P>(
    val value: P,
    val title: String,
    val vector: ImageVector? = null,
    val summery: String? = null,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    val darkUiMode =
        with(preferences) {
            preferences[GlobalKeys.NIGHT_MODE].map {
                Preference(
                    value = when (it) {
                        NightMode.YES -> true
                        else -> false
                    },
                    title = "Dark Mode",
                    summery = "Click to change the app night/light mode.",
                    vector = Icons.Outlined.Lightbulb
                )
            }.composeState()
        }

    val font =
        with(preferences) {
            preferences[GlobalKeys.FONT_FAMILY].map {
                Preference(
                    vector = Icons.Default.TextFields,
                    title = "Font",
                    summery = "Choose font to better reflect your desires.",
                    value = it
                )
            }.composeState()
        }

    val colorStatusBar =
        with(preferences) {
            preferences[GlobalKeys.COLOR_STATUS_BAR]
                .map {
                    Preference(
                        vector = null,
                        title = "Color Status Bar",
                        summery = "Force color status bar.",
                        value = it
                    )
                }
                .composeState()
        }

    val hideStatusBar =
        with(preferences) {
            preferences[GlobalKeys.HIDE_STATUS_BAR]
                .map {
                    Preference(
                        value = it,
                        title = "Hide Status Bar",
                        summery = "hide status bar for immersive view",
                        vector = Icons.Outlined.HideImage
                    )
                }
                .composeState()
        }

    val forceAccent =
        with(preferences) {
            preferences[GlobalKeys.FORCE_COLORIZE]
                .map {
                    Preference(
                        value = it,
                        title = "Force Accent Color",
                        summery = "Normally the app follows the rule of using 10% accent color. But if this setting is toggled it can make it use  more than 30%"
                    )
                }
                .composeState()
        }


    val fontScale =
        with(preferences) {
            preferences[GlobalKeys.FONT_SCALE]
                .map {
                    Preference(
                        value = it,
                        title = "Font Scale",
                        summery = "Zoom in or out the text shown on the screen.",
                        vector = Icons.Outlined.ZoomIn
                    )
                }
                .composeState()
        }


    val groupSeparator =
        with(preferences) {
            preferences[GlobalKeys.GROUP_SEPARATOR].map {
                Preference(
                    value = it,
                    title = "Font Scale",
                    summery = "Zoom in or out the text shown on the screen."
                )
            }
                .composeState()
        }


    fun <T> set(key: Key<T>, value: T) {
        viewModelScope.launch {
            preferences[key] = value
        }
    }

    fun <T> set(key: Key1<T>, value: T) {
        viewModelScope.launch {
            preferences[key] = value
        }
    }

    fun <T, O> set(key: Key2<T, O>, value: O) {
        viewModelScope.launch {
            preferences[key] = value
        }
    }

    fun <T, O> set(key: Key3<T, O>, value: O) {
        viewModelScope.launch {
            preferences[key] = value
        }
    }
}


context (Preferences, ViewModel) private fun <T> Flow<T>.composeState(): State<T> {

    val state = mutableStateOf(
        obtain()
    )
    onEach {
        state.value = it
    }
        .launchIn(viewModelScope)
    return state
}

