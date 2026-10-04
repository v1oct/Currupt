package com.currupt.reflame.account.repository

import com.currupt.reflame.account.model.Account
import com.currupt.reflame.account.model.AccountStatus
import com.currupt.reflame.account.model.UserEntitlement

class LocalAccountRepository : AccountRepository {

    private var localAccount: Account? = Account(
        id = "local_dev_account",
        discordId = null,
        displayName = "Local Developer",
        avatarUrl = null,
        status = AccountStatus.ACTIVE,
        entitlement = UserEntitlement.FREE
    )

    override suspend fun getCurrentAccount(): Result<Account?> {
        return Result.success(localAccount)
    }

    override suspend fun clearAccount() {
        localAccount = null
    }
}
