package com.upb.reco.app.data.model

/**
 * Modelo de perfil (Firestore usará campos equivalentes al integrar Firebase).
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val genres: List<String> = emptyList(),
    val keywords: String = "",
    val createdAtMillis: Long? = null,
)
