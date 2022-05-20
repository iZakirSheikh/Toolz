package com.prime.toolz2.ui.converter



import android.util.Log
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

}