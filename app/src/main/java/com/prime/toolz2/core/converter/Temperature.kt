package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal


private const val TAG = "Temperature"

@Suppress("FunctionName")
private fun Celsius() = object : Unet {
    override val title: Int = R.string.celsius
    override val code: Int = R.string.code_celsius
    override val uuid: String = TAG + "_celsius"
    override val group: Int = R.string.system_international

    override suspend fun toBase(value: UnifiedReal): UnifiedReal = value

    override suspend fun toUnit(value: UnifiedReal): UnifiedReal = value
}


@Suppress("FunctionName")
private fun Fahrenheit() = object : Unet {
    override val title: Int = R.string.fahrenheit
    override val code: Int = R.string.code_fahrenheit
    override val uuid: String = TAG + "_fahrenheit"
    override val group: Int = R.string.united_system_customary_system

    override suspend fun toBase(value: UnifiedReal): UnifiedReal =
        value.subtract(UnifiedReal(32)).multiply(UnifiedReal(BoundedRational(5, 9)))

    override suspend fun toUnit(value: UnifiedReal): UnifiedReal =
        value.multiply(UnifiedReal(BoundedRational(9, 5))).add(
            UnifiedReal(32)
        )
}

@Suppress("FunctionName")
private fun Kelvin() = object : Unet {
    override val title: Int = R.string.kelvin
    override val code: Int = R.string.code_kelvin
    override val uuid: String = TAG + "_kelvin"
    override val group: Int = R.string.system_international

    override suspend fun toBase(value: UnifiedReal) =
        value.subtract(UnifiedReal(BoundedRational(27315, 100)))

    override suspend fun toUnit(value: UnifiedReal) =
        value.add(UnifiedReal(BoundedRational(27315, 100)))
}

@Suppress("FunctionName")
private fun Rankine() = object : Unet {
    override val title: Int = R.string.rankine
    override val code: Int = R.string.code_rankine
    override val uuid: String = TAG + "_rankine"
    override val group: Int = R.string.imperial_system_us

    override suspend fun toBase(value: UnifiedReal) =
        value.subtract(UnifiedReal(BoundedRational(49167, 100)))
            .multiply(UnifiedReal(BoundedRational(5, 9)))

    override suspend fun toUnit(value: UnifiedReal) =
        value.add(UnifiedReal(BoundedRational(27315, 100)))
            .multiply(UnifiedReal(BoundedRational(9, 5)))
}


@Suppress("FunctionName")
private fun Delisle() = object : Unet {
    override val title: Int = R.string.delisle
    override val code: Int = R.string.code_delisle
    override val uuid: String = TAG + "_delisle"
    override val group: Int = R.string.unknown

    override suspend fun toBase(value: UnifiedReal) =
        UnifiedReal(100).subtract(value.multiply(UnifiedReal(BoundedRational(2, 3))))

    override suspend fun toUnit(value: UnifiedReal) = UnifiedReal(100).subtract(value)
        .multiply(UnifiedReal(BoundedRational(15, 10)))
}

@Suppress("FunctionName")
private fun Newton() = object : Unet {
    override val title: Int = R.string.newton
    override val code: Int = R.string.code_newton
    override val uuid: String = TAG + "_newton"
    override val group: Int = R.string.unknown

    override suspend fun toBase(value: UnifiedReal) =
        value.multiply(UnifiedReal(BoundedRational(100, 33)))

    override suspend fun toUnit(value: UnifiedReal) =
        value.multiply(UnifiedReal(BoundedRational(33, 100)))
}


@Suppress("FunctionName")
private fun Reaumur() = object : Unet {
    override val title: Int = R.string.reaumur
    override val code: Int = R.string.code_reaumur
    override val uuid: String = TAG + "_reaumur"
    override val group: Int = R.string.unknown

    override suspend fun toBase(value: UnifiedReal) =
        value.multiply(UnifiedReal(BoundedRational(5, 4)))

    override suspend fun toUnit(value: UnifiedReal) =
        value.multiply(UnifiedReal(BoundedRational(4, 5)))
}


@Suppress("FunctionName")
private fun Romer() = object : Unet {
    override val title: Int = R.string.romer
    override val code: Int = R.string.code_romer
    override val uuid: String = TAG + "_romer"
    override val group: Int = R.string.unknown

    override suspend fun toBase(value: UnifiedReal) =
        value.subtract(UnifiedReal(BoundedRational(75, 10)))
            .multiply(UnifiedReal(BoundedRational(40, 21)))

    override suspend fun toUnit(value: UnifiedReal) =
        value.multiply(UnifiedReal(BoundedRational(21, 40))).add(
            UnifiedReal(
                BoundedRational(
                    75,
                    10
                )
            )
        )
}

@Suppress("FunctionName")
fun Temperature() = Converter(
    uuid = "converter_$TAG",
    title = R.string.temperature,
    drawableRes = R.drawable.ic_temperature,
    units = arrayOf(Celsius(), Fahrenheit(), Kelvin(), Rankine(), Newton(), Romer(), Reaumur())
)