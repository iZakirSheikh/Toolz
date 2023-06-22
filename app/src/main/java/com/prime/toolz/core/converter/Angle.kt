package com.prime.toolz.core.converter

import com.prime.toolz.R
import com.prime.toolz.core.math.BoundedRational
import com.prime.toolz.core.math.UnifiedReal

private const val TAG = "Angle"

@Suppress("FunctionName")
private fun Degree() = Unit(
    TAG + "_degree",
    R.string.system_international,
    R.string.degrees,
    R.string.code_degrees,
    UnifiedReal(BoundedRational(1, 1))
)

@Suppress("FunctionName")
private fun Radian() = Unit(
    TAG + "_radian",
    R.string.system_international,
    R.string.radian,
    R.string.code_radian,
    UnifiedReal(BoundedRational(572957795130823L, 10000000000000L))
)

@Suppress("FunctionName")
private fun Gradian() = Unit(
    TAG + "_gradian",
    R.string.system_international,
    R.string.gradian,
    R.string.code_gradian,
    UnifiedReal(BoundedRational(9, 10))
)

@Suppress("FunctionName")
fun Angle() = Converter(
    "converter_$TAG", R.string.angle, R.drawable.ic_angle, arrayOf(
        Degree(), Radian(), Gradian()
    )
)

