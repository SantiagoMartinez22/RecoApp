package com.upb.reco.app.data.repository

import com.upb.reco.app.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Auth vía Firebase (Email/Password) + perfil en Firestore.
 *
 * Nota: `signInWithGoogleDemo()` sigue siendo un stub.
 */
class AuthRepository(
    private val userPreferences: UserPreferences,
) {

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password).await()
                userPreferences.setUserEmail(email)
            }
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
        try {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid ?: throw Exception("No se pudo crear el usuario")

            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "genres" to genres,
                "keywords" to keywords
            )

            db.collection("users")
                .document(uid)
                .set(userData)
                .await()

            // Opcional: guardar local también
            userPreferences.setUserName(name)
            userPreferences.setUserEmail(email)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            FirebaseAuth.getInstance().signOut()
            userPreferences.clearSession()
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                Unit
            }
        }

    companion object {
        private const val DEMO_DELAY_MS = 450L
    }
}
