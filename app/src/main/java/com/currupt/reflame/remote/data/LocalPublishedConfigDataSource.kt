package com.currupt.reflame.remote.data

import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.core.config.ClientDefaults

class LocalPublishedConfigDataSource : PublishedConfigDataSource {
    override suspend fun getPublishedConfig(): ClientConfig {
        return ClientDefaults.DEFAULT_CLIENT_CONFIG
    }
}
