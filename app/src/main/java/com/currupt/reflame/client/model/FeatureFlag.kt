package com.currupt.reflame.client.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlag(
    val key: String,
    val enabled: Boolean = false
)
