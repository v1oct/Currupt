package com.currupt.reflame.admin.data

import com.currupt.reflame.admin.model.ConfigEnvelope
import com.currupt.reflame.admin.model.ConfigLifecycleStatus
import com.currupt.reflame.admin.model.PublishResult
import com.currupt.reflame.admin.publisher.ConfigurationPublisher
import com.currupt.reflame.admin.publisher.DefaultConfigurationPublisher
import com.currupt.reflame.client.model.ClientConfig

class LocalAdminConfigurationDataSource(
    private val publisher: ConfigurationPublisher = DefaultConfigurationPublisher()
) : AdminConfigurationDataSource {

    private val draftStore = mutableMapOf<String, ConfigEnvelope>()
    private var currentLiveVersion: Long = 1L

    override suspend fun createDraft(
        payload: ClientConfig,
        authorId: String?
    ): ConfigEnvelope {
        val draft = publisher.createDraft(payload, authorId)
        draftStore[draft.configId] = draft
        return draft
    }

    override suspend fun updateDraft(draft: ConfigEnvelope): ConfigEnvelope {
        val updated = draft.copy(
            updatedAt = System.currentTimeMillis()
        )
        draftStore[draft.configId] = updated
        return updated
    }

    override suspend fun publishDraft(
        configId: String,
        authorId: String?
    ): PublishResult {
        val draft = draftStore[configId]
            ?: return PublishResult.Error("Draft with ID '$configId' not found.")

        val result = publisher.publishDraft(
            draft = draft,
            currentLiveVersion = currentLiveVersion,
            authorId = authorId
        )

        if (result is PublishResult.Success) {
            currentLiveVersion = result.publishedEnvelope.version
            draftStore[configId] = result.publishedEnvelope
        }

        return result
    }

    override suspend fun archiveConfig(configId: String): Boolean {
        val envelope = draftStore[configId] ?: return false
        draftStore[configId] = envelope.copy(
            status = ConfigLifecycleStatus.ARCHIVED,
            updatedAt = System.currentTimeMillis()
        )
        return true
    }
}
