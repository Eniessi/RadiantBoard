package com.eniessi.radiantboard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform