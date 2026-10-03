package com.currupt.reflame.core

import com.currupt.reflame.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Centralized Supabase client provider for CURRUPT. Studio.
 * 
 * Loaded from local.properties via BuildConfig.
 */
object Supabase {
    
    val PROJECT_URL = BuildConfig.SUPABASE_URL
    val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    val client = createSupabaseClient(
        supabaseUrl = PROJECT_URL,
        supabaseKey = ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
