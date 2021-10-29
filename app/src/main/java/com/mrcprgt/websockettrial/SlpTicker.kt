package com.mrcprgt.websockettrial

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SlpTicker(
    @Json(name = "s")
    val symbol: String,

    @Json(name = "p")
    val price: Float,

    @Json(name = "q")
    val quantity: Double,

    @Json(name = "T")
    val tradeTime: String,

    )