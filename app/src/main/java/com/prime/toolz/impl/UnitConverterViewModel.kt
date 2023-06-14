package com.prime.toolz.impl

import android.content.ClipboardManager
import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.toolz.converter.UnitConverter
import com.prime.toolz.core.compose.withSpanStyle
import com.prime.toolz.core.converter.Angle
import com.prime.toolz.core.converter.Area
import com.prime.toolz.core.converter.Converter
import com.prime.toolz.core.converter.Energy
import com.prime.toolz.core.converter.Length
import com.prime.toolz.core.converter.Mass
import com.prime.toolz.core.converter.Power
import com.prime.toolz.core.converter.Pressure
import com.prime.toolz.core.converter.Speed
import com.prime.toolz.core.converter.Temperature
import com.prime.toolz.core.converter.Time
import com.prime.toolz.core.converter.Unet
import com.prime.toolz.core.math.NumUtil
import com.prime.toolz.core.math.UnifiedReal
import com.primex.core.Text
import com.primex.core.resolve
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import com.primex.preferences.value
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject


private const val DEBOUNCE_TIMEOUT = 15L

private const val DEFAULT_VALUE = "0"

private const val TAG = "UnitConverterViewModel"

private val KEY_CONVERTER = stringPreferenceKey(TAG + "_converter")
private val KEY_UNIT_FROM = stringPreferenceKey(TAG + "_unit_from")
private val KEY_UNIT_TO = stringPreferenceKey(TAG + "_unit_to")
private val KEY_VALUE = stringPreferenceKey(TAG + "_converter_value")

private const val MAX_ALLOWED_CHARS = 12

@OptIn(FlowPreview::class)
@HiltViewModel
class UnitConverterViewModel @Inject constructor(
    private val preferences: Preferences,
    private val channel: SnackbarHostState
) : ViewModel(), UnitConverter {

    /**
     * The version of this [ViewModel].
     * Update to this triggers calculation.
     */
    private val version = MutableStateFlow(0)
    override val converters: List<Converter> = listOf(
        Length(),
        Mass(),
        Time(),
        Temperature(),
        // Data(),
        Angle(),
        Area(),
        // Volume(),
        Pressure(),
        Energy(),
        Power(),
        Speed()
    )

    // mutable lists.
    private val _converter = mutableStateOf(
        value = with(preferences) {
            // Init
            // If value is saved select it otherwise select the first converter.
            val uuid = value(KEY_CONVERTER) ?: converters[0].uuid
            val selected = converters.find { it.uuid == uuid }!!
            // return the selected converter.
            selected
        }
    )
    private val _fromUnit = mutableStateOf(
        value = with(preferences) {
            // check the saved one; if not select the first unit of converter.
            // TODO: In future select the recommended one.
            val uuid = value(KEY_UNIT_FROM) ?: converter.units[0].uuid
            val units = _converter.value.units

            // obtain selected unit.
            val selected = units.find { it.uuid == uuid }!!
            // return selected unit
            selected
        }
    )
    private val _toUnit = mutableStateOf(
        value = with(preferences) {
            // check the saved one; if not select the 2nd unit of converter.
            // TODO: In future select the recommended one.
            val uuid = value(KEY_UNIT_TO) ?: converter.units[1].uuid
            val units = _converter.value.units
            // obtain selected unit.
            val selected = units.find { it.uuid == uuid }!!
            // return selected unit
            selected
        }
    )

    /**
     * The value to  be converted.
     * The Max length = [MAX_ALLOWED_CHARS]
     */
    private val _value = mutableStateOf(
        value = with(preferences) {
            val text = value(KEY_VALUE) ?: DEFAULT_VALUE
            text
        }
    )
    override var more by mutableStateOf<Map<Text, String>>(emptyMap())

    //
    override var converter: Converter
        get() = _converter.value
        set(value) {
            // update the representation of the converter
            _converter.value = value
            //update uuiD saved
            val uuid = value.uuid
            preferences[KEY_CONVERTER] = uuid
            // as converter changed
            // obviously unit from and to too changed.
            // TODO: In future select the recommended ones not just random.
            fromUnit = value.units[0]
            toUnit = value.units[1]
            // update version to trigger calculation
            version.value += 1
        }

    override var fromUnit: Unet
        get() = _fromUnit.value
        set(value) {
            _fromUnit.value = value
            preferences[KEY_UNIT_FROM] = value.uuid
            // update trigger
            version.value += 1
        }

    override var toUnit: Unet
        get() = _toUnit.value
        set(value) {
            _toUnit.value = value
            preferences[KEY_UNIT_TO] = value.uuid
            // update trigger
            version.value += 1
        }

    override var value: String
        get() = _value.value
        set(value) {
            viewModelScope.launch {
                // preserve default value.
                val modified = when {
                    value.isBlank() -> DEFAULT_VALUE
                    // old is default value
                    value[0] == DEFAULT_VALUE[0] -> value.drop(1)
                    else -> value
                }
                // check for error
                // emit without saving
                val msg = when {
                    modified.length > MAX_ALLOWED_CHARS -> "Max allowed length reached."
                    // if it is not a valid double.
                    modified.toDoubleOrNull() == null -> "Provided input is invalid."
                    else -> null
                }
                // if error
                // emit message and return
                if (msg != null) {
                    channel.showSnackbar(message = msg)
                    return@launch
                }

                // emit value.
                _value.value = modified
                // update trigger
                version.value += 1
                //safe in prefs.
                preferences[KEY_VALUE] = modified
            }
        }

    override var result: String by mutableStateOf(DEFAULT_VALUE)

    override fun swap() {
        // from as to
        val from = _toUnit.value
        val to = _fromUnit.value

        fromUnit = from
        toUnit = to
        viewModelScope.launch {
            channel.showSnackbar(message = "Units Swapped!")
        }
    }

    override fun clear() {
        value = ""
        viewModelScope.launch {
            channel.showSnackbar(message = "Input cleared")
        }
    }

    override fun Resources.copy(clipboard: androidx.compose.ui.platform.ClipboardManager) {
        viewModelScope.launch {
            val resources = this@copy
            val text = buildAnnotatedString {
                val from = resources.resolve(fromUnit.code)
                val to = resources.resolve(toUnit.code)
                append(value)
                append(" ")
                withSpanStyle(fontStyle = FontStyle.Italic) {
                    append(from)
                }
                append(" = ")
                append(result)
                withSpanStyle(fontStyle = FontStyle.Italic) {
                    append(to)
                }
            }
            clipboard.setText(text)
            channel.showSnackbar(
                message = "Copied $text into Clipboard!!."
            )
        }
    }

    private val formatter = DecimalFormat("###,###.##")

    private val onUpdate: suspend (Int) -> Unit = { _ ->
        // Triggered when new update happens.
        // Obtain the instance of the currently used converter.
        val converter = converter
        // Obtain the instance of the units being converted.
        val from = fromUnit
        val to = toUnit
        // Obtain the value as UnifiedReal; if error happens in parsing; the catch block will possible
        // catch it and app will remain intact.
        val real = UnifiedReal(value)
        val res = converter.convert(from, to, real).doubleValue()
        // Publish the result
        result = NumUtil.doubleToString(res, 12, 2)!!
        // Compute in terms of others.
        // This might take time,
        // Use helper threads if possible.
        val map = HashMap<Unet, UnifiedReal>()
        val units = converter.units
        // the unified real form of the limit
        val limit = UnifiedReal("0.1")
        units.forEach { unit ->
            if (unit != to && unit != from) {
                val result = converter.convert(from, unit, real)

                if (result < limit)
                    return@forEach // continue.
                // only add then to map
                //if (value.compareTo(value))
                map += unit to result
            }
        }
        //Publish the result.
        // But sort in descending order first.
        //return the computed result
        // TODO: Sort in ascending order of the entries.
        // this hack currently works but needs some elegent solution.
        more =
            map.toSortedMap { o1, o2 ->
                val real1 = map[o1]!!
                val real2 = map[o2]!!
                real1.compareTo(real2)
            }.mapKeys { it.key.code }.mapValues { formatter.format(it.value.doubleValue()) }
    }

    init {
        version
            .debounce(DEBOUNCE_TIMEOUT)
            .onEach(onUpdate).catch {
                // FixMe find suitable method to emit the errors.
                channel.showSnackbar(message = "Oops!! An Unknown error occurred.\n\n ${it.message}")
            }.launchIn(viewModelScope)
    }
}