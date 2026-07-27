package com.eniessi.radiantboard.core.network

import com.eniessi.radiantboard.core.domain.SessionRepository

class SessionRepositoryImpl : SessionRepository {

    private var riotId: String = ""
    private var tagLine: String = ""

    override fun getCurrentRiotId(): String = riotId
    override fun getCurrentTagLine(): String = tagLine
    override fun isLoggedIn(): Boolean = riotId.isNotBlank() && tagLine.isNotBlank()

    fun configure(riotId: String, tagLine: String) {
        this.riotId = riotId
        this.tagLine = tagLine
    }
}
