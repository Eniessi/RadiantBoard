package com.eniessi.radiantboard.core.domain.heuristics

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.MatchPlayerInfo

enum class DiagnosticType {
    TRADE_KILL_REALIZADO,
    MORTE_SEM_TRADE,
    FIRST_BLOOD,
    FIRST_DEATH
}

data class Diagnostic(
    val type: DiagnosticType,
    val round: Int,
    val message: String
)

interface MatchHeuristic {
    fun evaluate(
        killsInRound: List<Kill>,
        playersMap: Map<String, MatchPlayerInfo>,
        userPuuid: String,
        userTeam: String
    ): List<Diagnostic>
}
