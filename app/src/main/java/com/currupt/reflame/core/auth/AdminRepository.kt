package com.currupt.reflame.core.auth

import com.currupt.reflame.core.Supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for checking and managing CURRUPT. Studio admin permissions.
 */
class AdminRepository {

    private val auth = Supabase.client.auth

    fun isLoggedIn(): Boolean = auth.currentUserOrNull() != null

    /**
     * Checks if the current authenticated user is an authorized admin.
     * Checks the authenticated email against the [AdminConfig.AUTHORIZED_EMAILS] allowlist.
     */
    suspend fun isCurrentUserManager(): Boolean = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUserOrNull() ?: return@withContext false
        val email = currentUser.email ?: return@withContext false
        
        // Authorization is based on the application allowlist
        AdminConfig.AUTHORIZED_EMAILS.contains(email)
    }
}
