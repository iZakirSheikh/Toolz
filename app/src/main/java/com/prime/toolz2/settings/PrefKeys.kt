package com.prime.toolz2.settings

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.prime.toolz2.common.compose.hsl
import com.primex.preferences.StringSaver
import com.primex.preferences.booleanPreferenceKey
import com.primex.preferences.intPreferenceKey
import com.primex.preferences.stringPreferenceKey
import com.primex.widgets.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json

private const val TAG = "PrefKeys"

object PrefKeys {
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

    val LIGHT_COLORS = stringPreferenceKey(
        name = TAG + "light_colors",
        defaultValue = DefaultLightColorScheme,
        saver = object : StringSaver<Colors> {
            override fun restore(value: String): Colors =
                Json.decodeFromString(ColorSchemeSerializer, value)

            override fun save(value: Colors): String =
                Json.encodeToString(ColorSchemeSerializer, value)
        }
    )

    val DARK_COLORS = stringPreferenceKey(
        name = TAG + "dark_colors",
        defaultValue = DefaultDarkColorScheme,
        saver = object : StringSaver<Colors> {
            override fun restore(value: String): Colors =
                Json.decodeFromString(ColorSchemeSerializer, value)

            override fun save(value: Colors): String =
                Json.encodeToString(ColorSchemeSerializer, value)
        }
    )


    val GROUP_SEPARATOR = stringPreferenceKey(
        name = TAG + "group_separator",
        defaultValue = ',',
        saver = object : StringSaver<Char> {
            override fun restore(value: String): Char = value[0]

            override fun save(value: Char): String = "$value"
        }
    )

    val DECIMAL_SEPARATOR = stringPreferenceKey(
        name = TAG + "decimal_separator",
        defaultValue = "."
    )

    val NUMBER_OF_DECIMALS = intPreferenceKey(
        name = TAG + "number_of_decimals",
        defaultValue = 5
    )
}

private val DefaultLightColorScheme = lightColors(
    primary = /*Color(0xFF1e9618)*/ /*Color(0xFF0600e9)*/ Color.MetroGreen,
    primaryVariant = Color.MetroGreen.hsl(lightness = 0.95f) /*Color(0xFF006700)*/, //darken a bit
    secondary = Color.Rose,
    secondaryVariant = Color.Rose.hsl(lightness = 0.95f), // darken a bit!!. TODO use relative brightness.
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color.UmbraGrey,
    background = Color(0xFFECF0F3),
    error = Color.OrientRed,
    onBackground = Color.UmbraGrey,
    onError = Color.SignalWhite
)

private val DefaultDarkColorScheme = darkColors(
    primary = Color.Amber,
    primaryVariant = Color.Amber.hsl(lightness = 0.95f),
    secondary = Color.DahliaYellow,
    secondaryVariant = Color.DahliaYellow.hsl(lightness = 0.95f),
    onPrimary = Color.SignalWhite,
    onSecondary = Color.SignalWhite,
    onSurface = Color.SignalWhite,
    background = Color(0xFF0E0E0F), //Color(0xFF0D0D0E)
    surface = Color(0xFF202124), /*Color()*//*Color(0xFF0D0D0E)*/
    error = Color.OrientRed,
    onBackground = Color.SignalWhite,
    onError = Color.SignalWhite,
)


private object ColorSchemeSerializer : KSerializer<Colors> {
    override fun deserialize(decoder: Decoder): Colors {
        return decoder.decodeStructure(descriptor) {
            Colors(
                primary = Color(decodeIntElement(descriptor, 0)),
                primaryVariant = Color(decodeIntElement(descriptor, 1)),
                secondary = Color(decodeIntElement(descriptor, 2)),
                secondaryVariant = Color(decodeIntElement(descriptor, 3)),
                background = Color(decodeIntElement(descriptor, 4)),
                surface = Color(decodeIntElement(descriptor, 5)),
                error = Color(decodeIntElement(descriptor, 6)),
                onPrimary = Color(decodeIntElement(descriptor, 7)),
                onSecondary = Color(decodeIntElement(descriptor, 8)),
                onBackground = Color(decodeIntElement(descriptor, 9)),
                onSurface = Color(decodeIntElement(descriptor, 10)),
                onError = Color(decodeIntElement(descriptor, 11)),
                isLight = decodeBooleanElement(descriptor, 12)
            )
        }
    }

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ColorsSerializerDescriptor") {
            val color = "color"
            for (i in 0 until 11) {
                element<Int>(color + i)
            }
            element<Boolean>("isLight")
        }

    override fun serialize(encoder: Encoder, value: Colors) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.primary.toArgb())
            encodeIntElement(descriptor, 1, value.primaryVariant.toArgb())
            encodeIntElement(descriptor, 2, value.secondary.toArgb())
            encodeIntElement(descriptor, 3, value.secondaryVariant.toArgb())
            encodeIntElement(descriptor, 4, value.background.toArgb())
            encodeIntElement(descriptor, 5, value.surface.toArgb())
            encodeIntElement(descriptor, 6, value.error.toArgb())
            encodeIntElement(descriptor, 7, value.onPrimary.toArgb())
            encodeIntElement(descriptor, 8, value.onSecondary.toArgb())
            encodeIntElement(descriptor, 9, value.onBackground.toArgb())
            encodeIntElement(descriptor, 10, value.onSurface.toArgb())
            encodeIntElement(descriptor, 11, value.onError.toArgb())
            encodeBooleanElement(descriptor, 12, value.isLight)
        }
    }
}