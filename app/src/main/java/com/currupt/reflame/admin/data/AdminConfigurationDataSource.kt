package com.currupt.reflame.admin.data

import com.currupt.reflame.admin.model.ConfigEnvelope
import com.currupt.reflame.admin.model.PublishResult
import com.currupt.reflame.client.model.ClientConfig

interface AdminConfigurationDataSource {
    suspend fun createDraft(payload: ClientConfig, authorId: String? = null): ConfigEnvelope
    suspend fun updateDraft(draft: ConfigEnvelope): ConfigEnvelope
    suspend fun publishDraft(configId: String, authorId: String? = null): PublishResult
    suspend fun archiveConfig(configId: String): Boolean
}
