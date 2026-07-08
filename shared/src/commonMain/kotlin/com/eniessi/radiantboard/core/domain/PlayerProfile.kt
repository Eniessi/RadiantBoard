package com.eniessi.radiantboard.core.domain

data class PlayerProfile(
    val puuid: String,
    val riotId: String,
    val tagLine: String,
    val region: String,
    val accountLevel: Int
)
