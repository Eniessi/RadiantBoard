package com.eniessi.radiantboard.core.domain

data class KillPosition(val x: Int, val y: Int)

data class Kill(
    val timeMillis: Int,
    val round: Int,
    val timeInRoundMillis: Int,
    val killerPuuid: String,
    val victimPuuid: String,
    val position: KillPosition,
    val killerPosition: KillPosition?,
    val weaponName: String?,
    val weaponIconUrl: String?,
    val playerLocations: Map<String, KillPosition> = emptyMap()
)
