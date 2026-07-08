package com.eniessi.radiantboard.core.network

import com.eniessi.radiantboard.core.domain.PlayerProfile
import com.eniessi.radiantboard.core.domain.ValorantRepository
import com.eniessi.radiantboard.core.network.dto.AccountDto
import com.eniessi.radiantboard.core.network.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.Result
import kotlin.Exception

class ValorantRepositoryImpl(
    private val httpClient: HttpClient
) : ValorantRepository {

    override suspend fun getPlayerProfile(riotId: String, tagLine: String): Result<PlayerProfile> {
        return try {
            val response = httpClient.get {
                url("https://api.henrikdev.xyz/valorant/v1/account/$riotId/$tagLine")
            }

            if (response.status.isSuccess()) {
                val accountResponse = response.body<AccountResponse>()
                Result.success(accountResponse.data.toDomain())
            } else {
                Result.failure(
                    Exception("HTTP ${response.status.value}: ${response.status.description}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class AccountResponse(
    @SerialName("status")
    val status: Int = 0,
    @SerialName("data")
    val data: AccountDto = AccountDto()
)
