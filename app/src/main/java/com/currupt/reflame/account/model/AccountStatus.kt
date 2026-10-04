package com.currupt.reflame.account.model

import kotlinx.serialization.Serializable

@Serializable
enum class AccountStatus {
    ACTIVE,
    BANNED,
    SUSPENDED
}
