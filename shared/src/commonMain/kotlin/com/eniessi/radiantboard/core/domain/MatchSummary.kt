package com.eniessi.radiantboard.core.domain

data class MatchSummary(
    val matchId: String,
    val mapName: String,
    val isWin: Boolean,
    val totalKills: Int = 0,
    val kills: List<Kill> = emptyList()
)
