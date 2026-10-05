package com.currupt.reflame.core.config.cache

import com.currupt.reflame.client.model.ClientConfig

interface ConfigCache {
    suspend fun getCachedConfig(): ClientConfig?
    suspend fun saveConfig(config: ClientConfig): Boolean
    suspend fun clearCache(): Boolean
}
