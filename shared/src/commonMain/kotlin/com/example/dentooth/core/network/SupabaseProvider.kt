package com.example.dentooth.core.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {
    // Replace with your clinic's Supabase project credentials
    private const val SUPABASE_URL = "https://xbiglikuafqfizgrarjr.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhiaWdsaWt1YWZxZml6Z3JhcmpyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTUyNzkzNjcsImV4cCI6MjExMDg1NTM2N30.U6RgO_bo_8FvgggvyF2L6frKtFii2iSyOkofIYAQ0MY"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
