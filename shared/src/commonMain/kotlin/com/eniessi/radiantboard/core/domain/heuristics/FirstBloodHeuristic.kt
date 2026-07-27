package com.eniessi.radiantboard.core.domain.heuristics

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.MatchPlayerInfo

class FirstBloodHeuristic : MatchHeuristic {

    override fun evaluate(
        killsInRound: List<Kill>,
        playersMap: Map<String, MatchPlayerInfo>,
        userPuuid: String,
        userTeam: String
    ): List<Diagnostic> {
        val firstKill = killsInRound.minByOrNull { it.timeInRoundMillis } ?: return emptyList()

        return when (userPuuid) {
            firstKill.killerPuuid -> listOf(
                Diagnostic(
                    type = DiagnosticType.FIRST_BLOOD,
                    round = firstKill.round,
                    message = "First Blood!"
                )
            )
            firstKill.victimPuuid -> listOf(
                Diagnostic(
                    type = DiagnosticType.FIRST_DEATH,
                    round = firstKill.round,
                    message = "First Death (Entrada falha)"
                )
            )
            else -> emptyList()
        }
    }
}
