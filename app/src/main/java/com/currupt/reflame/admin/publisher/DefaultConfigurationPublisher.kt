package com.currupt.reflame.admin.publisher

import com.currupt.reflame.admin.model.ConfigEnvelope
import com.currupt.reflame.admin.model.ConfigLifecycleStatus
import com.currupt.reflame.admin.model.PublishResult
import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.client.model.ClientConfigValidator
import com.currupt.reflame.client.model.ConfigMetadata
import com.currupt.reflame.client.model.ConfigValidationResult
import java.util.UUID

class DefaultConfigurationPublisher(
    private val validator: ClientConfigValidator = ClientConfigValidator
) : ConfigurationPublisher {

    override suspend fun createDraft(
        payload: ClientConfig,
        authorId: String?
    ): ConfigEnvelope {
        val now = System.currentTimeMillis()
        val draftId = "draft_${UUID.randomUUID()}"
        return ConfigEnvelope(
            configId = draftId,
            version = payload.metadata.version,
            status = ConfigLifecycleStatus.DRAFT,
            createdAt = now,
            updatedAt = now,
            publishedAt = null,
            authorId = authorId,
            payload = payload
        )
    }

    override suspend fun validateDraft(draft: ConfigEnvelope): PublishResult {
        return when (val validation = validator.validate(draft.payload)) {
            is ConfigValidationResult.Valid -> {
                val now = System.currentTimeMillis()
                val validatedDraft = draft.copy(
                    status = ConfigLifecycleStatus.VALIDATED,
                    updatedAt = now
                )
                PublishResult.Success(validatedDraft)
            }
            is ConfigValidationResult.Invalid -> {
                PublishResult.ValidationFailure(validation.reason)
            }
        }
    }

    override suspend fun publishDraft(
        draft: ConfigEnvelope,
        currentLiveVersion: Long,
        authorId: String?
    ): PublishResult {
        when (val validation = validator.validate(draft.payload)) {
            is ConfigValidationResult.Invalid -> {
                return PublishResult.ValidationFailure(validation.reason)
            }
            is ConfigValidationResult.Valid -> { /* valid */ }
        }

        val now = System.currentTimeMillis()
        val nextVersion = maxOf(currentLiveVersion + 1L, draft.version + 1L)

        val updatedPayload = draft.payload.copy(
            metadata = ConfigMetadata(
                version = nextVersion,
                publishedAt = now,
                configId = draft.configId
            )
        )

        val publishedEnvelope = draft.copy(
            version = nextVersion,
            status = ConfigLifecycleStatus.PUBLISHED,
            updatedAt = now,
            publishedAt = now,
            authorId = authorId ?: draft.authorId,
            payload = updatedPayload
        )

        return PublishResult.Success(publishedEnvelope)
    }
}
