package com.currupt.reflame.account

import com.currupt.reflame.account.model.UserEntitlement
import com.currupt.reflame.account.repository.AccountRepository
import com.currupt.reflame.account.repository.LocalAccountRepository
import com.currupt.reflame.account.state.AccountState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountManager(
    private val repository: AccountRepository = LocalAccountRepository()
) {
    private val _state = MutableStateFlow<AccountState>(AccountState.Loading)
    val state: StateFlow<AccountState> = _state.asStateFlow()

    suspend fun loadAccountState() {
        _state.value = AccountState.Loading
        repository.getCurrentAccount().fold(
            onSuccess = { account ->
                if (account != null) {
                    _state.value = AccountState.Authenticated(account)
                } else {
                    _state.value = AccountState.Unauthenticated
                }
            },
            onFailure = { error ->
                _state.value = AccountState.Error(error.message ?: "Failed to load account state")
            }
        )
    }

    suspend fun clearCurrentAccount() {
        repository.clearAccount()
        _state.value = AccountState.Unauthenticated
    }

    fun getCurrentEntitlement(): UserEntitlement {
        val currentState = _state.value
        return if (currentState is AccountState.Authenticated) {
            currentState.account.entitlement
        } else {
            UserEntitlement.FREE
        }
    }
}
