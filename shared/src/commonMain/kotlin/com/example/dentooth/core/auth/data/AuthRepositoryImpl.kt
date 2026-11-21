package com.example.dentooth.core.auth.data

import com.example.dentooth.core.auth.domain.UserRole
import com.example.dentooth.core.auth.domain.UserSession
import com.example.dentooth.core.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl : AuthRepository {

    // Default pre-authenticated dev session for seamless development & testing
    private val defaultDevSession = UserSession(
        id = "usr-001",
        email = "dr.ahmed@dentobrain.app",
        fullName = "Dr. Ahmed Hassan",
        role = UserRole.ADMIN,
        clinicId = "cln-001",
        branchId = "brn-001"
    )

    private val _currentSession = MutableStateFlow<UserSession?>(defaultDevSession)
    override val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    override suspend fun signIn(email: String, password: String): Result<UserSession> {
        val trimmedEmail = email.trim().lowercase()
        return try {
            // Determine role and account for fast, robust dev/testing workflow
            val session = when {
                trimmedEmail.contains("reception") -> UserSession(
                    id = "usr-002",
                    email = trimmedEmail,
                    fullName = "Sarah Mohamed",
                    role = UserRole.RECEPTIONIST,
                    clinicId = "cln-001",
                    branchId = "brn-001"
                )
                trimmedEmail.contains("account") -> UserSession(
                    id = "usr-003",
                    email = trimmedEmail,
                    fullName = "Tarek Nabil",
                    role = UserRole.ACCOUNTANT,
                    clinicId = "cln-001",
                    branchId = "brn-001"
                )
                else -> UserSession(
                    id = "usr-001",
                    email = trimmedEmail.ifEmpty { "dr.ahmed@dentobrain.app" },
                    fullName = "Dr. Ahmed Hassan",
                    role = UserRole.ADMIN,
                    clinicId = "cln-001",
                    branchId = "brn-001"
                )
            }
            _currentSession.value = session
            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Authentication failed", e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        _currentSession.value = null
        return Result.Success(Unit)
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return Result.Success(Unit)
    }

    override fun getSession(): UserSession? = _currentSession.value
}
