package com.eniessi.radiantboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eniessi.radiantboard.core.di.domainModule
import com.eniessi.radiantboard.core.di.networkModule
import com.eniessi.radiantboard.core.di.presentationModule
import com.eniessi.radiantboard.core.di.repositoryModule
import com.eniessi.radiantboard.core.presentation.MatchViewModel
import com.eniessi.radiantboard.core.presentation.MatchUiState
import com.eniessi.radiantboard.ui.TacticalBoardScreen
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(networkModule, repositoryModule, domainModule, presentationModule)
        }
    ) {
        MaterialTheme {
            val viewModel = koinInject<MatchViewModel>()
            val state by viewModel.uiState.collectAsState()

            when (state) {
                is MatchUiState.Idle -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Inicializando...")
                    }
                }
                is MatchUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MatchUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Erro: ${(state as MatchUiState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is MatchUiState.Success -> {
                    TacticalBoardScreen((state as MatchUiState.Success).analysis)
                }
            }
        }
    }
}
