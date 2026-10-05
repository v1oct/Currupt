package com.currupt.reflame.client.runtime

import kotlinx.serialization.Serializable

@Serializable
enum class ClientRuntimeStatus {
    NOT_STARTED,
    STARTING,
    RUNNING,
    STOPPING,
    ERROR
}

@Serializable
enum class PermissionStatus {
    GRANTED,
    REQUIRED
}

data class ClientRuntimeState(
    val status: ClientRuntimeStatus = ClientRuntimeStatus.NOT_STARTED,
    val permissionStatus: PermissionStatus = PermissionStatus.GRANTED,
    val errorMessage: String? = null
)
