package com.eniessi.radiantboard.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapListResponse(
    @SerialName("data")
    val data: List<MapDto> = emptyList()
)

@Serializable
data class MapDto(
    @SerialName("displayName")
    val displayName: String = "",
    @SerialName("displayIcon")
    val displayIcon: String? = null,
    @SerialName("xMultiplier")
    val xMultiplier: Float = 0f,
    @SerialName("yMultiplier")
    val yMultiplier: Float = 0f,
    @SerialName("xScalarToAdd")
    val xScalarToAdd: Float = 0f,
    @SerialName("yScalarToAdd")
    val yScalarToAdd: Float = 0f
)
