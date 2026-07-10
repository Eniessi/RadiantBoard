package com.eniessi.radiantboard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.eniessi.radiantboard.core.domain.MatchAnalysisResult
import com.eniessi.radiantboard.core.domain.mapToRelativePosition
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TacticalBoardScreen(analysis: MatchAnalysisResult) {
    val maxTimeMillis = analysis.match.kills.maxOfOrNull { it.timeMillis }?.toFloat() ?: 0f
    var currentTime by remember { mutableStateOf(maxTimeMillis) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Container Geométrico: Força a Proporção Quadrada Perfeita (1:1)
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            analysis.map.displayIcon?.let { iconUrl ->
                KamelImage(
                    resource = asyncPainterResource(iconUrl),
                    contentDescription = analysis.map.displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    onLoading = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
                    onFailure = { exception ->
                        Text(
                            text = "Erro Img: ${exception.message}",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                analysis.match.kills
                    .filter { it.timeMillis <= currentTime }
                    .forEach { kill ->
                        val relative = mapToRelativePosition(
                            rawX = kill.position.x,
                            rawY = kill.position.y,
                            mapData = analysis.map
                        )

                        val centerX = relative.x * canvasWidth
                        val centerY = relative.y * canvasHeight

                        val color = if (kill.victimPuuid == analysis.profile.puuid) Color.Red else Color.Blue

                        drawCircle(
                            color = color,
                            radius = 12f,
                            center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                        )
                    }
            }
        }

        // Painel de Controle
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            val totalSeconds = (currentTime / 1000).toInt()
            val minutes = (totalSeconds / 60).toString().padStart(2, '0')
            val seconds = (totalSeconds % 60).toString().padStart(2, '0')
            Text(text = "Tempo de Partida: $minutes:$seconds")
            Slider(
                value = currentTime,
                onValueChange = { currentTime = it },
                valueRange = 0f..(if (maxTimeMillis > 0f) maxTimeMillis else 1f)
            )
        }
    }
}
