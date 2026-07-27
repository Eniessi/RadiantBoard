package com.eniessi.radiantboard.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.eniessi.radiantboard.core.presentation.model.RoundSummaryUiModel
import com.eniessi.radiantboard.core.presentation.model.TacticalBoardUiState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TacticalBoardScreen(
    state: TacticalBoardUiState,
    onRoundSelected: (Int) -> Unit,
    onTimeChanged: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(0.6f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                state.map.displayIcon?.let { iconUrl ->
                    KamelImage(
                        resource = asyncPainterResource(iconUrl),
                        contentDescription = state.map.displayName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                        onLoading = {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        },
                        onFailure = { exception ->
                            Text(
                                text = "Erro Img: ${exception.message}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    )
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        state.visibleKills.forEach { killModel ->
                            if (!killModel.isVectorActive || !(killModel.isKillerCurrentUser || killModel.isVictimCurrentUser)) return@forEach

                            val killerRel = killModel.killerRelPosition ?: return@forEach
                            val victimRel = killModel.victimRelPosition

                            val killerX = canvasWidth * killerRel.x
                            val killerY = canvasHeight * killerRel.y
                            val victimX = canvasWidth * victimRel.x
                            val victimY = canvasHeight * victimRel.y

                            val lineColor = when {
                                killModel.isKillerCurrentUser -> Color.Yellow.copy(alpha = 0.7f)
                                killModel.isVictimCurrentUser -> Color.Red.copy(alpha = 0.7f)
                                else -> Color.White.copy(alpha = 0.4f)
                            }

                            drawLine(
                                color = lineColor,
                                start = Offset(killerX, killerY),
                                end = Offset(victimX, victimY),
                                strokeWidth = 3f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                            )
                        }
                    }

                    state.visibleKills.forEach { killModel ->
                        val victimRelative = killModel.victimRelPosition
                        val victimOffsetX = maxWidth * victimRelative.x
                        val victimOffsetY = maxHeight * victimRelative.y

                        val victimInfo = killModel.victimAgentInfo
                        val victimBorderColor = when {
                            killModel.isVictimCurrentUser -> Color.Yellow
                            killModel.isAlly -> Color.Blue
                            else -> Color.Red
                        }
                        val victimBorderWidth = if (killModel.isVictimCurrentUser) 3.dp else 2.dp
                        val victimIconSize = if (killModel.isVictimCurrentUser) 32.dp else 24.dp
                        val victimOffsetAdj = if (killModel.isVictimCurrentUser) 16.dp else 12.dp
                        val victimZIndex = if (killModel.isVictimCurrentUser) 1f else 0f

                        if (victimInfo != null) {
                            KamelImage(
                                resource = asyncPainterResource(victimInfo.agentIconUrl),
                                contentDescription = victimInfo.agentName,
                                modifier = Modifier
                                    .size(victimIconSize)
                                    .offset(victimOffsetX - victimOffsetAdj, victimOffsetY - victimOffsetAdj)
                                    .zIndex(victimZIndex)
                                    .clip(CircleShape)
                                    .border(victimBorderWidth, victimBorderColor, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(victimIconSize)
                                    .offset(victimOffsetX - victimOffsetAdj, victimOffsetY - victimOffsetAdj)
                                    .zIndex(victimZIndex)
                                    .clip(CircleShape)
                                    .border(victimBorderWidth, victimBorderColor, CircleShape)
                            )
                        }

                        if (killModel.isVectorActive && (killModel.isKillerCurrentUser || killModel.isVictimCurrentUser)) {
                            val killerRelative = killModel.killerRelPosition ?: return@forEach
                            val killerOffsetX = maxWidth * killerRelative.x
                            val killerOffsetY = maxHeight * killerRelative.y

                            val killerInfo = killModel.killerAgentInfo
                            val killerBorderColor = when {
                                killModel.isKillerCurrentUser -> Color.Yellow
                                killModel.isKillerAlly -> Color.Blue
                                else -> Color.Red
                            }
                            val killerBorderWidth = if (killModel.isKillerCurrentUser) 3.dp else 2.dp
                            val killerIconSize = if (killModel.isKillerCurrentUser) 32.dp else 24.dp
                            val killerOffsetAdj = if (killModel.isKillerCurrentUser) 16.dp else 12.dp
                            val killerZIndex = if (killModel.isKillerCurrentUser) 10f else 0.5f

                            if (killerInfo != null) {
                                KamelImage(
                                    resource = asyncPainterResource(killerInfo.agentIconUrl),
                                    contentDescription = killerInfo.agentName,
                                    modifier = Modifier
                                        .size(killerIconSize)
                                        .offset(killerOffsetX - killerOffsetAdj, killerOffsetY - killerOffsetAdj)
                                        .zIndex(killerZIndex)
                                        .clip(CircleShape)
                                        .border(killerBorderWidth, killerBorderColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(killerIconSize)
                                        .offset(killerOffsetX - killerOffsetAdj, killerOffsetY - killerOffsetAdj)
                                        .zIndex(killerZIndex)
                                        .clip(CircleShape)
                                        .border(killerBorderWidth, killerBorderColor, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                val totalSecs = (state.currentTimeMillis / 1000).toInt()
                val minutes = (totalSecs / 60).toString().padStart(2, '0')
                val seconds = (totalSecs % 60).toString().padStart(2, '0')
                Text(
                    text = "Tempo de Partida: $minutes:$seconds",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = state.currentTimeMillis,
                    onValueChange = { onTimeChanged(it) },
                    valueRange = 0f..(if (state.maxTimeInMillis > 0f) state.maxTimeInMillis else 1f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }
            items(state.roundSummaries) { summary ->
                RoundSummaryCard(
                    summary = summary,
                    isSelected = summary.roundNumber == state.selectedRound,
                    onClick = { onRoundSelected(summary.roundNumber) }
                )
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoundSummaryCard(
    summary: RoundSummaryUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val winColor = if (summary.isWin) Color(0xFF2E7D32) else Color(0xFFC62828)
    val winLabel = if (summary.isWin) "Vitoria" else "Derrota"
    val cardContainerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Round ${summary.roundNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = winLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = winColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .border(2.dp, winColor, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Abates: ${summary.killsByCurrentUser}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (summary.diagnosticTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    summary.diagnosticTags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = summary.currentUserName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
