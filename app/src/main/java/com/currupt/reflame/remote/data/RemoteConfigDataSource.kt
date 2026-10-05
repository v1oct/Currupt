package com.currupt.reflame.remote.data

import com.currupt.reflame.client.model.ClientConfig

interface RemoteConfigDataSource : PublishedConfigDataSource {
    suspend fun getClientConfig(): ClientConfig

    override suspend fun getPublishedConfig(): ClientConfig {
        return getClientConfig()
    }
}
