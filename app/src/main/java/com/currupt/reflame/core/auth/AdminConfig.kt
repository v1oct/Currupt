package com.currupt.reflame.core.auth

/**
 * Centralized configuration for CURRUPT. Studio administrators.
 */
object AdminConfig {
    /**
     * List of emails authorized to access the Admin CMS and perform secure operations.
     * 
     * IMPORTANT: 
     * 1. Add your admin email here.
     * 2. Ensure this email is also added to the 'admin_allowlist' table in Supabase.
     */
    val AUTHORIZED_EMAILS = setOf(
        "admin@currupt.studio", // Placeholder: Replace with your actual admin email
        "ayushv1ct@gmail.com"
    )
}
