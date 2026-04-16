package com.reco.app.data.repository

import com.reco.app.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Implementación **sin Firebase** para compilar y probar flujo UI/navegación.
 * Cuando configures Firebase, reemplaza este repositorio por llamadas reales a Auth + Firestore
 * (y vuelve a añadir el plugin `com.google.gms.google-services` y dependencias en `app/build.gradle.kts`).
 */
class AuthRepository(
    private val userPreferences: UserPreferences,
) {

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            delay(DEMO_DELAY_MS)
            userPreferences.setUserEmail(email)
            Result.success(Unit)
        }

    /** Simula “Iniciar sesión con Google” sin Play Services / Firebase. */
    suspend fun signInWithGoogleDemo(): Result<Unit> =
        withContext(Dispatchers.IO) {
            delay(DEMO_DELAY_MS)
            Result.success(Unit)
        }

    suspend fun registerWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        genres: List<String>,
        keywords: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        delay(DEMO_DELAY_MS)
        userPreferences.setUserName(name)
        userPreferences.setUserEmail(email)
        Result.success(Unit)
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        delay(DEMO_DELAY_MS)
        userPreferences.clearSession()
        Result.success(Unit)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            delay(DEMO_DELAY_MS)
            Result.success(Unit)
        }

    companion object {
        private const val DEMO_DELAY_MS = 450L
    }
}
