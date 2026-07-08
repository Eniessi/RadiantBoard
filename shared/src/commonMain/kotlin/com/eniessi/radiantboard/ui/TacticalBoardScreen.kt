package com.eniessi.radiantboard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.eniessi.radiantboard.core.domain.MatchAnalysisResult
import com.eniessi.radiantboard.core.domain.mapToRelativePosition
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TacticalBoardScreen(analysis: MatchAnalysisResult) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Container Geométrico: Força a Proporção Quadrada Perfeita (1:1)
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {

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

                analysis.match.kills.forEach { kill ->
                    // Agora a conversão mapeará as mortes com exatidão vetorial
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
    }
}