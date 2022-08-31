package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal

private const val TAG = "Pressure"

//Basic Unit Pascal
@Suppress("FunctionName")
private fun Atmosphere() = Unit(
    TAG + "_atmosphere",
    R.string.system_international,
    R.string.atmosphere,
    R.string.code_atmosphere,
    UnifiedReal(
        BoundedRational(101325, 1)
    )
)

@Suppress("FunctionName")
private fun Bar() = Unit(
    TAG + "_bar",
    R.string.system_international,
    R.string.bar,
    R.string.code_bar,
    UnifiedReal(
        BoundedRational(100000, 1)
    )
)


@Suppress("FunctionName")
private fun KiloPascal() = Unit(
    TAG + "_kilo_pascal",
    R.string.system_international,
    R.string.kilopascal,
    R.string.code_kilopascal,
    UnifiedReal(
        BoundedRational(1000, 1)
    )
)

@Suppress("FunctionName")
private fun PoundsPerInch() = Unit(
    TAG + "_pounds_per_inch",
    R.string.system_international,
    R.string.pounds_per_inch,
    R.string.code_pounds_per_inch,
    UnifiedReal(
        BoundedRational(
            689475729316836L,
            100000000000L
        )
    )
)

@Suppress("FunctionName")
private fun MMsOfMercury() = Unit(
    TAG + "_mms_of_mercury",
    R.string.system_international,
    R.string.mm_of_mercury,
    R.string.code_mm_of_mercury,
    UnifiedReal(
        BoundedRational(133322387415L, 1000000000)
    )
)


@Suppress("FunctionName")
private fun Pascal() = Unit(
    TAG + "_pascal",
    R.string.system_international,
    R.string.pascal,
    R.string.code_pascal,
    UnifiedReal(
        BoundedRational(1, 1)
    )
)

@Suppress("FunctionName")
fun Pressure() = Converter(
    "converter_$TAG",
    R.string.pressure,
    R.drawable.ic_pressure,
    arrayOf(
        Atmosphere(),
        Bar(),
        KiloPascal(),
        PoundsPerInch(),
        MMsOfMercury(),
        Pascal()
    )
)