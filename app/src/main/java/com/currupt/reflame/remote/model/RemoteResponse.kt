package com.currupt.reflame.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val errorMessage: String? = null
)
