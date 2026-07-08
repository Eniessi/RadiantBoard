package com.eniessi.radiantboard.core.domain

data class MatchAnalysisResult(
    val profile: PlayerProfile,
    val match: MatchSummary,
    val map: GameMap
)

class GetLatestMatchAnalysisUseCase(
    private val valorantRepo: ValorantRepository,
    private val mapRepo: MapRepository
) {
    suspend operator fun invoke(riotId: String, tagLine: String): Result<MatchAnalysisResult> {
        val profile = valorantRepo.getPlayerProfile(riotId, tagLine).getOrElse {
            return Result.failure(it)
        }

        val matches = valorantRepo.getMatchHistory(profile.region, profile.puuid).getOrElse {
            return Result.failure(it)
        }

        val latestMatch = matches.firstOrNull()
            ?: return Result.failure(Exception("Nenhuma partida encontrada"))

        val maps = mapRepo.getMaps().getOrElse {
            return Result.failure(it)
        }

        val map = maps.find { it.displayName.equals(latestMatch.mapName, ignoreCase = true) }
            ?: return Result.failure(Exception("Mapa não encontrado: ${latestMatch.mapName}"))

        return Result.success(
            MatchAnalysisResult(
                profile = profile,
                match = latestMatch,
                map = map
            )
        )
    }
}
