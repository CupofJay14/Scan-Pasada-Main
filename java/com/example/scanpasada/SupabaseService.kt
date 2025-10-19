package com.example.scanpasada

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SupabaseService {

	fun signIn(emailArg: String, passwordArg: String): UserInfo = runBlocking {
		withContext(Dispatchers.IO) {
			val client = SupabaseProvider.getClient()
			client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
				this.email = emailArg
				this.password = passwordArg
			}
			client.auth.retrieveUserForCurrentSession()
		}
	}

	fun signUp(emailArg: String, passwordArg: String): UserInfo = runBlocking {
		withContext(Dispatchers.IO) {
			val client = SupabaseProvider.getClient()
			client.auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
				this.email = emailArg
				this.password = passwordArg
			}
			// Ensure a session exists; if the project requires email confirmation,
			// signIn will still fail until confirmed, which surfaces a clearer error.
			client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
				this.email = emailArg
				this.password = passwordArg
			}
			client.auth.retrieveUserForCurrentSession()
		}
	}

	fun signUpOnly(emailArg: String, passwordArg: String) = runBlocking {
		withContext(Dispatchers.IO) {
			val client = SupabaseProvider.getClient()
			client.auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
				this.email = emailArg
				this.password = passwordArg
			}
		}
	}

	// Check if user exists in auth.users and has confirmed email
	fun checkUserConfirmed(email: String): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Checking if user exists and is confirmed: $email")
				val client = SupabaseProvider.getClient()
				
				// Check auth.users table
				val response = client.postgrest["auth.users"]
					.select {
						filter {
							eq("email", email)
						}
					}
					.decodeList<Map<String, Any>>()
				
				if (response.isNotEmpty()) {
					val user = response.first()
					val confirmedAt = user["email_confirmed_at"]
					android.util.Log.d("SupabaseService", "User found in auth.users, confirmed_at: $confirmedAt")
					
					// Only consider confirmed if email_confirmed_at is not null
					return@withContext confirmedAt != null
				}
				
				android.util.Log.d("SupabaseService", "User not found in auth.users")
				false
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error checking user confirmation: ${e.message}")
				false
			}
		}
	}
	
	// Get user ID from auth.users table by email
	fun getUserIdFromAuth(email: String): String? = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Getting user ID for email: $email")
				val client = SupabaseProvider.getClient()
				
				// Check auth.users table
				val response = client.postgrest["auth.users"]
					.select {
						filter {
							eq("email", email)
						}
					}
					.decodeList<Map<String, Any>>()
				
				if (response.isNotEmpty()) {
					val user = response.first()
					val userId = user["id"] as? String
					android.util.Log.d("SupabaseService", "Found user ID: $userId")
					return@withContext userId
				}
				
				android.util.Log.d("SupabaseService", "User ID not found")
				null
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error getting user ID: ${e.message}")
				null
			}
		}
	}

	fun insertOperator(userId: String, fullName: String, phone: String, terminalName: String, terminalLocation: String, email: String? = null) = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Inserting operator with userId: $userId")
				
				// Use a mutable variable for the user ID
				var finalUserId = userId
				
				// If email is provided, check if user is confirmed
				if (email != null) {
					val isConfirmed = checkUserConfirmed(email)
					if (!isConfirmed) {
						android.util.Log.e("SupabaseService", "Cannot insert operator: Email not confirmed")
						throw Exception("Email not confirmed. Please check your email and click the confirmation link before proceeding.")
					}
					
					// get user ID from auth.users if needed
					if (finalUserId.isEmpty()) {
						val authUserId = getUserIdFromAuth(email)
						if (authUserId != null) {
							android.util.Log.d("SupabaseService", "Using auth user ID: $authUserId")
							finalUserId = authUserId
						} else {
							throw Exception("User not found in auth.users table")
						}
					}
				}
				
				val payload = mapOf(
					"user_id" to finalUserId,
					"full_name" to fullName,
					"phone" to phone,
					"terminal_name" to terminalName,
					"terminal_location" to terminalLocation
				)
				val result = SupabaseProvider.getClient().postgrest["operators"].insert(payload).decodeSingle<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Operator inserted successfully: $result")
				result
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error inserting operator: ${e.message}", e)
				throw e
			}
		}
	}

	fun insertDriver(userId: String, fullName: String, phone: String, licenseNumber: String, expiryDate: String, plateNumber: String, email: String? = null) = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Inserting driver with userId: $userId")
				
				// Use a mutable variable for the user ID
				var finalUserId = userId
				
				// If email is provided, check if user is confirmed
				if (email != null) {
					val isConfirmed = checkUserConfirmed(email)
					if (!isConfirmed) {
						android.util.Log.e("SupabaseService", "Cannot insert driver: Email not confirmed")
						throw Exception("Email not confirmed. Please check your email and click the confirmation link before proceeding.")
					}
					
					// Get user ID from auth.users if needed
					if (finalUserId.isEmpty()) {
						val authUserId = getUserIdFromAuth(email)
						if (authUserId != null) {
							android.util.Log.d("SupabaseService", "Using auth user ID: $authUserId")
							finalUserId = authUserId
						} else {
							throw Exception("User not found in auth.users table")
						}
					}
				}
				
				val payload = mutableMapOf<String, Any>(
					"user_id" to finalUserId,
					"full_name" to fullName,
					"phone" to phone,
					"license_number" to licenseNumber,
					"plate_number" to plateNumber,
					"latitude" to 0.0,
					"longitude" to 0.0,
					"is_online" to false
				)
				// Ensure a valid date if the column is NOT NULL in the database
				payload["expiry_date"] = if (expiryDate.isNotEmpty()) expiryDate else "2099-12-31"
				val result = SupabaseProvider.getClient().postgrest["drivers"].insert(payload).decodeSingle<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Driver inserted successfully: $result")
				result
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error inserting driver: ${e.message}", e)
				throw e
			}
		}
	}

	fun insertPassenger(userId: String, fullName: String, age: Int, address: String, email: String? = null) = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Inserting passenger with userId: $userId")
				
				// Use a mutable variable for the user ID
				var finalUserId = userId
				
				// If email is provided, check if user is confirmed
				if (email != null) {
					val isConfirmed = checkUserConfirmed(email)
					if (!isConfirmed) {
						android.util.Log.e("SupabaseService", "Cannot insert passenger: Email not confirmed")
						throw Exception("Email not confirmed. Please check your email and click the confirmation link before proceeding.")
					}
					
					// Get user ID from auth.users if needed
					if (finalUserId.isEmpty()) {
						val authUserId = getUserIdFromAuth(email)
						if (authUserId != null) {
							android.util.Log.d("SupabaseService", "Using auth user ID: $authUserId")
							finalUserId = authUserId
						} else {
							throw Exception("User not found in auth.users table")
						}
					}
				}
				
				val payload = mapOf(
					"user_id" to finalUserId,
					"full_name" to fullName,
					"age" to age,
					"address" to address
				)
				val result = SupabaseProvider.getClient().postgrest["passengers"].insert(payload).decodeSingle<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Passenger inserted successfully: $result")
				result
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error inserting passenger: ${e.message}", e)
				throw e
			}
		}
	}

	fun joinQueue(queueId: String, driverId: String, driverName: String, plateNumber: String) = runBlocking {
		withContext(Dispatchers.IO) {
			val payload = mapOf(
				"queue_id" to queueId,
				"driver_id" to driverId,
				"driver_name" to driverName,
				"plate_number" to plateNumber,
				"is_active" to true
			)
			SupabaseProvider.getClient().postgrest["queue_drivers"].insert(payload).decodeSingle<Map<String, Any>>()
		}
	}

	fun sendConfirmationEmail(email: String): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			return@withContext try {
				// Re-invoke signUpWith to trigger Supabase to resend confirmation for unconfirmed accounts
				val client = SupabaseProvider.getClient()
				client.auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
					this.email = email
					// Password is required by API but ignored on resend for existing unconfirmed users
					this.password = "temp_resend_password_123"
				}
				true
			} catch (e: Exception) {
				false
			}
		}
	}
	
	fun checkUserExists(email: String): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Checking if user exists: $email")
				val client = SupabaseProvider.getClient()
				
				// check if the user exists
				val response = client.postgrest["auth.users"]
					.select {
						filter {
							eq("email", email)
						}
					}
					.decodeList<Map<String, Any>>()
				
				val exists = response.isNotEmpty()
				android.util.Log.d("SupabaseService", "User exists check result: $exists")
				exists
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error checking if user exists: ${e.message}")
				// If we can't check, assume the user might exist
				true
			}
		}
	}
	
	fun checkEmailConfirmationStatus(email: String): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Checking email confirmation status for: $email")
				val client = SupabaseProvider.getClient()
				
				// Check if user is already logged in with this email
				val currentSession = client.auth.currentSessionOrNull()
				if (currentSession != null && currentSession.user?.email == email) {
					android.util.Log.d("SupabaseService", "User is logged in with this email - confirmed")
					return@withContext true
				}
				
				// Try to sign in with a dummy password to get error messages
				try {
					client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
						this.email = email
						this.password = "dummy_password_for_check_only"
					}
					// If sign in succeeds, email must be confirmed
					android.util.Log.d("SupabaseService", "Sign in succeeded unexpectedly")
					return@withContext true
				} catch (e: Exception) {
					val errorMsg = e.message ?: ""
					android.util.Log.d("SupabaseService", "Sign in error: $errorMsg")
					
					// If we get "Email not confirmed" error, it's not confirmed
					if (errorMsg.contains("Email not confirmed", ignoreCase = true)) {
						android.util.Log.d("SupabaseService", "Email exists but is NOT confirmed")
						return@withContext false
					}

					// Invalid credentials doesn't necessarily mean the email is confirmed
					if (errorMsg.contains("Invalid login credentials", ignoreCase = true)) {
						android.util.Log.d("SupabaseService", "Invalid credentials - NOT treating as confirmed")
						return@withContext false
					}
				}
				
				// Try password reset as a final confirmation test
				try {
					client.auth.resetPasswordForEmail(email)
					// If we here without exception, the email exists and is confirmed
					android.util.Log.d("SupabaseService", "Password reset initiated - email must be confirmed")
					return@withContext true
				} catch (e: Exception) {
					android.util.Log.d("SupabaseService", "Password reset error: ${e.message}")
				}
				
				// If couldn't determine status, assume not confirmed for safety
				android.util.Log.d("SupabaseService", "Could not determine email confirmation status - assuming not confirmed")
				return@withContext false
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error checking email confirmation status: ${e.message}")
				e.printStackTrace()
				return@withContext false
			}
		}
	}

	fun checkUserExistsInTable(userId: String, userType: String): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Checking user existence for userId: $userId, userType: $userType")
				val client = SupabaseProvider.getClient()
				
				// Try to fetch the user from the appropriate table
				val result = when (userType) {
					"driver" -> {
						client.postgrest["drivers"].select().decodeList<Map<String, Any>>()
					}
					"passenger" -> {
						client.postgrest["passengers"].select().decodeList<Map<String, Any>>()
					}
					"operator" -> {
						client.postgrest["operators"].select().decodeList<Map<String, Any>>()
					}
					else -> emptyList()
				}
				
				// Check if any record has the matching user_id
				val userExists = result.any { it["user_id"] == userId }
				android.util.Log.d("SupabaseService", "User exists in table: $userExists")
				userExists
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error checking user existence: ${e.message}", e)
				false
			}
		}
	}

	fun getUserNameFromTable(userId: String, userType: String): String = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "Getting user name for userId: $userId, userType: $userType")
				val client = SupabaseProvider.getClient()
				
				// Try to fetch the user from the appropriate table
				val result = when (userType) {
					"driver" -> {
						client.postgrest["drivers"].select().decodeList<Map<String, Any>>()
					}
					"passenger" -> {
						client.postgrest["passengers"].select().decodeList<Map<String, Any>>()
					}
					"operator" -> {
						client.postgrest["operators"].select().decodeList<Map<String, Any>>()
					}
					else -> emptyList()
				}
				
				// Find the user record and get the full_name
				val userRecord = result.find { it["user_id"] == userId }
				val fullName = userRecord?.get("full_name") as? String
				
				if (fullName != null) {
					android.util.Log.d("SupabaseService", "Found user name: $fullName")
					fullName
				} else {
					android.util.Log.w("SupabaseService", "User name not found, using default")
					when (userType) {
						"driver" -> "Driver"
						"passenger" -> "Passenger"
						"operator" -> "Operator"
						else -> "User"
					}
				}
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error getting user name: ${e.message}", e)
				"User"
			}
		}
	}

	fun debugCheckAllUsers(): Unit = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "=== DEBUG: Checking all users in database ===")
				val client = SupabaseProvider.getClient()
				
				// Check drivers
				val drivers = client.postgrest["drivers"].select().decodeList<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Drivers in database: ${drivers.size}")
				drivers.forEach { driver ->
					android.util.Log.d("SupabaseService", "Driver: ${driver["user_id"]} - ${driver["full_name"]}")
				}
				
				// Check passengers
				val passengers = client.postgrest["passengers"].select().decodeList<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Passengers in database: ${passengers.size}")
				passengers.forEach { passenger ->
					android.util.Log.d("SupabaseService", "Passenger: ${passenger["user_id"]} - ${passenger["full_name"]}")
				}
				
				// Check operators
				val operators = client.postgrest["operators"].select().decodeList<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Operators in database: ${operators.size}")
				operators.forEach { operator ->
					android.util.Log.d("SupabaseService", "Operator: ${operator["user_id"]} - ${operator["full_name"]}")
				}
				
				android.util.Log.d("SupabaseService", "=== END DEBUG ===")
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Error in debug check: ${e.message}", e)
			}
		}
	}

	fun testDatabaseConnection(): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "=== TESTING DATABASE CONNECTION ===")
				android.util.Log.d("SupabaseService", "Supabase URL: ${SupabaseConfig.getSupabaseUrl()}")
				android.util.Log.d("SupabaseService", "Supabase Key: ${SupabaseConfig.getSupabaseAnonKey().take(20)}...")
				
				val client = SupabaseProvider.getClient()
				
				// Test basic connection by trying to select from each table
				android.util.Log.d("SupabaseService", "Testing drivers table...")
				val drivers = client.postgrest["drivers"].select().decodeList<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Drivers table accessible: ${drivers.size} records")
				
				android.util.Log.d("SupabaseService", "Testing passengers table...")
				val passengers = client.postgrest["passengers"].select().decodeList<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Passengers table accessible: ${passengers.size} records")
				
				android.util.Log.d("SupabaseService", "Testing operators table...")
				val operators = client.postgrest["operators"].select().decodeList<Map<String, Any>>()
				android.util.Log.d("SupabaseService", "Operators table accessible: ${operators.size} records")
				
				android.util.Log.d("SupabaseService", "=== DATABASE CONNECTION SUCCESSFUL ===")
				true
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Database connection failed: ${e.message}", e)
				android.util.Log.e("SupabaseService", "Error type: ${e.javaClass.simpleName}")
				android.util.Log.e("SupabaseService", "Error details: ${e.stackTraceToString()}")
				
				// Check for specific network errors
				when (e) {
					is java.nio.channels.UnresolvedAddressException -> {
						android.util.Log.e("SupabaseService", "❌ DNS Resolution failed - check internet connection and URL")
					}
					is java.net.UnknownHostException -> {
						android.util.Log.e("SupabaseService", "❌ Unknown host - check Supabase URL")
					}
					is java.net.ConnectException -> {
						android.util.Log.e("SupabaseService", "❌ Connection refused - check network connectivity")
					}
					is java.net.SocketTimeoutException -> {
						android.util.Log.e("SupabaseService", "❌ Connection timeout - check network speed")
					}
					else -> {
						android.util.Log.e("SupabaseService", "❌ Other network error: ${e.javaClass.simpleName}")
					}
				}
				false
			}
		}
	}

	fun testBasicNetworkConnectivity(): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "=== TESTING BASIC NETWORK CONNECTIVITY ===")
				
				// Test basic internet connectivity
				val url = java.net.URL("https://www.google.com")
				val connection = url.openConnection()
				connection.connectTimeout = 5000
				connection.readTimeout = 5000
				connection.connect()
				
				android.util.Log.d("SupabaseService", "✅ Basic internet connectivity works")
				true
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "❌ Basic internet connectivity failed: ${e.message}")
				false
			}
		}
	}

	fun testSupabaseUrlConnectivity(): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "=== TESTING SUPABASE URL CONNECTIVITY ===")
				
				val supabaseUrl = SupabaseConfig.getSupabaseUrl()
				android.util.Log.d("SupabaseService", "Testing URL: $supabaseUrl")
				
				val url = java.net.URL(supabaseUrl)
				val connection = url.openConnection()
				connection.connectTimeout = 10000
				connection.readTimeout = 10000
				connection.connect()
				
				android.util.Log.d("SupabaseService", "✅ Supabase URL is reachable")
				true
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "❌ Supabase URL connectivity failed: ${e.message}")
				android.util.Log.e("SupabaseService", "Error type: ${e.javaClass.simpleName}")
				
				// Check for SSL certificate errors
				if (e.message?.contains("CertificateException") == true || 
					e.message?.contains("checkServerTrusted") == true) {
					android.util.Log.e("SupabaseService", "❌ SSL Certificate validation failed")
					android.util.Log.e("SupabaseService", "This might be due to network security configuration")
				}
				false
			}
		}
	}

	fun testInsertOperation(userType: String, userId: String, fullName: String): Boolean = runBlocking {
		withContext(Dispatchers.IO) {
			try {
				android.util.Log.d("SupabaseService", "=== TESTING INSERT OPERATION ===")
				android.util.Log.d("SupabaseService", "UserType: $userType, UserId: $userId, FullName: $fullName")
				
				val client = SupabaseProvider.getClient()
				val result = when (userType) {
					"driver" -> {
						val payload = mapOf(
							"user_id" to userId,
							"full_name" to fullName,
							"phone" to "test-phone",
							"license_number" to "test-license",
							"plate_number" to "test-plate",
							"latitude" to 0.0,
							"longitude" to 0.0,
							"is_online" to false,
							"expiry_date" to "2099-12-31"
						)
						client.postgrest["drivers"].insert(payload).decodeSingle<Map<String, Any>>()
					}
					"passenger" -> {
						val payload = mapOf(
							"user_id" to userId,
							"full_name" to fullName,
							"age" to 25,
							"address" to "test-address"
						)
						client.postgrest["passengers"].insert(payload).decodeSingle<Map<String, Any>>()
					}
					"operator" -> {
						val payload = mapOf(
							"user_id" to userId,
							"full_name" to fullName,
							"phone" to "test-phone",
							"terminal_name" to "test-terminal",
							"terminal_location" to "test-location"
						)
						client.postgrest["operators"].insert(payload).decodeSingle<Map<String, Any>>()
					}
					else -> throw IllegalArgumentException("Invalid user type: $userType")
				}
				
				android.util.Log.d("SupabaseService", "Insert test successful: $result")
				android.util.Log.d("SupabaseService", "=== INSERT OPERATION SUCCESSFUL ===")
				true
			} catch (e: Exception) {
				android.util.Log.e("SupabaseService", "Insert test failed: ${e.message}", e)
				android.util.Log.e("SupabaseService", "Error details: ${e.stackTraceToString()}")
				false
			}
		}
	}
}


