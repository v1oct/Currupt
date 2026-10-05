package com.currupt.reflame.client.model

import kotlinx.serialization.Serializable

@Serializable
enum class OperationalMode {
    NORMAL,
    MAINTENANCE,
    DOWNTIME,
    EMERGENCY,
    UPDATE_REQUIRED
}
