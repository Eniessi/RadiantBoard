package com.eniessi.radiantboard.core.presentation.model

import com.eniessi.radiantboard.core.domain.GameMap
import com.eniessi.radiantboard.core.domain.PlayerProfile

data class TacticalBoardUiState(
    val roundSummaries: List<RoundSummaryUiModel>,
    val selectedRound: Int,
    val currentTimeMillis: Float,
    val maxTimeInMillis: Float,
    val visibleKills: List<KillUiModel>,
    val map: GameMap,
    val profile: PlayerProfile,
    val userTeam: String
)
