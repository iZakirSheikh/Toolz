package com.prime.toolz.core.converter

import com.prime.toolz.R
import com.prime.toolz.core.math.BoundedRational
import com.prime.toolz.core.math.UnifiedReal
import javax.crypto.Mac

private const val TAG = "Speed"
//Base Unit kmph


@Suppress("FunctionName")
private fun CMsPerSecond() = Unit(
    TAG + "_cms_per_second",
    R.string.system_international,
    R.string.centimetres_per_second,
    R.string.code_centimetres_per_second,
    UnifiedReal(
        BoundedRational(36, 1000)
    )
)

@Suppress("FunctionName")
private fun MsPerSecond() = Unit(
    TAG + "_Ms_per_second",
    R.string.system_international,
    R.string.metres_per_second,
    R.string.code_metres_per_second,
    UnifiedReal(
        BoundedRational(36, 10)
    )
)

@Suppress("FunctionName")
private fun KMsPerHour() = Unit(
    TAG + "_KMs_per_hour",
    R.string.system_international,
    R.string.kilometres_per_hour,
    R.string.code_kilometres_per_hour,
    UnifiedReal(
        BoundedRational(1, 1)
    )
)


@Suppress("FunctionName")
private fun FeetPerSecond() = Unit(
    TAG + "_feet_per_second",
    R.string.imperial_system,
    R.string.feet_per_second,
    R.string.code_feet_per_second,
    UnifiedReal(
        BoundedRational(109728, 100000)
    )
)

@Suppress("FunctionName")
private fun MilesPerHour() = Unit(
    TAG + "_miles_per_hour",
    R.string.imperial_system,
    R.string.miles_per_hour,
    R.string.code_miles_per_hour,
    UnifiedReal(
        BoundedRational(16092, 10000)
    )
)

@Suppress("FunctionName")
private fun Knot() = Unit(
    TAG + "_knot",
    R.string.imperial_system,
    R.string.knot,
    R.string.code_knot,
    UnifiedReal(
        BoundedRational(185184, 100000)
    )
)

@Suppress("FunctionName")
private fun Mach() = Unit(
    TAG + "_mach",
    R.string.imperial_system,
    R.string.mach,
    R.string.code_mach,
    UnifiedReal(
        BoundedRational(122508, 100)
    )
)

@Suppress("FunctionName")
fun Speed() = Converter(
    "converter_$TAG",
    R.string.speed,
    R.drawable.ic_motorcycle,
    arrayOf(
        CMsPerSecond(),
        MsPerSecond(),
        KMsPerHour(),
        FeetPerSecond(),
        MilesPerHour(),
        Knot(),
        Mach()
    )
)