package com.currupt.reflame.remote.repository

import com.currupt.reflame.client.model.ClientConfig

interface RemoteConfigRepository {
    suspend fun fetchClientConfig(): Result<ClientConfig>
}
