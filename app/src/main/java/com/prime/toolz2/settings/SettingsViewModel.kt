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
import com.primex.core.Text
import com.primex.preferences.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Preference<out P>(
    val value: P,
    val title: Text,
    val vector: ImageVector? = null,
    val summery: Text? = null,
)

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
                    title = Text("Dark Mode"),
                    summery = Text("Click to change the app night/light mode."),
                    vector = Icons.Outlined.Lightbulb
                )
            }.collectAsState()
        }

    val font =
        with(preferences) {
            preferences[GlobalKeys.FONT_FAMILY].map {
                Preference(
                    vector = Icons.Default.TextFields,
                    title = Text("Font"),
                    summery = Text("Choose font to better reflect your desires."),
                    value = it
                )
            }.collectAsState()
        }

    val colorStatusBar =
        with(preferences) {
            preferences[GlobalKeys.COLOR_STATUS_BAR]
                .map {
                    Preference(
                        vector = null,
                        title = Text("Color Status Bar"),
                        summery = Text("Force color status bar."),
                        value = it
                    )
                }
                .collectAsState()
        }

    val hideStatusBar =
        with(preferences) {
            preferences[GlobalKeys.HIDE_STATUS_BAR]
                .map {
                    Preference(
                        value = it,
                        title = Text("Hide Status Bar"),
                        summery = Text("hide status bar for immersive view"),
                        vector = Icons.Outlined.HideImage
                    )
                }
                .collectAsState()
        }

    val forceAccent =
        with(preferences) {
            preferences[GlobalKeys.FORCE_COLORIZE]
                .map {
                    Preference(
                        value = it,
                        title = Text("Force Accent Color"),
                        summery = Text("Normally the app follows the rule of using 10% accent color. But if this setting is toggled it can make it use  more than 30%")
                    )
                }
                .collectAsState()
        }


    val fontScale =
        with(preferences) {
            preferences[GlobalKeys.FONT_SCALE]
                .map {
                    Preference(
                        value = it,
                        title = Text("Font Scale"),
                        summery = Text("Zoom in or out the text shown on the screen."),
                        vector = Icons.Outlined.ZoomIn
                    )
                }
                .collectAsState()
        }


    val groupSeparator =
        with(preferences) {
            preferences[GlobalKeys.GROUP_SEPARATOR].map {
                Preference(
                    value = it,
                    title = Text("Font Scale"),
                    summery = Text("Zoom in or out the text shown on the screen.")
                )
            }
                .collectAsState()
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

context (Preferences, ViewModel)
        private fun <T> Flow<T>.collectAsState(): State<T> {

    val state = mutableStateOf(
        obtain()
    )
    onEach {
        state.value = it
    }
        .launchIn(viewModelScope)
    return state
}