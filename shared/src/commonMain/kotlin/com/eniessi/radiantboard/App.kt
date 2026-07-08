package com.eniessi.radiantboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eniessi.radiantboard.core.di.networkModule
import com.eniessi.radiantboard.core.di.repositoryModule
import com.eniessi.radiantboard.core.domain.ValorantRepository
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(networkModule, repositoryModule)
        }
    ) {
        MaterialTheme {
            val repository = koinInject<ValorantRepository>()
            val scope = rememberCoroutineScope()
            var consoleOutput by remember { mutableStateOf("Aguardando teste...") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Radiant Board",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            consoleOutput = "Buscando perfil de Aspas#BR1..."
                            val result = repository.getPlayerProfile("Eniessi", "gol16")
                            consoleOutput = result.fold(
                                onSuccess = { profile ->
                                    buildString {
                                        appendLine("Perfil encontrado!")
                                        appendLine("PUUID: ${profile.puuid}")
                                        appendLine("Região: ${profile.region}")
                                        appendLine("Nível: ${profile.accountLevel}")
                                    }
                                },
                                onFailure = { exception ->
                                    "Erro: ${exception.message}"
                                }
                            )
                        }
                    }
                ) {
                    Text("Testar Conexão")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = consoleOutput,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
