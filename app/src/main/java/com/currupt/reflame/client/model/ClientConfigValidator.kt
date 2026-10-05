package com.currupt.reflame.client.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ConfigValidationResult {
    data object Valid : ConfigValidationResult
    data class Invalid(val reason: String) : ConfigValidationResult
}

object ClientConfigValidator {
    fun validate(config: ClientConfig): ConfigValidationResult {
        if (config.metadata.version < 1L) {
            return ConfigValidationResult.Invalid("Invalid configuration version: ${config.metadata.version}")
        }

        val modeConfig = config.operationalModeConfig
        if (modeConfig.isEnabled && modeConfig.mode != OperationalMode.NORMAL) {
            if (modeConfig.title.isBlank() && modeConfig.message.isBlank()) {
                return ConfigValidationResult.Invalid("Operational mode ${modeConfig.mode} enabled without title or message.")
            }
        }

        for (flag in config.featureFlags) {
            if (flag.key.isBlank()) {
                return ConfigValidationResult.Invalid("Feature flag key cannot be blank.")
            }
        }

        return ConfigValidationResult.Valid
    }
}
