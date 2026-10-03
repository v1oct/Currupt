package com.currupt.reflame.core.state

import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.client.model.ClientConfig
import com.currupt.reflame.core.config.ClientDefaults
import com.currupt.reflame.core.model.Game

data class AppState(
    val isLoading: Boolean = true,
    val clientConfig: ClientConfig = ClientDefaults.DEFAULT_CLIENT_CONFIG,
    val userEntitlement: UserEntitlement = UserEntitlement.FREE,
    val activeGame: Game? = null,
    val errorMessage: String? = null
)
