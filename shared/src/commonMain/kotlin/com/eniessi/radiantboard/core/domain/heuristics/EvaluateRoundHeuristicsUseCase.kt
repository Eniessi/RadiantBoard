package com.eniessi.radiantboard.core.domain.heuristics

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.MatchPlayerInfo

class EvaluateRoundHeuristicsUseCase(
    private val heuristics: Set<MatchHeuristic>
) {
    operator fun invoke(
        killsInRound: List<Kill>,
        playersMap: Map<String, MatchPlayerInfo>,
        userPuuid: String,
        userTeam: String
    ): List<Diagnostic> {
        return heuristics.flatMap { it.evaluate(killsInRound, playersMap, userPuuid, userTeam) }
    }
}
