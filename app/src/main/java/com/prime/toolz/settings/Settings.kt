package com.prime.toolz.settings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.prime.toolz.R
import com.prime.toolz.core.NightMode
import com.primex.core.Text
import com.primex.preferences.Key
import com.primex.preferences.StringSaver
import com.primex.preferences.booleanPreferenceKey
import com.primex.preferences.intPreferenceKey
import com.primex.preferences.stringPreferenceKey

private const val TAG = "State"

/**
 * Immutable data class representing a preference.
 *
 * @property value The value of the preference.
 * @property title The title text of the preference.
 * @property vector The optional vector image associated with the preference.
 * @property summary The optional summary text of the preference.
 * @param P The type of the preference value.
 */
@Immutable
data class Preference<out P>(
    val value: P,
    @JvmField val title: Text,
    val vector: ImageVector? = null,
    @JvmField val summery: Text? = null,
)

@OptIn(ExperimentalTextApi::class)
private val provider = GoogleFont.Provider(
    "com.google.android.gms.fonts",
    "com.google.android.gms",
    R.array.com_google_android_gms_fonts_certs
)

@Stable
private fun FontFamily(name: String) =
    FontFamily(
        Font(GoogleFont(name), provider, FontWeight.Light),
        Font(GoogleFont(name), provider, FontWeight.Medium),
        Font(GoogleFont(name), provider, FontWeight.Normal),
        Font(GoogleFont(name), provider, FontWeight.Bold),
    )

/**
 * Interface representing the settings.
 *
 * @property nightMode The state of the night mode preference.
 * @property colorSystemBars The state of the color system bars preference.
 * @property hideStatusBar The state of the hide status bar preference.
 * @property dynamicColors The state of the dynamic colors preference.
 * @property numberGroupSeparator The state of the number group separator preference.
 */

@Stable
interface Settings {

    val nightMode: Preference<NightMode>
    val colorSystemBars: Preference<Boolean>
    val hideStatusBar: Preference<Boolean>
    val dynamicColors: Preference<Boolean>
    val numberGroupSeparator: Preference<Char>

    /**
     * Sets the value for the specified key.
     *
     * @param key the key for the setting
     * @param value the value to be set
     * @param S the type of setting
     * @param O the type of value
     */
    fun <S, O> set(key: Key<S, O>, value: O)

    companion object {
        const val route = "route_settings"

        /**
         * Provides the direction for the settings route.
         *
         * @return the route for settings
         */
        fun direction() = route

        /**
         * Retrieves/Sets The [NightMode] Strategy
         */
        val KEY_NIGHT_MODE = stringPreferenceKey(
            "${TAG}_night_mode",
            NightMode.FOLLOW_SYSTEM,
            object : StringSaver<NightMode> {
                override fun save(value: NightMode): String = value.name
                override fun restore(value: String): NightMode = NightMode.valueOf(value)
            },
        )

        val KEY_COLOR_STATUS_BAR = booleanPreferenceKey(TAG + "_color_status_bar", false)
        val KEY_HIDE_STATUS_BAR = booleanPreferenceKey(TAG + "_hide_status_bar", false)
        val KEY_DYNAMIC_COLORS = booleanPreferenceKey(TAG + "_dynamic_colors", true)

        /**
         * The counter counts the number of times this app was launched.
         */
        val KEY_LAUNCH_COUNTER = intPreferenceKey(TAG + "_launch_counter")

        val KEY_GROUP_SEPARATOR =
            stringPreferenceKey(
                TAG + "_group_separator",
                ',',
                object : StringSaver<Char> {
                    override fun restore(value: String): Char = value[0]
                    override fun save(value: Char): String = "$value"
                }
            )
        val LatoFontFamily = FontFamily("Roboto")
    }
}