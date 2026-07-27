package com.eniessi.radiantboard.core.domain.heuristics

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.MatchPlayerInfo
import kotlin.math.hypot

class UntradedDeathHeuristic : MatchHeuristic {

    override fun evaluate(
        killsInRound: List<Kill>,
        playersMap: Map<String, MatchPlayerInfo>,
        userPuuid: String,
        userTeam: String
    ): List<Diagnostic> {
        val userDeaths = killsInRound
            .filter { it.victimPuuid == userPuuid }
            .sortedBy { it.timeInRoundMillis }

        val diagnostics = mutableListOf<Diagnostic>()

        for (death in userDeaths) {
            val deathTime = death.timeInRoundMillis
            val killerPuuid = death.killerPuuid

            val wasTraded = killsInRound.any { subsequent ->
                subsequent.killerPuuid != userPuuid &&
                    subsequent.victimPuuid == killerPuuid &&
                    playersMap[subsequent.killerPuuid]?.team == userTeam &&
                    subsequent.timeInRoundMillis > deathTime &&
                    (subsequent.timeInRoundMillis - deathTime) <= TRADE_WINDOW_MS
            }

            if (!wasTraded) {
                val userDeathX = death.position.x.toDouble()
                val userDeathY = death.position.y.toDouble()

                val nearbyAlly = death.playerLocations.entries.any { (puuid, allyPos) ->
                    if (puuid == userPuuid) return@any false
                    if (playersMap[puuid]?.team != userTeam) return@any false

                    val distance = hypot(userDeathX - allyPos.x, userDeathY - allyPos.y)
                    distance <= NEARBY_ALLY_THRESHOLD_UNREAL
                }

                val message = if (nearbyAlly) {
                    "Morte sem Trade (Aliado proximo falhou na troca)"
                } else {
                    "Morte Isolada (Sem suporte proximo)"
                }

                diagnostics.add(
                    Diagnostic(
                        type = DiagnosticType.MORTE_SEM_TRADE,
                        round = death.round,
                        message = message
                    )
                )
            }
        }

        return diagnostics
    }

    private companion object {
        const val TRADE_WINDOW_MS = 4000
        const val NEARBY_ALLY_THRESHOLD_UNREAL = 1500.0
    }
}
