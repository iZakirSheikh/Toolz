package com.prime.toolz.core.converter

import com.prime.toolz.R

private const val TAG = "Volume"

@Suppress("FunctionName")
fun Volume() = Converter(
    "converter_$TAG",
    R.string.volume,
    R.drawable.ic_volume,
    arrayOf()
)