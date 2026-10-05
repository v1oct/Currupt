package com.currupt.reflame.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigMetadata(
    val version: Long = 1L,
    @SerialName("published_at") val publishedAt: Long = 0L,
    @SerialName("config_id") val configId: String? = null
)
