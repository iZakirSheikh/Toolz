package com.prime.toolz2.core.converter

import com.prime.toolz2.R
import com.prime.toolz2.core.math.BoundedRational
import com.prime.toolz2.core.math.UnifiedReal

//Base unit second
private const val TAG = "Time"

@Suppress("FunctionName")
private fun Nanosecond() = Unit(
    TAG + "_nano_second",
    R.string.system_international,
    R.string.nanosecond,
    R.string.code_nanosecond,
    UnifiedReal(
        BoundedRational(1, 1000000000)
    )
)

@Suppress("FunctionName")
private fun Microsecond() = Unit(
    TAG + "_micro_second",
    R.string.system_international,
    R.string.microsecond,
    R.string.code_microsecond,
    UnifiedReal(
        BoundedRational(1, 1000000)
    )
)


@Suppress("FunctionName")
private fun Millisecond() = Unit(
    TAG + "_milli_second",
    R.string.system_international,
    R.string.millisecond,
    R.string.code_millisecond,
    UnifiedReal(
        BoundedRational(1, 1000)
    )
)

@Suppress("FunctionName")
private fun Second() = Unit(
    TAG + "_second",
    R.string.system_international,
    R.string.second,
    R.string.code_second,
    UnifiedReal(
        BoundedRational(1, 1)
    )
)

@Suppress("FunctionName")
private fun Minute() = Unit(
    TAG + "_minute",
    R.string.system_international,
    R.string.minute,
    R.string.code_minute,
    UnifiedReal(
        BoundedRational(60, 1)
    )
)


@Suppress("FunctionName")
private fun Hour() = Unit(
    TAG + "_hour",
    R.string.system_international,
    R.string.hour,
    R.string.code_hour,
    UnifiedReal(
        BoundedRational(3600, 1)
    )
)


@Suppress("FunctionName")
private fun Day() = Unit(
    TAG + "_day",
    R.string.system_international,
    R.string.day,
    R.string.code_day,
    UnifiedReal(
        BoundedRational(86400, 1)
    )
)


@Suppress("FunctionName")
private fun Week() = Unit(
    TAG + "_week",
    R.string.system_international,
    R.string.week,
    R.string.code_week,
    UnifiedReal(
        BoundedRational(604800, 1)
    )
)

@Suppress("FunctionName")
private fun Year() = Unit(
    TAG + "_year",
    R.string.system_international,
    R.string.year,
    R.string.code_year,
    UnifiedReal(
        BoundedRational(31557600, 1)
    )
)

@Suppress("FunctionName")
fun Time() = Converter(
    "converter_$TAG",
    R.string.time,
    R.drawable.ic_time,
    arrayOf(
        Nanosecond(),
        Microsecond(),
        Millisecond(),
        Second(),
        Minute(),
        Hour(),
        Day(),
        Week(),
        Year(),
    )
)