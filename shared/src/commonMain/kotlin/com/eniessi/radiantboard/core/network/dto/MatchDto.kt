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
    val kills: List<KillDto> = emptyList(),
    @SerialName("players")
    val players: PlayersWrapperDto = PlayersWrapperDto()
)

@Serializable
data class MatchMetadataDto(
    @SerialName("matchid")
    val matchId: String = "",
    @SerialName("map")
    val map: String = ""
)

@Serializable
data class PlayersWrapperDto(
    @SerialName("all_players")
    val allPlayers: List<PlayerDto> = emptyList()
)

@Serializable
data class PlayerDto(
    @SerialName("puuid")
    val puuid: String = "",
    @SerialName("character")
    val character: String = "",
    @SerialName("team")
    val team: String = "",
    @SerialName("assets")
    val assets: AgentAssetsDto = AgentAssetsDto()
)

@Serializable
data class AgentAssetsDto(
    @SerialName("agent")
    val agent: AgentImagesDto = AgentImagesDto()
)

@Serializable
data class AgentImagesDto(
    @SerialName("small")
    val small: String = ""
)

@Serializable
data class WeaponAssetsDto(
    @SerialName("display_icon")
    val displayIcon: String? = null
)

@Serializable
data class PlayerLocationDto(
    @SerialName("player_puuid")
    val playerPuuid: String = "",
    @SerialName("location")
    val location: LocationDto = LocationDto()
)

@Serializable
data class KillDto(
    @SerialName("kill_time_in_match")
    val killTimeInMatch: Int = 0,
    @SerialName("round")
    val round: Int = 0,
    @SerialName("kill_time_in_round")
    val killTimeInRound: Int = 0,
    @SerialName("killer_puuid")
    val killerPuuid: String = "",
    @SerialName("victim_puuid")
    val victimPuuid: String = "",
    @SerialName("victim_death_location")
    val victimDeathLocation: LocationDto = LocationDto(),
    @SerialName("damage_weapon_name")
    val damageWeaponName: String? = null,
    @SerialName("damage_weapon_assets")
    val damageWeaponAssets: WeaponAssetsDto? = null,
    @SerialName("player_locations_on_kill")
    val playerLocationsOnKill: List<PlayerLocationDto> = emptyList()
)

@Serializable
data class LocationDto(
    @SerialName("x")
    val x: Int = 0,
    @SerialName("y")
    val y: Int = 0
)

fun KillDto.toDomain() = Kill(
    timeMillis = killTimeInMatch,
    round = round,
    timeInRoundMillis = killTimeInRound,
    killerPuuid = killerPuuid,
    victimPuuid = victimPuuid,
    position = KillPosition(victimDeathLocation.x, victimDeathLocation.y),
    killerPosition = playerLocationsOnKill
        .find { it.playerPuuid == killerPuuid }
        ?.location
        ?.let { KillPosition(it.x, it.y) },
    weaponName = damageWeaponName,
    weaponIconUrl = damageWeaponAssets?.displayIcon,
    playerLocations = playerLocationsOnKill.associate { loc ->
        loc.playerPuuid to KillPosition(loc.location.x, loc.location.y)
    }
)
