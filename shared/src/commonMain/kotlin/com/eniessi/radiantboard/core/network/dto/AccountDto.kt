package com.eniessi.radiantboard.core.network.dto

import com.eniessi.radiantboard.core.domain.PlayerProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    @SerialName("puuid")
    val puuid: String = "",
    @SerialName("region")
    val region: String = "",
    @SerialName("account_level")
    val accountLevel: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("tag")
    val tag: String = ""
)

fun AccountDto.toDomain(): PlayerProfile {
    return PlayerProfile(
        puuid = puuid,
        riotId = name,
        tagLine = tag,
        region = region,
        accountLevel = accountLevel
    )
}
