package com.prime.toolz2.ui.converter

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.toolz2.common.compose.SnackDataChannel
import com.prime.toolz2.common.compose.send
import com.prime.toolz2.core.converter.UnitConverter
import com.prime.toolz2.core.math.NumUtil
import com.prime.toolz2.core.math.UnifiedReal
import com.primex.core.Text
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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
) : ViewModel() {

    /**
     * The version of this [ViewModel].
     * Update to this triggers calculation.
     */
    private val version = MutableStateFlow(0)

    /**
     * The messenger used to show messages on the UI.
     */
    @JvmField
    var channel: SnackDataChannel? = null

    /**
     * The unit converter.
     */
    private val engine = UnitConverter()

    /**
     * The all of converters supported by [engine]
     */
    val converters = engine.converters

    /**
     * The converter.
     */
    private val _converter = mutableStateOf(
        with(preferences) {
            // some required blocking calls
            val uuid = get(KEY_CONVERTER).obtain() ?: engine.converter.uuid
            val selected = converters.find { it.uuid == uuid }!!

            // set converter to engine
            engine.converter = selected

            // return the selected converter.
            selected
        }
    )
    var converter
        get() = _converter.value
        set(value) {
            // update the representation of the converter
            _converter.value = value
            // update the converter of the engine.
            engine.converter = value
            //update uuiD saved
            val uuid = value.uuid
            preferences[KEY_CONVERTER] = uuid
            // as converter changed
            // obviously unit from and to too changed.
            fromUnit = engine.from
            toUnit = engine.to

            // update version to trigger calculation
            version.value += 1
        }

    /**
     * The unit from.
     */
    private val _fromUnit = mutableStateOf(
        with(preferences) {
            val uuid = get(KEY_UNIT_FROM).obtain() ?: engine.from.uuid
            val units = _converter.value.units

            // obtain selected unit.
            val selected = units.find { it.uuid == uuid }!!

            // set selected from unit to engine
            engine.from = selected

            // return selected unit
            selected
        }
    )

    var fromUnit
        get() = _fromUnit.value
        set(value) {
            viewModelScope.launch {
                // units of the converter
                engine.from = value
                _fromUnit.value = value
                preferences[KEY_UNIT_FROM] = value.uuid
                // update trigger
                version.value += 1
            }
        }

    /**
     * To Unit
     */
    private val _toUnit = mutableStateOf(
        with(preferences) {
            val uuid = get(KEY_UNIT_TO).obtain() ?: engine.to.uuid
            val units = _converter.value.units

            // obtain selected unit.
            val selected = units.find { it.uuid == uuid }!!

            // set selected to unit to engine
            engine.to = selected

            // return selected unit
            selected
        }
    )

    var toUnit
        get() = _toUnit.value
        set(value) {
            viewModelScope.launch {
                // units of the converter
                engine.to = value
                _toUnit.value = value
                preferences[KEY_UNIT_TO] = value.uuid
                // update trigger
                version.value += 1
            }
        }

    /**
     * The value to  be converted.
     * The Max length = [MAX_ALLOWED_CHARS]
     */
    private val _value = mutableStateOf(
        with(preferences) {
            val text = get(KEY_VALUE).obtain() ?: DEFAULT_VALUE
            text
        }
    )
    var value
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
                val msg =
                    when {
                        modified.length > MAX_ALLOWED_CHARS -> "Max allowed length reached."
                        // if it is not a valid double.
                        modified.toDoubleOrNull() == null -> "Provided input is invalid."
                        else -> null
                    }

                // if error
                // emit message and return
                if (msg != null) {
                    channel?.send(message = msg)
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

    /**
     * The computed result.
     */
    private val _result = mutableStateOf(DEFAULT_VALUE)
    var result
        get() = _result.value
        private set(value) {
            _result.value = value
        }

    /**
     * The value of converter in terms of other units.
     */
    private val _more = mutableStateOf<Map<Text, String>>(emptyMap())
    var more
        get() = _more.value
        private set(value) {
            _more.value = value
        }

    /**
     * A convince method to swap the values of the [fromUnit] and [toUnit]
     */
    fun swap() {
        // from as to
        val from = _toUnit.value
        val to = _fromUnit.value

        fromUnit = from
        toUnit = to
        viewModelScope.launch {
            channel?.send(message = "Units Swapped!")
        }
    }

    fun clear() {
        value = DEFAULT_VALUE
        viewModelScope.launch {
            channel?.send(message = "Input cleared")
        }
    }

    private val formatter = DecimalFormat("###,###.##")

    init {
        version
            .debounce(DEBOUNCE_TIMEOUT)
            .onEach { _ ->
                // set value as unified
                // TODO: Find which errors might occur
                // catch the errors.
                // find solution to report to the user.
                val value = _value.value
                engine.value = UnifiedReal(value)

                // compute and emit result.
                val double = engine.convert().doubleValue()
                result = NumUtil.doubleToString(double, 12, 2)!!

                // compute in terms of others.
                // this might take time,
                // use helper threads if possible.
                more = engine.mapped(0.1f)
                    .mapKeys { it.key.code }
                    .mapValues { formatter.format(it.value.doubleValue()) }
            }
            .catch {
                // FixMe find suitable method to emit the errors.
                channel?.send(message = "Oops!! An Unknown error occurred.")
            }
            .launchIn(viewModelScope)
    }
}