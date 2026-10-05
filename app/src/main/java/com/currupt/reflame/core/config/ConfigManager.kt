package com.currupt.reflame.core.config

import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.client.model.ClientConfigValidator
import com.currupt.reflame.client.model.ConfigValidationResult
import com.currupt.reflame.client.model.OperationalModeConfig
import com.currupt.reflame.core.config.cache.ConfigCache
import com.currupt.reflame.core.config.cache.InMemoryConfigCache
import com.currupt.reflame.remote.repository.DefaultRemoteConfigRepository
import com.currupt.reflame.remote.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class ConfigStatus {
    INITIAL_DEFAULT,
    LOADED_FROM_CACHE,
    UPDATED_REMOTE,
    REJECTED_OLDER_REMOTE_VERSION,
    VALIDATION_FAILED_RETAINED_PREVIOUS,
    NETWORK_FAILED_RETAINED_PREVIOUS,
    CORRUPT_CACHE_CLEARED_FALLBACK_DEFAULT,
    ROLLED_BACK
}

data class ConfigManagerState(
    val currentConfig: ClientConfig = ClientDefaults.DEFAULT_CLIENT_CONFIG,
    val status: ConfigStatus = ConfigStatus.INITIAL_DEFAULT,
    val lastError: String? = null
)

class ConfigManager(
    private val repository: RemoteConfigRepository = DefaultRemoteConfigRepository(),
    private val validator: ClientConfigValidator = ClientConfigValidator,
    private val cache: ConfigCache = InMemoryConfigCache()
) {
    private val syncMutex = Mutex()

    private var lastKnownGoodConfig: ClientConfig = ClientDefaults.DEFAULT_CLIENT_CONFIG

    private val _config = MutableStateFlow(ClientDefaults.DEFAULT_CLIENT_CONFIG)
    val config: StateFlow<ClientConfig> = _config.asStateFlow()

    private val _operationalModeConfig = MutableStateFlow(
        ClientDefaults.DEFAULT_CLIENT_CONFIG.getEffectiveOperationalModeConfig()
    )
    val operationalModeConfig: StateFlow<OperationalModeConfig> = _operationalModeConfig.asStateFlow()

    private val _managerState = MutableStateFlow(ConfigManagerState())
    val managerState: StateFlow<ConfigManagerState> = _managerState.asStateFlow()

    fun getLastKnownGoodConfig(): ClientConfig = lastKnownGoodConfig

    suspend fun initializeFromCache(): Boolean = syncMutex.withLock {
        val cachedConfig = cache.getCachedConfig()
        if (cachedConfig != null) {
            when (val validation = validator.validate(cachedConfig)) {
                is ConfigValidationResult.Valid -> {
                    lastKnownGoodConfig = cachedConfig
                    applyConfig(cachedConfig, ConfigStatus.LOADED_FROM_CACHE, null)
                    return true
                }
                is ConfigValidationResult.Invalid -> {
                    cache.clearCache()
                    lastKnownGoodConfig = ClientDefaults.DEFAULT_CLIENT_CONFIG
                    applyConfig(
                        ClientDefaults.DEFAULT_CLIENT_CONFIG,
                        ConfigStatus.CORRUPT_CACHE_CLEARED_FALLBACK_DEFAULT,
                        "Cached configuration was invalid: ${validation.reason}"
                    )
                    return false
                }
            }
        } else {
            return false
        }
    }

    suspend fun loadConfiguration(): Boolean = syncMutex.withLock {
        try {
            val result = repository.fetchClientConfig()
            result.fold(
                onSuccess = { fetchedConfig ->
                    when (val validation = validator.validate(fetchedConfig)) {
                        is ConfigValidationResult.Valid -> {
                            val currentVersion = lastKnownGoodConfig.metadata.version
                            val incomingVersion = fetchedConfig.metadata.version

                            if (incomingVersion < currentVersion) {
                                val msg = "Rejected remote configuration version ($incomingVersion) older than current ($currentVersion)"
                                _managerState.value = _managerState.value.copy(
                                    status = ConfigStatus.REJECTED_OLDER_REMOTE_VERSION,
                                    lastError = msg
                                )
                                return@fold false
                            }

                            cache.saveConfig(fetchedConfig)
                            lastKnownGoodConfig = fetchedConfig
                            applyConfig(fetchedConfig, ConfigStatus.UPDATED_REMOTE, null)
                            true
                        }
                        is ConfigValidationResult.Invalid -> {
                            val errorMsg = "Validation failed: ${validation.reason}"
                            _managerState.value = _managerState.value.copy(
                                status = ConfigStatus.VALIDATION_FAILED_RETAINED_PREVIOUS,
                                lastError = errorMsg
                            )
                            false
                        }
                    }
                },
                onFailure = { throwable ->
                    val errorMsg = throwable.message ?: "Failed to fetch remote configuration"
                    _managerState.value = _managerState.value.copy(
                        status = ConfigStatus.NETWORK_FAILED_RETAINED_PREVIOUS,
                        lastError = errorMsg
                    )
                    false
                }
            )
        } catch (e: Throwable) {
            _managerState.value = _managerState.value.copy(
                status = ConfigStatus.NETWORK_FAILED_RETAINED_PREVIOUS,
                lastError = e.message ?: "Unexpected runtime exception during config sync"
            )
            false
        }
    }

    suspend fun syncConfiguration(): Boolean {
        initializeFromCache()
        return loadConfiguration()
    }

    suspend fun rollbackToLastKnownGood(): Boolean = syncMutex.withLock {
        cache.saveConfig(lastKnownGoodConfig)
        applyConfig(lastKnownGoodConfig, ConfigStatus.ROLLED_BACK, null)
        return true
    }

    private fun applyConfig(newConfig: ClientConfig, status: ConfigStatus, error: String?) {
        _config.value = newConfig
        _operationalModeConfig.value = newConfig.getEffectiveOperationalModeConfig()
        _managerState.value = ConfigManagerState(
            currentConfig = newConfig,
            status = status,
            lastError = error
        )
    }
}
