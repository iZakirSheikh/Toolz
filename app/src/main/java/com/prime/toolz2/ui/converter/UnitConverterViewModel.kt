package com.prime.toolz2.ui.converter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.toolz2.common.compose.SnackDataChannel
import com.prime.toolz2.common.compose.send
import com.prime.toolz2.core.converter.Converter
import com.prime.toolz2.core.converter.Unet
import com.prime.toolz2.core.converter.UnitConverter
import com.prime.toolz2.core.math.UnifiedReal
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
     * The value to  be converted.
     * The Max length = [MAX_ALLOWED_CHARS]
     */
    private val _value = mutableStateOf(
        with(preferences) {
            val text = get(KEY_VALUE).obtain() ?: DEFAULT_VALUE
            text
        }
    )
    val value: State<String> = _value

    /**
     * The computed result.
     */
    private val _result = mutableStateOf(UnifiedReal.ZERO, neverEqualPolicy())
    val result: State<UnifiedReal> = _result

    /**
     * The value of converter in terms of other units.
     */
    private val _more = mutableStateOf<Map<Unet, UnifiedReal>>(emptyMap(), neverEqualPolicy())
    val more: State<Map<Unet, UnifiedReal>> = _more

    /**
     * The trigger triggers the calculation.
     */
    private val trigger = MutableStateFlow(0)

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
    val converter: State<Converter> = _converter

    /**
     * Unit from
     */
    private val _fromUnit = mutableStateOf(
        with(preferences) {
            val uuid = get(KEY_UNIT_FROM).obtain() ?: engine.from.uuid
            val units = converter.value.units

            // obtain selected unit.
            val selected = units.find { it.uuid == uuid }!!

            // set selected from unit to engine
            engine.from = selected

            // return selected unit
            selected
        }
    )
    val fromUnit: State<Unet> = _fromUnit

    /**
     * To Unit
     */
    private val _toUnit = mutableStateOf(
        with(preferences) {
            val uuid = get(KEY_UNIT_TO).obtain() ?: engine.to.uuid
            val units = converter.value.units

            // obtain selected unit.
            val selected = units.find { it.uuid == uuid }!!

            // set selected to unit to engine
            engine.to = selected

            // return selected unit
            selected
        }
    )
    val toUnit: State<Unet> = _toUnit

    fun converter(value: Converter) {
        viewModelScope.launch {
            // set the converter.
            engine.converter = value
            // update converter.
            // so to update the UI
            _converter.value = value

            //update uuiD saved
            val uuid = value.uuid
            preferences[KEY_CONVERTER] = uuid

            // change units
            from(engine.from)
            toUnit(engine.to)

            // update trigger
            trigger.value += 1
            /*channel?.send(
                message = "Changing Converter."
            )*/
        }
    }

    fun from(value: Unet) {
        viewModelScope.launch {
            // units of the converter
            engine.from = value
            _fromUnit.value = value
            preferences[KEY_UNIT_FROM] = value.uuid
            // update trigger
            trigger.value += 1
        }
    }

    fun toUnit(value: Unet) {
        viewModelScope.launch {
            // units of the converter
            engine.to = value
            _toUnit.value = value
            preferences[KEY_UNIT_TO] = value.uuid
            // update trigger
            trigger.value += 1
        }
    }

    fun append(char: Char){
        viewModelScope.launch {
            val old = value.value
            val new = if (old == DEFAULT_VALUE) "$char" else "$old$char"
            value(new)
        }
    }

    fun value(new: String) {
        viewModelScope.launch {
            val msg = when {
                new.length > MAX_ALLOWED_CHARS -> "Max allowed digits reached."
                // if it is not a valid double.
                new.toDoubleOrNull() == null -> "Provided input is invalid."
                else -> null
            }

            if (msg != null) {
                channel?.send(message = msg)
                return@launch
            }

            // emit value.
            _value.value = new
            // update trigger
            trigger.value += 1
            preferences[KEY_VALUE] = new
        }
    }

    fun swap() {
        val from = toUnit.value
        val to = fromUnit.value
        // from as to
        from(from)
        toUnit(to)
        viewModelScope.launch {
            channel?.send(message = "Units Swapped!")
        }
    }

    fun clear() {
        value(DEFAULT_VALUE)
        viewModelScope.launch {
            channel?.send(message = "Input cleared")
        }
    }


    fun backspace() {
        val old = value.value
        value(
            if (old.length == 1)
                DEFAULT_VALUE
            else
                old.dropLast(1)
        )
    }

    init {
        trigger
            .debounce(DEBOUNCE_TIMEOUT)
            .onEach { _ ->
                // set value as unified
                // TODO: Find which errors might occur
                // catch the errors.
                // find solution to report to the user.
                val value = _value.value
                engine.value = UnifiedReal(value)

                // compute and emit result.
                val result = engine.convert()
                _result.value = result

                // compute in terms of others.
                // this might take time,
                // use helper threads if possible.
                val mapped = engine.mapped(0.1f)
                _more.value = mapped
            }
            .catch {
                channel?.send(message = "Oops!! An Unknown error occurred.")
            }
            .launchIn(viewModelScope)
    }
}