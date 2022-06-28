package com.prime.toolz2.settings

import com.primex.preferences.*

private const val TAG = "PrefKeys"

object GlobalKeys {
    /**
     * Retrieves/Sets The [NightMode] Strategy
     */
    val NIGHT_MODE = stringPreferenceKey(
        "${TAG}_night_mode",
        NightMode.NO,
        object : StringSaver<NightMode> {
            override fun save(value: NightMode): String = value.name

            override fun restore(value: String): NightMode = NightMode.valueOf(value)
        }
    )

    val FONT_FAMILY = stringPreferenceKey(
        TAG + "_font_family",
        FontFamily.PROVIDED,
        object : StringSaver<FontFamily> {
            override fun save(value: FontFamily): String = value.name

            override fun restore(value: String): FontFamily = FontFamily.valueOf(value)
        }
    )


    val FORCE_COLORIZE = booleanPreferenceKey(TAG + "_force_colorize", false)

    val COLOR_STATUS_BAR = booleanPreferenceKey(TAG + "_color_status_bar", false)

    val HIDE_STATUS_BAR = booleanPreferenceKey(TAG + "_hide_status_bar", false)

    val GROUP_SEPARATOR = stringPreferenceKey(
        name = TAG + "_group_separator",
        defaultValue = ',',
        saver = object : StringSaver<Char> {
            override fun restore(value: String): Char = value[0]

            override fun save(value: Char): String = "$value"
        }
    )

    val DECIMAL_SEPARATOR = stringPreferenceKey(
        name = TAG + "_decimal_separator",
        defaultValue = "."
    )

    val NUMBER_OF_DECIMALS = intPreferenceKey(
        name = TAG + "_number_of_decimals",
        defaultValue = 5
    )

    val FONT_SCALE = floatPreferenceKey(
        TAG + "_font_scale",
        defaultValue = 1.0f
    )
}