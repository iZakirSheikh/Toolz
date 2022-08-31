package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal

private const val TAG = "Length"

@Suppress("FunctionName")
private fun Nanometre() = Unit(
    TAG + "_nanometre",
    R.string.system_international,
    R.string.nanometre,
    R.string.code_nanometre,
    UnifiedReal(BoundedRational(1, 1000000000))
)

@Suppress("FunctionName")
private fun Micrometre() = Unit(
    TAG + "_micrometre",
    R.string.system_international,
    R.string.micrometre,
    R.string.code_micrometre,
    UnifiedReal(BoundedRational(1, 1000000))
)

@Suppress("FunctionName")
private fun Millimetre() = Unit(
    TAG + "_millimetre",
    R.string.system_international,
    R.string.millimetre,
    R.string.code_millimetre,
    UnifiedReal(BoundedRational(1, 1000))
)

@Suppress("FunctionName")
private fun Centimetre() = Unit(
    TAG + "_centimetre",
    R.string.system_international,
    R.string.centimetre,
    R.string.code_centimetre,
    UnifiedReal(BoundedRational(1, 100))
)

@Suppress("FunctionName")
private fun Metre() = Unit(
    TAG + "_metre",
    R.string.system_international,
    R.string.metre,
    R.string.code_metre,
    UnifiedReal(1)
)

@Suppress("FunctionName")
private fun Kilometre() = Unit(
    TAG + "_kilometre",
    R.string.system_international,
    R.string.kilometre,
    R.string.code_kilometre,
    UnifiedReal(BoundedRational(1000, 1))
)

@Suppress("FunctionName")
private fun Mile() = Unit(
    TAG + "_mile",
    R.string.imperial_system,
    R.string.mile,
    R.string.code_mile,
    UnifiedReal(BoundedRational(1609344, 1000))
)

@Suppress("FunctionName")
private fun NauticalMile() = Unit(
    TAG + "_nautical_mile",
    R.string.imperial_system,
    R.string.nautical_mile,
    R.string.code_nautical_mile,
    UnifiedReal(BoundedRational(1852, 1))
)

@Suppress("FunctionName")
private fun Yard() = Unit(
    TAG + "_yard",
    R.string.imperial_system,
    R.string.yard,
    R.string.code_yard,
    UnifiedReal(BoundedRational(9144, 10000))
)

@Suppress("FunctionName")
private fun Foot() = Unit(
    TAG + "_foot",
    R.string.imperial_system,
    R.string.foot,
    R.string.code_foot,
    UnifiedReal(BoundedRational(3048, 10000))
)

@Suppress("FunctionName")
private fun Inch() = Unit(
    TAG + "_inch",
    R.string.imperial_system,
    R.string.inch,
    R.string.code_inch,
    UnifiedReal(BoundedRational(254, 10000))
)


@Suppress("FunctionName")
private fun Au() = Unit(
    TAG + "_astronomical_unit",
    R.string.system_international,
    R.string.astronomical_unt,
    R.string.code_astronomical_unit,
    UnifiedReal(BoundedRational(149597870700L, 1))
)


@Suppress("FunctionName")
private fun LightYear() = Unit(
    TAG + "_light_year",
    R.string.system_international,
    R.string.light_year,
    R.string.code_light_year,
    UnifiedReal(BoundedRational(9460730472580800L, 1))
)

@Suppress("FunctionName")
fun Length() = Converter(
    uuid = "converter_$TAG",
    title = R.string.length,
    drawableRes = R.drawable.ic_length,
    units = arrayOf(
        Nanometre(),
        Micrometre(),
        Millimetre(),
        Centimetre(),
        Metre(),
        Kilometre(),
        Mile(),
        NauticalMile(),
        Yard(),
        Foot(),
        Inch(),
        Au(),
        LightYear()
    )
)