package com.eniessi.radiantboard.core.network.dto

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.KillPosition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchesResponse(
    @SerialName("data")
    val data: List<MatchDto> = emptyList()
)

@Serializable
data class MatchDto(
    @SerialName("metadata")
    val metadata: MatchMetadataDto = MatchMetadataDto(),
    @SerialName("kills")
    val kills: List<KillDto> = emptyList()
)

@Serializable
data class MatchMetadataDto(
    @SerialName("matchid")
    val matchId: String = "",
    @SerialName("map")
    val map: String = ""
)

@Serializable
data class KillDto(
    @SerialName("time_since_round_start_millis")
    val timeSinceRoundStartMillis: Int = 0,
    @SerialName("killer_puuid")
    val killerPuuid: String = "",
    @SerialName("victim_puuid")
    val victimPuuid: String = "",
    @SerialName("victim_death_location")
    val victimDeathLocation: LocationDto = LocationDto()
)

@Serializable
data class LocationDto(
    @SerialName("x")
    val x: Int = 0,
    @SerialName("y")
    val y: Int = 0
)

fun KillDto.toDomain() = Kill(
    timeMillis = timeSinceRoundStartMillis,
    killerPuuid = killerPuuid,
    victimPuuid = victimPuuid,
    position = KillPosition(victimDeathLocation.x, victimDeathLocation.y)
)
