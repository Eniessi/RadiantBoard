package com.eniessi.radiantboard.core.domain

interface ValorantRepository {
    suspend fun getPlayerProfile(riotId: String, tagLine: String): Result<PlayerProfile>
    suspend fun getMatchHistory(region: String, puuid: String): Result<List<MatchSummary>>
}
