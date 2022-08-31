package com.prime.toolz2.core.converter

import com.prime.toolz2.R

private const val TAG = "Volume"

@Suppress("FunctionName")
fun Volume() = Converter(
    "converter_$TAG",
    R.string.volume,
    R.drawable.ic_volume,
    arrayOf()
)