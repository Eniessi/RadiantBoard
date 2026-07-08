package com.eniessi.radiantboard.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eniessi.radiantboard.core.domain.GetLatestMatchAnalysisUseCase
import com.eniessi.radiantboard.core.domain.MatchAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MatchUiState {
    data object Idle : MatchUiState
    data object Loading : MatchUiState
    data class Success(val analysis: MatchAnalysisResult) : MatchUiState
    data class Error(val message: String) : MatchUiState
}

class MatchViewModel(
    private val analyzeMatchUseCase: GetLatestMatchAnalysisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Idle)
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    init {
        loadLatestMatch("GGiraldi", "2222")
    }

    fun loadLatestMatch(riotId: String, tagLine: String) {
        viewModelScope.launch {
            _uiState.value = MatchUiState.Loading
            val result = analyzeMatchUseCase(riotId, tagLine)
            _uiState.value = result.fold(
                onSuccess = { MatchUiState.Success(it) },
                onFailure = { MatchUiState.Error(it.message ?: "Erro desconhecido") }
            )
        }
    }
}
