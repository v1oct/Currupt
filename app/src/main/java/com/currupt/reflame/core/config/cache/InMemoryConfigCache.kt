package com.currupt.reflame.core.config.cache

import com.currupt.reflame.client.model.ClientConfig

class InMemoryConfigCache(
    initialConfig: ClientConfig? = null
) : ConfigCache {
    private var cachedConfig: ClientConfig? = initialConfig

    override suspend fun getCachedConfig(): ClientConfig? {
        return cachedConfig
    }

    override suspend fun saveConfig(config: ClientConfig): Boolean {
        cachedConfig = config
        return true
    }

    override suspend fun clearCache(): Boolean {
        cachedConfig = null
        return true
    }
}
