package com.currupt.reflame.remote.data

import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.core.config.ClientDefaults

class LocalRemoteConfigDataSource : RemoteConfigDataSource {
    override suspend fun getClientConfig(): ClientConfig {
        return ClientDefaults.DEFAULT_CLIENT_CONFIG
    }
}
