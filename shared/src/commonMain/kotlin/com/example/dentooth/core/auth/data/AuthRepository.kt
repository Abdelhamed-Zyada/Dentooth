package com.example.dentooth.core.auth.data

import com.example.dentooth.core.auth.domain.UserSession
import com.example.dentooth.core.util.Result
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentSession: StateFlow<UserSession?>
    suspend fun signIn(email: String, password: String): Result<UserSession>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    fun getSession(): UserSession?
}
