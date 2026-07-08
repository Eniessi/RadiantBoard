package com.eniessi.radiantboard.core.domain

data class RelativePosition(val x: Float, val y: Float)

fun mapToRelativePosition(rawX: Int, rawY: Int, mapData: GameMap): RelativePosition {
    // MAGIA DA ENGENHARIA GRÁFICA:
    // O eixo Y do jogo (Largura) mapeia para o eixo X do Canvas 2D
    // O eixo X do jogo (Profundidade) mapeia para o eixo Y do Canvas 2D
    val relX = (rawY * mapData.xMultiplier) + mapData.xScalarToAdd
    val relY = (rawX * mapData.yMultiplier) + mapData.yScalarToAdd

    return RelativePosition(relX, relY)
}