package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal

private const val TAG = "Mass"

//Base unit - Kilograms

@Suppress("FunctionName")
private fun Carat() = Unit(
    TAG + "_carat",
    R.string.system_international,
    R.string.carat,
    R.string.code_carat,
    UnifiedReal(BoundedRational(2, 10000))
)

@Suppress("FunctionName")
private fun MilliGram() = Unit(
    TAG + "_milli_gram",
    R.string.system_international,
    R.string.milligram,
    R.string.code_milligram,
    UnifiedReal(BoundedRational(1, 1000000))
)

@Suppress("FunctionName")
private fun Centigram() = Unit(
    TAG + "_centi_gram",
    R.string.system_international,
    R.string.centigram,
    R.string.code_centigram,
    UnifiedReal(BoundedRational(1, 100000))
)

@Suppress("FunctionName")
private fun Decigram() = Unit(
    TAG + "_decigram",
    R.string.system_international,
    R.string.decigram,
    R.string.code_decigram,
    UnifiedReal(BoundedRational(1, 10000))
)


@Suppress("FunctionName")
private fun Gram() = Unit(
    TAG + "_gram",
    R.string.system_international,
    R.string.gram,
    R.string.code_gram,
    UnifiedReal(BoundedRational(1, 1000))
)

@Suppress("FunctionName")
private fun Decagram() = Unit(
    TAG + "_deca_gram",
    R.string.system_international,
    R.string.decagram,
    R.string.code_decagram,
    UnifiedReal(BoundedRational(1, 100))
)

@Suppress("FunctionName")
private fun Hectogram() = Unit(
    TAG + "_hectogram",
    R.string.system_international,
    R.string.hectogram,
    R.string.code_hectogram,
    UnifiedReal(BoundedRational(1, 10))
)

@Suppress("FunctionName")
private fun Kilogram() = Unit(
    TAG + "_kilo_gram",
    R.string.system_international,
    R.string.kilogram,
    R.string.code_kilogram,
    UnifiedReal(BoundedRational(1, 1))
)

@Suppress("FunctionName")
private fun MetricTonne() = Unit(
    TAG + "_metric_tonne",
    R.string.system_international,
    R.string.metric_ton,
    R.string.code_metric_ton,
    UnifiedReal(BoundedRational(1000, 1))
)

@Suppress("FunctionName")
private fun Ounce() = Unit(
    TAG + "_ounce",
    R.string.imperial_system,
    R.string.ounce,
    R.string.code_ounce,
    UnifiedReal(BoundedRational(28349523125L, 1000000000000L))
)

@Suppress("FunctionName")
private fun Pound() = Unit(
    TAG + "_pound",
    R.string.imperial_system,
    R.string.pound,
    R.string.code_pound,
    UnifiedReal(BoundedRational(45359237, 100000000))
)

@Suppress("FunctionName")
private fun Stone() = Unit(
    TAG + "_stone",
    R.string.imperial_system,
    R.string.stone,
    R.string.code_stone,
    UnifiedReal(BoundedRational(635029318, 100000000))
)

@Suppress("FunctionName")
private fun ShortTonneUS() = Unit(
    TAG + "_short_tonne_us",
    R.string.imperial_system_us,
    R.string.short_ton,
    R.string.code_short_ton,
    UnifiedReal(BoundedRational(90718474, 100000))
)

@Suppress("FunctionName")
private fun LongTonneUK() = Unit(
    TAG + "_long_tonne_uk",
    R.string.imperial_system,
    R.string.long_ton,
    R.string.code_long_ton,
    UnifiedReal(BoundedRational(10160469088L, 10000000))
)

@Suppress("FunctionName")
fun Mass() = Converter(
    "converter_$TAG",
    R.string.weight_and_mass,
    R.drawable.ic_weight_n_mass,
    arrayOf(
        Carat(),
        MilliGram(),
        Centigram(),
        Decigram(),
        Gram(),
        Decagram(),
        Hectogram(),
        Kilogram(),
        MetricTonne(),
        Ounce(),
        Stone(),
        ShortTonneUS(),
        LongTonneUK()
    )
)