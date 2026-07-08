package com.eniessi.radiantboard.core.domain
import kotlin.Result
interface ValorantRepository {
    suspend fun getPlayerProfile(riotId: String, tagLine: String): Result<PlayerProfile>
}
