package com.currupt.reflame.remote.data

import com.currupt.reflame.client.model.ClientConfig

interface RemoteConfigDataSource {
    suspend fun getClientConfig(): ClientConfig
}
