package com.prime.toolz2.core.converter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.prime.toolz2.core.math.UnifiedReal


//TODO: Replace string resources plurals in futuure versions.

interface Unet {
    /**
     * The title of the Unit.
     */
    val title: Int

    /**
     * The short name of the Unit
     */
    val code: Int

    /**
     * The unique Id of this unit in the converter.
     */
    val uuid: String

    /**
     * The name resource of the group to which this unit belongs; e.g., SI, BTS etc.
     */
    val group: Int

    /**
     * Consumes value in Unit and returns value in [base] unit
     * @param value the value in Unit
     */
    suspend fun toBase(value: UnifiedReal): UnifiedReal

    /**
     * Consumes value in base and returns value in [Unet]
     *
     *@param value: The value in base.
     */
    suspend fun toUnit(value: UnifiedReal): UnifiedReal
}

@Suppress("FunctionName")
fun Unit(
    uuid: String,
    @StringRes group: Int,
    @StringRes title: Int,
    @StringRes code: Int,
    inBase: UnifiedReal
) = object : Unet {
    override val title: Int = title
    override val code: Int = code
    override val uuid: String = uuid
    override val group: Int = group

    override suspend fun toBase(value: UnifiedReal): UnifiedReal {
        return value.multiply(inBase)
    }

    override suspend fun toUnit(value: UnifiedReal): UnifiedReal {
        return value.divide(inBase)
    }
}

interface Converter {

    /**
     * The title of the Unit.
     */
    val title: Int

    /**
     * The resource icon associated with this converter.
     */
    val drawableRes: Int

    /**
     * The unique Id to identify this converter.
     */
    val uuid: String

    /**
     * The list of units supported by this converter.
     */
    val units: Array<Unet>

    /**
     * The method to convert [value]from -> to
     */
    suspend fun convert(from: Unet, to: Unet, value: UnifiedReal): UnifiedReal {
        val inBase = from.toBase(value)
        return to.toUnit(inBase)
    }
}

@Suppress("FunctionName")
fun Converter(
    uuid: String,
    @StringRes title: Int,
    @DrawableRes drawableRes: Int,
    units: Array<Unet>,
) = object : Converter {
    override val title: Int = title
    override val drawableRes: Int = drawableRes
    override val uuid: String = uuid
    override val units: Array<Unet> = units
}


interface UnitConverter {

    var value: UnifiedReal

    var converter: Converter

    var from: Unet

    var to: Unet

    suspend fun convert(): UnifiedReal = converter.convert(from, to, value)

    /**
     * @param pct: A value between 0 and 1
     */
    suspend fun mapped(pct: Float): Map<Unet, UnifiedReal> {
        val map = HashMap<Unet, UnifiedReal>()

        val units = converter.units
        // the pct must between 0 and one
        // throw error if not
        require(pct in 0.0..1.0)

        // the unified real form of the limit
        val limit = UnifiedReal("$pct")

        units.forEach { unit ->
            if (unit != to && unit != from) {
                val result = converter.convert(from, unit, value)

                if (result < limit)
                    return@forEach // continue.
                // only add then to map
                //if (value.compareTo(value))
                map += unit to result
            }
        }
        //return the computed result
        // TODO: Sort in ascending order of the entries.
        // this hack currently works but needs some elegent solution.
        return map.toSortedMap { o1, o2 ->
            val real1 = map[o1]!!
            val real2 = map[o2]!!
            real1.compareTo(real2)
        }
    }

    val converters: Array<Converter>

    /**
     * Returns the default suggested units indices of the [converter]
     */
    val default: Pair<Int, Int>
}


private class UnitConverterImpl : UnitConverter {

    override val converters: Array<Converter> =
        arrayOf(
            Length(),
            Mass(),
            Time(),
            Temperature(),
            // Data(),
            Angle(),
            Area(),
            // Volume(),
            Pressure(),
            Energy(),
            Power(),
            Speed()
        )


    override var value: UnifiedReal = UnifiedReal.ZERO

    override var converter: Converter = converters[0] // length
        set(value) {
            field = value
            val units = field.units
            // whenever converter changes switch to defaults.
            val def = default
            // of the unit.
            from = units[def.first]; to = units[def.second]
        }

    override var from: Unet = converter.units[0]
    override var to: Unet = converter.units[1]

    override val default: Pair<Int, Int>
        get() = when (converter) {
            converters[0] -> 4 to 10 //metre to inch
            else -> 0 to 1 //TODO: Implement properly this thing.
        }

}

fun UnitConverter(): UnitConverter = UnitConverterImpl()