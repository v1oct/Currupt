package com.currupt.reflame.remote.repository

import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.remote.data.LocalRemoteConfigDataSource
import com.currupt.reflame.remote.data.RemoteConfigDataSource

class DefaultRemoteConfigRepository(
    private val dataSource: RemoteConfigDataSource = LocalRemoteConfigDataSource()
) : RemoteConfigRepository {

    override suspend fun fetchClientConfig(): Result<ClientConfig> {
        return runCatching {
            dataSource.getClientConfig()
        }
    }
}
