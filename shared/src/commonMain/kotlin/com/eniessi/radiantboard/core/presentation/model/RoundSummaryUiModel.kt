package com.eniessi.radiantboard.core.presentation.model

data class RoundSummaryUiModel(
    val roundNumber: Int,
    val isWin: Boolean,
    val killsByCurrentUser: Int,
    val currentUserName: String,
    val diagnosticTags: List<String> = emptyList()
)
