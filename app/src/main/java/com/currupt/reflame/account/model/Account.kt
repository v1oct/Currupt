package com.currupt.reflame.account.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    @SerialName("discord_id") val discordId: String? = null,
    @SerialName("display_name") val displayName: String = "",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val status: AccountStatus = AccountStatus.ACTIVE,
    val entitlement: UserEntitlement = UserEntitlement.FREE
)
