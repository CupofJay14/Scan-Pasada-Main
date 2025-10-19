package com.example.scanpasada

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig

object SupabaseProvider {
	@Volatile
	private var client: SupabaseClient? = null

	fun getClient(): SupabaseClient {
		val existing = client
		if (existing != null) return existing
		return synchronized(this) {
			val again = client
			if (again != null) again else createClient()
		}
	}

	private fun createClient(): SupabaseClient {
		val created = createSupabaseClient(
			SupabaseConfig.getSupabaseUrl(),
			SupabaseConfig.getSupabaseAnonKey()
		) {
			install(Auth)
			install(io.github.jan.supabase.postgrest.Postgrest)
			install(io.github.jan.supabase.realtime.Realtime)
			httpEngine = CIO.create()
		}
		client = created
		return created
	}
}


