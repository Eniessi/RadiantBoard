package com.eniessi.radiantboard.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eniessi.radiantboard.core.domain.FilterUserContextKillsUseCase
import com.eniessi.radiantboard.core.domain.GetLatestMatchAnalysisUseCase
import com.eniessi.radiantboard.core.domain.MatchAnalysisResult
import com.eniessi.radiantboard.core.domain.SessionRepository
import com.eniessi.radiantboard.core.domain.heuristics.EvaluateRoundHeuristicsUseCase
import com.eniessi.radiantboard.core.domain.mapToRelativePosition
import com.eniessi.radiantboard.core.presentation.model.KillUiModel
import com.eniessi.radiantboard.core.presentation.model.RoundSummaryUiModel
import com.eniessi.radiantboard.core.presentation.model.TacticalBoardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MatchUiState {
    data object Idle : MatchUiState
    data object Loading : MatchUiState
    data object Success : MatchUiState
    data class Error(val message: String) : MatchUiState
}

class MatchViewModel(
    private val analyzeMatchUseCase: GetLatestMatchAnalysisUseCase,
    private val sessionRepository: SessionRepository,
    private val evaluateRoundHeuristicsUseCase: EvaluateRoundHeuristicsUseCase,
    private val filterUserContextKillsUseCase: FilterUserContextKillsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Idle)
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    private val _tacticalState = MutableStateFlow<TacticalBoardUiState?>(null)
    val tacticalState: StateFlow<TacticalBoardUiState?> = _tacticalState.asStateFlow()

    private var cachedAnalysis: MatchAnalysisResult? = null

    fun loadLatestMatch() {
        val riotId = sessionRepository.getCurrentRiotId()
        val tagLine = sessionRepository.getCurrentTagLine()

        if (!sessionRepository.isLoggedIn()) {
            _uiState.value = MatchUiState.Error("Usuario nao configurado na sessao")
            return
        }

        viewModelScope.launch {
            _uiState.value = MatchUiState.Loading
            val result = analyzeMatchUseCase(riotId, tagLine)
            result.fold(
                onSuccess = { analysis ->
                    cachedAnalysis = analysis
                    val roundSummaries = buildRoundSummaries(analysis)
                    val firstRound = roundSummaries.firstOrNull()?.roundNumber ?: 0
                    _tacticalState.value = buildTacticalState(analysis, firstRound, roundSummaries)
                    _uiState.value = MatchUiState.Success
                },
                onFailure = {
                    _uiState.value = MatchUiState.Error(it.message ?: "Erro desconhecido")
                }
            )
        }
    }

    fun onRoundSelected(round: Int) {
        val analysis = cachedAnalysis ?: return
        val current = _tacticalState.value
        val roundSummaries = current?.roundSummaries ?: buildRoundSummaries(analysis)
        _tacticalState.value = buildTacticalState(analysis, round, roundSummaries)
    }

    fun onTimeChanged(timeMillis: Float) {
        val current = _tacticalState.value ?: return
        val analysis = cachedAnalysis ?: return
        _tacticalState.value = buildTacticalState(
            analysis, current.selectedRound, current.roundSummaries, timeMillis
        )
    }

    private fun buildRoundSummaries(analysis: MatchAnalysisResult): List<RoundSummaryUiModel> {
        val allRounds = analysis.match.kills.map { it.round }.distinct().sorted()
        val userName = analysis.profile.riotId
        val userPuuid = analysis.profile.puuid

        return allRounds.map { roundNumber ->
            val killsInRound = analysis.match.kills.filter { it.round == roundNumber }

            val userTeamKills = killsInRound.count { kill ->
                analysis.match.playersMap[kill.killerPuuid]?.team == analysis.match.userTeam
            }
            val enemyTeamKills = killsInRound.count { kill ->
                val killerTeam = analysis.match.playersMap[kill.killerPuuid]?.team
                killerTeam != null && killerTeam != analysis.match.userTeam
            }

            val killsByCurrentUser = killsInRound.count { it.killerPuuid == userPuuid }

            val diagnostics = evaluateRoundHeuristicsUseCase(
                killsInRound = killsInRound,
                playersMap = analysis.match.playersMap,
                userPuuid = userPuuid,
                userTeam = analysis.match.userTeam
            )

            RoundSummaryUiModel(
                roundNumber = roundNumber,
                isWin = userTeamKills > enemyTeamKills,
                killsByCurrentUser = killsByCurrentUser,
                currentUserName = userName,
                diagnosticTags = diagnostics.map { it.message }
            )
        }
    }

    private fun buildTacticalState(
        analysis: MatchAnalysisResult,
        selectedRound: Int,
        roundSummaries: List<RoundSummaryUiModel>,
        currentTimeMillis: Float? = null
    ): TacticalBoardUiState {
        val killsInRound = analysis.match.kills.filter { it.round == selectedRound }
        val maxTime = killsInRound.maxOfOrNull { it.timeInRoundMillis }?.toFloat() ?: 0f
        val time = currentTimeMillis ?: maxTime

        val userPuuid = analysis.profile.puuid
        val relevantKills = filterUserContextKillsUseCase(
            killsInRound = killsInRound,
            userPuuid = userPuuid,
            userTeam = analysis.match.userTeam,
            playersMap = analysis.match.playersMap
        )

        val visibleKills = relevantKills
            .filter { it.timeInRoundMillis <= time }
            .map { kill ->
                val killTime = kill.timeInRoundMillis.toFloat()
                KillUiModel(
                    kill = kill,
                    victimRelPosition = mapToRelativePosition(
                        kill.position.x, kill.position.y, analysis.map
                    ),
                    killerRelPosition = kill.killerPosition?.let { pos ->
                        mapToRelativePosition(pos.x, pos.y, analysis.map)
                    },
                    victimAgentInfo = analysis.match.playersMap[kill.victimPuuid],
                    killerAgentInfo = analysis.match.playersMap[kill.killerPuuid],
                    isVictimCurrentUser = kill.victimPuuid == userPuuid,
                    isKillerCurrentUser = kill.killerPuuid == userPuuid,
                    isAlly = analysis.match.playersMap[kill.victimPuuid]?.team == analysis.match.userTeam,
                    isKillerAlly = analysis.match.playersMap[kill.killerPuuid]?.team == analysis.match.userTeam,
                    isVectorActive = time in killTime..(killTime + VECTORS_ACTIVE_WINDOW_MS)
                )
            }

        return TacticalBoardUiState(
            roundSummaries = roundSummaries,
            selectedRound = selectedRound,
            currentTimeMillis = time,
            maxTimeInMillis = maxTime,
            visibleKills = visibleKills,
            map = analysis.map,
            profile = analysis.profile,
            userTeam = analysis.match.userTeam
        )
    }

    private companion object {
        const val VECTORS_ACTIVE_WINDOW_MS = 4000f
    }
}
