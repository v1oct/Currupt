package com.currupt.reflame.remote.data

import com.currupt.reflame.client.model.ClientConfig

interface PublishedConfigDataSource {
    suspend fun getPublishedConfig(): ClientConfig
}
