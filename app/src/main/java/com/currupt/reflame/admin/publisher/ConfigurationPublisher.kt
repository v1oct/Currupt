package com.currupt.reflame.admin.publisher

import com.currupt.reflame.admin.model.ConfigEnvelope
import com.currupt.reflame.admin.model.PublishResult
import com.currupt.reflame.client.model.ClientConfig

interface ConfigurationPublisher {
    suspend fun createDraft(
        payload: ClientConfig,
        authorId: String? = null
    ): ConfigEnvelope

    suspend fun validateDraft(
        draft: ConfigEnvelope
    ): PublishResult

    suspend fun publishDraft(
        draft: ConfigEnvelope,
        currentLiveVersion: Long,
        authorId: String? = null
    ): PublishResult
}
