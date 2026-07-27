package com.eniessi.radiantboard.core.domain

interface SessionRepository {
    fun getCurrentRiotId(): String
    fun getCurrentTagLine(): String
    fun isLoggedIn(): Boolean
}
