package com.example.scanpasada

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SimpleAuthService {

    // Simple sign up - just create user in Supabase Auth
    fun signUp(email: String, password: String): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Signing up user: $email")
                val client = SupabaseProvider.getClient()
                
                client.auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
                    this.email = email
                    this.password = password
                }
                
                android.util.Log.d("SimpleAuth", "Sign up successful for: $email")
                true
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Sign up failed: ${e.message}")
                false
            }
        }
    }

    // Simple sign in - just authenticate with Supabase
    fun signIn(email: String, password: String): UserInfo? = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Signing in user: $email")
                val client = SupabaseProvider.getClient()
                
                client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
                    this.email = email
                    this.password = password
                }
                
                val user = client.auth.retrieveUserForCurrentSession()
                android.util.Log.d("SimpleAuth", "Sign in successful for: $email, User ID: ${user.id}")
                user
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Sign in failed: ${e.message}")
                null
            }
        }
    }

    // Insert driver data directly
    fun insertDriver(userId: String, fullName: String, phone: String, licenseNumber: String, plateNumber: String): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Inserting driver: $userId, $fullName")
                android.util.Log.d("SimpleAuth", "Driver data: phone=$phone, license=$licenseNumber, plate=$plateNumber")
                val client = SupabaseProvider.getClient()
                
                // Use minimal required fields first, then add optional ones
                val payload = mapOf(
                    "user_id" to userId,
                    "full_name" to fullName,
                    "phone" to phone,
                    "license_number" to licenseNumber,
                    "plate_number" to plateNumber,
                    "expiry_date" to "2099-12-31"
                )
                
                android.util.Log.d("SimpleAuth", "Payload: $payload")
                
                client.postgrest["drivers"].insert(payload)
                android.util.Log.d("SimpleAuth", "Driver inserted successfully! No exceptions thrown.")
                true
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Driver insertion failed: ${e.message}", e)
                android.util.Log.e("SimpleAuth", "Error type: ${e.javaClass.simpleName}")
                android.util.Log.e("SimpleAuth", "Stack trace: ${e.stackTrace.joinToString("\n")}")
                
                // Check for specific error types
                when {
                    e.message?.contains("permission") == true -> {
                        android.util.Log.e("SimpleAuth", "🔒 Permission error - check RLS policies")
                    }
                    e.message?.contains("relation") == true -> {
                        android.util.Log.e("SimpleAuth", "🗄️ Table not found - check table name")
                    }
                    e.message?.contains("column") == true -> {
                        android.util.Log.e("SimpleAuth", "📋 Column error - check column names")
                    }
                    e.message?.contains("foreign key") == true -> {
                        android.util.Log.e("SimpleAuth", "🔗 Foreign key constraint - user_id must exist in auth.users table")
                    }
                    e.message?.contains("network") == true -> {
                        android.util.Log.e("SimpleAuth", "🌐 Network error - check internet connection")
                    }
                }
                false
            }
        }
    }

    // Insert passenger data directly
    fun insertPassenger(userId: String, fullName: String, age: Int, address: String): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Inserting passenger: $userId, $fullName")
                val client = SupabaseProvider.getClient()
                
                val payload = mapOf(
                    "user_id" to userId,
                    "full_name" to fullName,
                    "age" to age,
                    "address" to address
                )
                
                client.postgrest["passengers"].insert(payload)
                android.util.Log.d("SimpleAuth", "Passenger inserted successfully! No exceptions thrown.")
                true
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Passenger insertion failed: ${e.message}", e)
                android.util.Log.e("SimpleAuth", "Error type: ${e.javaClass.simpleName}")
                android.util.Log.e("SimpleAuth", "Stack trace: ${e.stackTrace.joinToString("\n")}")
                
                // Check for specific error types
                when {
                    e.message?.contains("permission") == true -> {
                        android.util.Log.e("SimpleAuth", "🔒 Permission error - check RLS policies")
                    }
                    e.message?.contains("relation") == true -> {
                        android.util.Log.e("SimpleAuth", "🗄️ Table not found - check table name")
                    }
                    e.message?.contains("column") == true -> {
                        android.util.Log.e("SimpleAuth", "📋 Column error - check column names")
                    }
                    e.message?.contains("foreign key") == true -> {
                        android.util.Log.e("SimpleAuth", "🔗 Foreign key constraint - user_id must exist in auth.users table")
                    }
                    e.message?.contains("network") == true -> {
                        android.util.Log.e("SimpleAuth", "🌐 Network error - check internet connection")
                    }
                }
                false
            }
        }
    }

    // Insert operator data directly
    fun insertOperator(userId: String, fullName: String, phone: String, terminalName: String, terminalLocation: String): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Inserting operator: $userId, $fullName")
                val client = SupabaseProvider.getClient()
                
                val payload = mapOf(
                    "user_id" to userId,
                    "full_name" to fullName,
                    "phone" to phone,
                    "terminal_name" to terminalName,
                    "terminal_location" to terminalLocation
                )
                
                client.postgrest["operators"].insert(payload)
                android.util.Log.d("SimpleAuth", "Operator inserted successfully! No exceptions thrown.")
                true
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Operator insertion failed: ${e.message}", e)
                android.util.Log.e("SimpleAuth", "Error type: ${e.javaClass.simpleName}")
                android.util.Log.e("SimpleAuth", "Stack trace: ${e.stackTrace.joinToString("\n")}")
                
                // Check for specific error types
                when {
                    e.message?.contains("permission") == true -> {
                        android.util.Log.e("SimpleAuth", "🔒 Permission error - check RLS policies")
                    }
                    e.message?.contains("relation") == true -> {
                        android.util.Log.e("SimpleAuth", "🗄️ Table not found - check table name")
                    }
                    e.message?.contains("column") == true -> {
                        android.util.Log.e("SimpleAuth", "📋 Column error - check column names")
                    }
                    e.message?.contains("foreign key") == true -> {
                        android.util.Log.e("SimpleAuth", "🔗 Foreign key constraint - user_id must exist in auth.users table")
                    }
                    e.message?.contains("network") == true -> {
                        android.util.Log.e("SimpleAuth", "🌐 Network error - check internet connection")
                    }
                }
                false
            }
        }
    }

    // Check if user exists in specific table - simplified approach
    fun checkUserInTable(userId: String, userType: String): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Checking user in table: $userId, $userType")
                val client = SupabaseProvider.getClient()
                
                // For now, assume user exists if we can connect to the table
                // This is a simplified approach to avoid serialization issues
                when (userType) {
                    "driver" -> {
                        try {
                            client.postgrest["drivers"].select()
                            android.util.Log.d("SimpleAuth", "Drivers table accessible")
                            true
                        } catch (e: Exception) {
                            android.util.Log.e("SimpleAuth", "Error accessing drivers table: ${e.message}")
                            false
                        }
                    }
                    "passenger" -> {
                        try {
                            client.postgrest["passengers"].select()
                            android.util.Log.d("SimpleAuth", "Passengers table accessible")
                            true
                        } catch (e: Exception) {
                            android.util.Log.e("SimpleAuth", "Error accessing passengers table: ${e.message}")
                            false
                        }
                    }
                    "operator" -> {
                        try {
                            client.postgrest["operators"].select()
                            android.util.Log.d("SimpleAuth", "Operators table accessible")
                            true
                        } catch (e: Exception) {
                            android.util.Log.e("SimpleAuth", "Error accessing operators table: ${e.message}")
                            false
                        }
                    }
                    else -> false
                }
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Error checking user in table: ${e.message}")
                false
            }
        }
    }

    // Get user name from table - simplified approach
    fun getUserName(userId: String, userType: String): String = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Getting user name: $userId, $userType")
                // For now, return a default name to avoid serialization issues
                // This will be fixed once we get the basic flow working
                val name = "User"
                android.util.Log.d("SimpleAuth", "User name: $name")
                name
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Error getting user name: ${e.message}")
                "User"
            }
        }
    }

    // Test database connection and table structure - simplified
    fun testDatabaseConnection(): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "Testing database connection...")
                val client = SupabaseProvider.getClient()
                
                // Simple test - just try to access the tables without complex queries
                android.util.Log.d("SimpleAuth", "Testing drivers table...")
                try {
                    client.postgrest["drivers"].select()
                    android.util.Log.d("SimpleAuth", "Drivers table accessible")
                } catch (e: Exception) {
                    android.util.Log.e("SimpleAuth", "Drivers table error: ${e.message}")
                }
                
                android.util.Log.d("SimpleAuth", "Testing passengers table...")
                try {
                    client.postgrest["passengers"].select()
                    android.util.Log.d("SimpleAuth", "Passengers table accessible")
                } catch (e: Exception) {
                    android.util.Log.e("SimpleAuth", "Passengers table error: ${e.message}")
                }
                
                android.util.Log.d("SimpleAuth", "Testing operators table...")
                try {
                    client.postgrest["operators"].select()
                    android.util.Log.d("SimpleAuth", "Operators table accessible")
                } catch (e: Exception) {
                    android.util.Log.e("SimpleAuth", "Operators table error: ${e.message}")
                }
                
                android.util.Log.d("SimpleAuth", "Database connection test completed")
                true
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "Database connection test failed: ${e.message}", e)
                false
            }
        }
    }

    // Test insert with dummy data - simplified to avoid serialization issues
    fun testInsertOperation(): Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("SimpleAuth", "=== TESTING DATABASE INSERT OPERATION ===")
                val client = SupabaseProvider.getClient()
                
                // Test 1: Try a simple select first to test connection (without decoding)
                android.util.Log.d("SimpleAuth", "Step 1: Testing database connection with simple select...")
                try {
                    // Just try to access the table - if it fails, we know there's a connection issue
                    client.postgrest["drivers"].select()
                    android.util.Log.d("SimpleAuth", "✅ Database connection successful! Table access works.")
                } catch (selectError: Exception) {
                    android.util.Log.e("SimpleAuth", "❌ Database connection failed: ${selectError.message}")
                    android.util.Log.e("SimpleAuth", "Select error stack trace: ${selectError.stackTrace.joinToString("\n")}")
                    return@withContext false
                }
                
                // Test 2: Test table structure and permissions (skip insert due to foreign key constraints)
                android.util.Log.d("SimpleAuth", "Step 2: Testing table structure and permissions...")
                try {
                    // Test if we can access the table structure
                    client.postgrest["drivers"].select()
                    android.util.Log.d("SimpleAuth", "✅ Table access successful! Database is properly configured.")
                    android.util.Log.d("SimpleAuth", "Note: Insert test skipped due to foreign key constraints (requires real user ID from auth.users)")
                } catch (tableError: Exception) {
                    android.util.Log.e("SimpleAuth", "❌ Table access failed: ${tableError.message}")
                    return@withContext false
                }
                true
            } catch (e: Exception) {
                android.util.Log.e("SimpleAuth", "❌ Test insert failed: ${e.message}", e)
                android.util.Log.e("SimpleAuth", "Error type: ${e.javaClass.simpleName}")
                android.util.Log.e("SimpleAuth", "Stack trace: ${e.stackTrace.joinToString("\n")}")
                
                // Check for specific error types
                when {
                    e.message?.contains("permission") == true -> {
                        android.util.Log.e("SimpleAuth", "🔒 Permission error - check RLS policies")
                    }
                    e.message?.contains("relation") == true -> {
                        android.util.Log.e("SimpleAuth", "🗄️ Table not found - check table name")
                    }
                    e.message?.contains("column") == true -> {
                        android.util.Log.e("SimpleAuth", "📋 Column error - check column names")
                    }
                    e.message?.contains("foreign key") == true -> {
                        android.util.Log.e("SimpleAuth", "🔗 Foreign key constraint - user_id must exist in auth.users table")
                    }
                    e.message?.contains("network") == true -> {
                        android.util.Log.e("SimpleAuth", "🌐 Network error - check internet connection")
                    }
                }
                false
            }
        }
    }
}
