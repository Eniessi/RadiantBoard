package com.eniessi.radiantboard.core.domain

data class KillPosition(val x: Int, val y: Int)

data class Kill(
    val timeMillis: Int,
    val killerPuuid: String,
    val victimPuuid: String,
    val position: KillPosition
)
