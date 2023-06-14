package com.prime.toolz.core.converter

import com.prime.toolz.R
import com.prime.toolz.core.math.BoundedRational
import com.prime.toolz.core.math.UnifiedReal
import java.math.BigInteger


private const val TAG = "Energy"

//base unit joule


@Suppress("FunctionName")
private fun ElectronVolt() = Unit(
    TAG + "_electron_volt",
    R.string.system_international,
    R.string.electron_volt,
    R.string.code_electron_volt,
    UnifiedReal(
        BoundedRational(
            BigInteger("1602176565"), BigInteger("10000000000000000000000000000")
        )
    )
)

@Suppress("FunctionName")
private fun Joule() = Unit(
    TAG + "_joule",
    R.string.system_international,
    R.string.joule,
    R.string.code_joule,
    UnifiedReal(BoundedRational(1, 1))
)

@Suppress("FunctionName")
private fun KiloJoule() = Unit(
    TAG + "_kilo_joule",
    R.string.system_international,
    R.string.kilojoule,
    R.string.code_kilojoule,
    UnifiedReal(BoundedRational(1000, 1))
)

@Suppress("FunctionName")
private fun ThermalCalorie() = Unit(
    TAG + "_thermal_calorie",
    R.string.system_international,
    R.string.thermal_calorie,
    R.string.code_thermal_calorie,
    UnifiedReal(BoundedRational(4184, 1000))
)

@Suppress("FunctionName")
private fun FoodCalorie() = Unit(
    TAG + "_food_calorie",
    R.string.system_international,
    R.string.food_calorie,
    R.string.code_food_calorie,
    UnifiedReal(BoundedRational(4184, 1))
)

@Suppress("FunctionName")
private fun FootPound() = Unit(
    TAG + "_foot_pound",
    R.string.imperial_system,
    R.string.foot_pound,
    R.string.code_foot_pound,
    UnifiedReal(
        BoundedRational(13558179483314003L, 10000000000000000L)
    )
)


@Suppress("FunctionName")
fun Energy() = Converter(
    "converter_$TAG",
    R.string.energy,
    R.drawable.ic_energy,
    arrayOf(
        ElectronVolt(),
        Joule(),
        KiloJoule(),
        ThermalCalorie(),
        FoodCalorie(),
        FootPound()
    )
)