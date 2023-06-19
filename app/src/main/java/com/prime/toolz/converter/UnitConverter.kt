package com.prime.toolz.converter

import android.content.res.Resources
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.ClipboardManager
import com.prime.toolz.core.converter.Converter
import com.prime.toolz.core.converter.Unet
import com.primex.core.Text

/**
 * Interface representing a unit converter.
 *
 * @property converters The array of converters supported by the engine.
 * @property converter The currently selected converter.
 * @property fromUnit The current unit to convert from.
 * @property toUnit The current unit to convert into.
 * @property value The value to be converted.
 * @property result The result of the conversion.
 */

@Stable
interface UnitConverter {

    /**
     * The all of converters supported by [engine]
     */
    val converters: List<Converter>
    var converter: Converter
    var fromUnit: Unet
    var toUnit: Unet
    var value: String
    val result: String
    val more: Map<Text, String>

    /**
     * Swaps the fromUnit and toUnit.
     */
    fun swap()

    /**
     * Clears the value.
     */
    fun clear()

    /**
     * Copies the text.
     *
     * @return The copied text.
     */
    fun Resources.copy(clipboard: ClipboardManager)

    companion object {
        val route = "unit_converter"

        /**
         * Provides the direction for the unit converter route.
         *
         * @return The route for the unit converter.
         */
        fun direction() = route
    }
}