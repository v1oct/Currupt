package com.currupt.reflame.account.repository

import com.currupt.reflame.account.model.Account

interface AccountRepository {
    suspend fun getCurrentAccount(): Result<Account?>
    suspend fun clearAccount()
}
