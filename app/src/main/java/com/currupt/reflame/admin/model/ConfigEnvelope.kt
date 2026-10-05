package com.currupt.reflame.admin.model

import com.currupt.reflame.client.model.ClientConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ConfigLifecycleStatus {
    DRAFT,
    VALIDATED,
    PUBLISHED,
    ARCHIVED
}

@Serializable
data class ConfigEnvelope(
    @SerialName("config_id") val configId: String,
    val version: Long = 1L,
    val status: ConfigLifecycleStatus = ConfigLifecycleStatus.DRAFT,
    @SerialName("created_at") val createdAt: Long = 0L,
    @SerialName("updated_at") val updatedAt: Long = 0L,
    @SerialName("published_at") val publishedAt: Long? = null,
    @SerialName("author_id") val authorId: String? = null,
    val payload: ClientConfig = ClientConfig()
)

@Serializable
sealed interface PublishResult {
    data class Success(val publishedEnvelope: ConfigEnvelope) : PublishResult
    data class ValidationFailure(val reason: String) : PublishResult
    data class Error(val message: String) : PublishResult
}
