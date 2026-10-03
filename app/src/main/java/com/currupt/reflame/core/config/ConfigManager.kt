package com.currupt.reflame.core.config

import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.remote.repository.DefaultRemoteConfigRepository
import com.currupt.reflame.remote.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfigManager(
    private val repository: RemoteConfigRepository = DefaultRemoteConfigRepository()
) {
    private val _config = MutableStateFlow(ClientDefaults.DEFAULT_CLIENT_CONFIG)
    val config: StateFlow<ClientConfig> = _config.asStateFlow()

    suspend fun loadConfiguration(): Boolean {
        return try {
            val result = repository.fetchClientConfig()
            result.fold(
                onSuccess = { fetchedConfig ->
                    _config.value = fetchedConfig
                    true
                },
                onFailure = {
                    // Retain previous/default configuration safely on failure
                    false
                }
            )
        } catch (_: Throwable) {
            // Safe guard against unexpected runtime exceptions
            false
        }
    }
}
