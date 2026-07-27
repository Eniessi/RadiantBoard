package com.eniessi.radiantboard.core.domain

class FilterUserContextKillsUseCase {

    operator fun invoke(
        killsInRound: List<Kill>,
        userPuuid: String,
        userTeam: String,
        playersMap: Map<String, MatchPlayerInfo>
    ): List<Kill> {
        return killsInRound.filter { kill ->
            kill.killerPuuid == userPuuid ||
                kill.victimPuuid == userPuuid ||
                isTradeA(kill, userPuuid, userTeam, playersMap, killsInRound) ||
                isTradeB(kill, userPuuid, userTeam, playersMap, killsInRound)
        }
    }

    private fun isTradeA(
        kill: Kill,
        userPuuid: String,
        userTeam: String,
        playersMap: Map<String, MatchPlayerInfo>,
        killsInRound: List<Kill>
    ): Boolean {
        val isAllyVictim = playersMap[kill.victimPuuid]?.team == userTeam
        if (!isAllyVictim) return false

        return killsInRound.any { subsequent ->
            subsequent.killerPuuid == userPuuid &&
                subsequent.victimPuuid == kill.killerPuuid &&
                subsequent.timeInRoundMillis > kill.timeInRoundMillis &&
                (subsequent.timeInRoundMillis - kill.timeInRoundMillis) <= TRADE_WINDOW_MS
        }
    }

    private fun isTradeB(
        kill: Kill,
        userPuuid: String,
        userTeam: String,
        playersMap: Map<String, MatchPlayerInfo>,
        killsInRound: List<Kill>
    ): Boolean {
        val isAllyKiller = playersMap[kill.killerPuuid]?.team == userTeam
        if (!isAllyKiller) return false

        return killsInRound.any { previous ->
            previous.victimPuuid == userPuuid &&
                previous.killerPuuid == kill.victimPuuid &&
                previous.timeInRoundMillis < kill.timeInRoundMillis &&
                (kill.timeInRoundMillis - previous.timeInRoundMillis) <= TRADE_WINDOW_MS
        }
    }

    private companion object {
        const val TRADE_WINDOW_MS = 4000
    }
}
