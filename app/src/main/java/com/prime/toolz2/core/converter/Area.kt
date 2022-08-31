package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal

private const val TAG = "Area"

//Base Unit: Square Metre

@Suppress("FunctionName")
private fun SquareMillimetre() = Unit(
    TAG + "_sq_millimetre",
    R.string.system_international,
    R.string.sq_millimetres,
    R.string.code_sq_millimetres,
    UnifiedReal(BoundedRational(1, 1000000))
)

@Suppress("FunctionName")
private fun SquareCentimetre() = Unit(
    TAG + "_sq_centimetre",
    R.string.system_international,
    R.string.sq_centimetres,
    R.string.code_sq_centimetres,
    UnifiedReal(BoundedRational(1, 10000))
)


@Suppress("FunctionName")
private fun SquareMetre() = Unit(
    TAG + "_sq_metre",
    R.string.system_international,
    R.string.sq_metres,
    R.string.code_sq_metres,
    UnifiedReal(BoundedRational(1, 1))
)

@Suppress("FunctionName")
private fun Hectare() = Unit(
    TAG + "_sq_hectare",
    R.string.system_international,
    R.string.hectare,
    R.string.code_hectare,
    UnifiedReal(BoundedRational(10000, 1))
)


@Suppress("FunctionName")
private fun SqKilometre() = Unit(
    TAG + "_sq_kilometre",
    R.string.system_international,
    R.string.sq_kilometre,
    R.string.code_sq_kilometre,
    UnifiedReal(BoundedRational(1000000, 1))
)

@Suppress("FunctionName")
private fun SqInch() = Unit(
    TAG + "_sq_inch",
    R.string.imperial_system,
    R.string.sq_inche,
    R.string.code_sq_inche,
    UnifiedReal(BoundedRational(64516, 100000000))
)

@Suppress("FunctionName")
private fun SqFoot() = Unit(
    TAG + "_sq_foot",
    R.string.imperial_system,
    R.string.sq_foot,
    R.string.code_sq_foot,
    UnifiedReal(BoundedRational(92903, 1000000))
)

@Suppress("FunctionName")
private fun SqYard() = Unit(
    TAG + "_sq_yard",
    R.string.imperial_system,
    R.string.sq_yard,
    R.string.code_sq_yard,
    UnifiedReal(BoundedRational(836127, 1000000))
)

@Suppress("FunctionName")
private fun Acre() = Unit(
    TAG + "_acre",
    R.string.imperial_system,
    R.string.acre,
    R.string.code_acre,
    UnifiedReal(BoundedRational(40468564224L, 10000000L))
)

@Suppress("FunctionName")
private fun SqMile() = Unit(
    TAG + "_sq_mile",
    R.string.imperial_system,
    R.string.sq_mile,
    R.string.code_sq_mile,
    UnifiedReal(BoundedRational(2589988110336L, 1000000))
)

@Suppress("FunctionName")
fun Area() = Converter(
    "converter_$TAG",
    R.string.area,
    R.drawable.ic_area,
    arrayOf(
        SquareMillimetre(),
        SquareCentimetre(),
        SquareMetre(),
        Hectare(),
        SqKilometre(),
        SqInch(),
        SqFoot(),
        SqYard(),
        Acre(),
        SqMile()
    )
)