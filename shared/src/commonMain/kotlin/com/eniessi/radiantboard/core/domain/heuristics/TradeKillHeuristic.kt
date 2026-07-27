package com.eniessi.radiantboard.core.domain.heuristics

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.MatchPlayerInfo

class TradeKillHeuristic : MatchHeuristic {

    override fun evaluate(
        killsInRound: List<Kill>,
        playersMap: Map<String, MatchPlayerInfo>,
        userPuuid: String,
        userTeam: String
    ): List<Diagnostic> {
        val diagnostics = mutableListOf<Diagnostic>()

        val userKills = killsInRound
            .filter { it.killerPuuid == userPuuid }
            .sortedBy { it.timeInRoundMillis }

        for (currentKill in userKills) {
            val victimPuuid = currentKill.victimPuuid
            val currentTime = currentKill.timeInRoundMillis

            val tradeCandidate = killsInRound.find { previous ->
                previous.killerPuuid == victimPuuid &&
                    previous.victimPuuid != userPuuid &&
                    playersMap[previous.victimPuuid]?.team == userTeam &&
                    previous.timeInRoundMillis < currentTime &&
                    (currentTime - previous.timeInRoundMillis) <= TRADE_WINDOW_MS
            }

            if (tradeCandidate != null) {
                val avengedAgentName = playersMap[tradeCandidate.victimPuuid]?.agentName ?: "aliado"
                diagnostics.add(
                    Diagnostic(
                        type = DiagnosticType.TRADE_KILL_REALIZADO,
                        round = currentKill.round,
                        message = "Voce vingou a morte de $avengedAgentName!"
                    )
                )
            }
        }

        return diagnostics
    }

    private companion object {
        const val TRADE_WINDOW_MS = 4000
    }
}
