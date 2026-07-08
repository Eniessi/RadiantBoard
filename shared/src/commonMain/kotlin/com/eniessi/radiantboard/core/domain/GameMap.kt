package com.eniessi.radiantboard.core.domain

data class GameMap(
    val displayName: String,
    val displayIcon: String?,
    val xMultiplier: Float,
    val yMultiplier: Float,
    val xScalarToAdd: Float,
    val yScalarToAdd: Float
)

interface MapRepository {
    suspend fun getMaps(): Result<List<GameMap>>
}
