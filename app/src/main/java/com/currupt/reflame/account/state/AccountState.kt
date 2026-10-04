package com.currupt.reflame.account.state

import com.currupt.reflame.account.model.Account

sealed interface AccountState {
    data object Loading : AccountState
    data object Unauthenticated : AccountState
    data class Authenticated(val account: Account) : AccountState
    data class Error(val message: String) : AccountState
}
