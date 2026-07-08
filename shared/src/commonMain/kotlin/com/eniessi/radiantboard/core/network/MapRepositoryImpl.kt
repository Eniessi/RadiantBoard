package com.eniessi.radiantboard.core.network

import com.eniessi.radiantboard.core.domain.GameMap
import com.eniessi.radiantboard.core.domain.MapRepository
import com.eniessi.radiantboard.core.network.dto.MapDto
import com.eniessi.radiantboard.core.network.dto.MapListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.isSuccess

class MapRepositoryImpl(
    private val httpClient: HttpClient
) : MapRepository {

    override suspend fun getMaps(): Result<List<GameMap>> {
        return try {
            val response = httpClient.get {
                url("https://valorant-api.com/v1/maps")
            }

            if (response.status.isSuccess()) {
                val mapListResponse = response.body<MapListResponse>()
                val maps = mapListResponse.data.map { it.toDomain() }
                Result.success(maps)
            } else {
                Result.failure(
                    Exception("HTTP ${response.status.value}: ${response.status.description}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun MapDto.toDomain(): GameMap {
    return GameMap(
        displayName = displayName,
        displayIcon = displayIcon,
        xMultiplier = xMultiplier,
        yMultiplier = yMultiplier,
        xScalarToAdd = xScalarToAdd,
        yScalarToAdd = yScalarToAdd
    )
}
