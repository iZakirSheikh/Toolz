package com.prime.toolz.core.converter


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.prime.toolz.core.math.UnifiedReal
import com.primex.core.Text


//TODO: Replace string resources plurals in futuure versions.
interface Unet {
    /**
     * The unique Id of this unit in the converter.
     */
    val uuid: String

    /**
     * The title of the Unit.
     */
    val title: Text

    /**
     * The short name of the Unit
     */
    val code: Text

    /**
     * The name resource of the group to which this unit belongs; e.g., SI, BTS etc.
     */
    val group: Text

    /**
     * The optional iconRes [DrawableRes] of the unit
     */
    val icon: Int?
        get() = null

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


interface Converter {

    /**
     * The title of the Unit.
     */
    val title: Text

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
    override val title: Text = Text(title)
    override val drawableRes: Int = drawableRes
    override val uuid: String = uuid
    override val units: Array<Unet> = units
}

/**
 * Constructs the Unit for the Converter
 */
@Suppress("FunctionName")
fun Unit(
    uuid: String,
    @StringRes group: Int,
    @StringRes title: Int,
    @StringRes code: Int,
    inBase: UnifiedReal,
    @DrawableRes icon: Int? = null
) =
    object : Unet {
        override val uuid: String = uuid
        override val title: Text = Text(title)
        override val code: Text = Text(code)
        override val group: Text = Text(group)
        override val icon: Int? = icon

        override suspend fun toBase(value: UnifiedReal): UnifiedReal {
            return value.multiply(inBase)
        }

        override suspend fun toUnit(value: UnifiedReal): UnifiedReal {
            return value.divide(inBase)
        }
    }
