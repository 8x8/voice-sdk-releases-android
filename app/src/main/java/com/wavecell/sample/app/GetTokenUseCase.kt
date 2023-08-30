package com.wavecell.sample.app

import kotlinx.coroutines.CoroutineDispatcher

class GetTokenUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val tokenRemoteDataSource: TokenRemoteDataSource
) :
    UseCaseKt<String>(coroutineDispatcher) {

    private lateinit var userId: String
    private lateinit var accountId: String
    private lateinit var tokenUrl: String

    suspend fun execute(tokenUrl: String, userId: String, accountId: String): ResultWrapper<String> {
        this.userId = userId
        this.accountId = accountId
        this.tokenUrl = tokenUrl
        return super.execute()
    }

    override suspend fun build(): String {
        return tokenRemoteDataSource.getToken(tokenUrl, userId, accountId)
    }
}
