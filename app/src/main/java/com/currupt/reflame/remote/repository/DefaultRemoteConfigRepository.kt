package com.currupt.reflame.remote.repository

import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.remote.data.LocalPublishedConfigDataSource
import com.currupt.reflame.remote.data.PublishedConfigDataSource

class DefaultRemoteConfigRepository(
    private val dataSource: PublishedConfigDataSource = LocalPublishedConfigDataSource()
) : RemoteConfigRepository {

    override suspend fun fetchClientConfig(): Result<ClientConfig> {
        return runCatching {
            dataSource.getPublishedConfig()
        }
    }
}
