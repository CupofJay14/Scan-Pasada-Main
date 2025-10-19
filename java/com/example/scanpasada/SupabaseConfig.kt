package com.example.scanpasada

object SupabaseConfig {
    private const val SUPABASE_URL = "https://ieewhtukdhvmiayeebid.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImllZXdodHVrZGh2bWlheWVlYmlkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA0MDE3NTIsImV4cCI6MjA3NTk3Nzc1Mn0.1k4p0EJuzHn6P3KpwOx6nw0vJ_D9ybB9jkoo6ILf5ss"
    // Optional: backend endpoint to resend confirmation emails via service role
    private const val RESEND_CONFIRMATION_ENDPOINT = "" // e.g., https://<your-edge-func-domain>/resend-confirmation
    
    fun getSupabaseUrl(): String = SUPABASE_URL
    
    fun getSupabaseAnonKey(): String = SUPABASE_ANON_KEY
    
    fun getResendConfirmationEndpoint(): String = RESEND_CONFIRMATION_ENDPOINT
}
