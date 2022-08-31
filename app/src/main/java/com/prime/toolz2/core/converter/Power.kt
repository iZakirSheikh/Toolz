package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal

private const val TAG = "Power"
//basic unit watt

@Suppress("FunctionName")
private fun Watt() = Unit(
    TAG + "_watt",
    R.string.system_international,
    R.string.watt,
    R.string.code_watt,
    UnifiedReal(BoundedRational(1, 1))
)

@Suppress("FunctionName")
private fun KiloWatt() = Unit(
    TAG + "_kilo_watt",
    R.string.system_international,
    R.string.kilowatt,
    R.string.code_kilowatt,
    UnifiedReal(BoundedRational(1000, 1))
)

@Suppress("FunctionName")
private fun HorsePower() = Unit(
    TAG + "_horse_power",
    R.string.imperial_system_us,
    R.string.horse_power_us,
    R.string.code_horse_power_us,
    UnifiedReal(
        BoundedRational(
            7456998715822702L,
            10000000000000L
        )
    )
)

@Suppress("FunctionName")
private fun FootPoundsPerMinute() = Unit(
    TAG + "_foot_pounds_per_minute",
    R.string.imperial_system,
    R.string.foot_pounds_per_minute,
    R.string.code_foot_pounds_per_minute,
    UnifiedReal(
        BoundedRational(22596966, 1000000000L)
    )
)

@Suppress("FunctionName")
private fun BTUPerMinute() = Unit(
    TAG + "_btu_per_minute",
    R.string.imperial_system,
    R.string.british_thermal_units_per_minute,
    R.string.code_british_thermal_units_per_minute,
    UnifiedReal(
        BoundedRational(175842641667L, 10000000000L)
    )
)

@Suppress("FunctionName")
fun Power() = Converter(
    "converter_$TAG",
    R.string.power,
    R.drawable.ic_power,
    arrayOf(
        Watt(),
        KiloWatt(),
        HorsePower(),
        FootPoundsPerMinute(),
        BTUPerMinute()
    )
)