package com.currupt.reflame.admin.model

import kotlinx.serialization.Serializable

@Serializable
enum class AdminPermission {
    OWNER,
    ADMIN,
    DEVELOPER,
    MODERATOR
}
