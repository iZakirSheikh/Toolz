package com.prime.toolz.core.converter

import com.prime.toolz.R

private const val TAG = "Data"

@Suppress("FunctionName")
fun Data() = Converter(
    "converter_$TAG",
    R.string.data,
    R.drawable.ic_sd_card,
    arrayOf()
)