package com.reco.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferences(private val context: Context) {

    val userName: Flow<String> = context.recoDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs -> prefs[USER_NAME_KEY].orEmpty().ifBlank { "tú" } }

    val userEmail: Flow<String> = context.recoDataStore.data
        .map { prefs -> prefs[USER_EMAIL_KEY].orEmpty() }

    val favoritesIds: Flow<Set<String>> = context.recoDataStore.data
        .map { prefs -> prefs[FAVORITES_IDS_KEY] ?: emptySet() }

    val platformSubscriptions: Flow<Set<String>> = context.recoDataStore.data
        .map { prefs -> prefs[PLATFORM_SUBSCRIPTIONS_KEY] ?: defaultPlatformLabels }

    val notifRecommendations: Flow<Boolean> = context.recoDataStore.data
        .map { prefs -> prefs[NOTIF_RECOMMENDATIONS_KEY] ?: true }

    val notifReleases: Flow<Boolean> = context.recoDataStore.data
        .map { prefs -> prefs[NOTIF_RELEASES_KEY] ?: true }

    suspend fun setUserName(value: String) {
        context.recoDataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = value.trim()
        }
    }

    suspend fun setUserEmail(value: String) {
        context.recoDataStore.edit { prefs ->
            prefs[USER_EMAIL_KEY] = value.trim()
        }
    }

    suspend fun setFavoritesIds(ids: Set<String>) {
        context.recoDataStore.edit { prefs ->
            prefs[FAVORITES_IDS_KEY] = ids
        }
    }

    suspend fun toggleFavorite(mediaType: String, id: Int) {
        val key = "$mediaType:$id"
        context.recoDataStore.edit { prefs ->
            val current = prefs[FAVORITES_IDS_KEY] ?: emptySet()
            prefs[FAVORITES_IDS_KEY] = if (key in current) current - key else current + key
        }
    }

    suspend fun setPlatformSubscriptions(values: Set<String>) {
        context.recoDataStore.edit { prefs ->
            prefs[PLATFORM_SUBSCRIPTIONS_KEY] = values
        }
    }

    suspend fun setNotifRecommendations(enabled: Boolean) {
        context.recoDataStore.edit { prefs ->
            prefs[NOTIF_RECOMMENDATIONS_KEY] = enabled
        }
    }

    suspend fun setNotifReleases(enabled: Boolean) {
        context.recoDataStore.edit { prefs ->
            prefs[NOTIF_RELEASES_KEY] = enabled
        }
    }

    suspend fun clearSession() {
        context.recoDataStore.edit { prefs ->
            prefs.remove(USER_NAME_KEY)
            prefs.remove(USER_EMAIL_KEY)
            prefs.remove(FAVORITES_IDS_KEY)
        }
    }

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val FAVORITES_IDS_KEY = stringSetPreferencesKey("fav_movie_ids")
        private val PLATFORM_SUBSCRIPTIONS_KEY = stringSetPreferencesKey("platform_subscriptions")
        private val NOTIF_RECOMMENDATIONS_KEY = booleanPreferencesKey("notif_recommendations")
        private val NOTIF_RELEASES_KEY = booleanPreferencesKey("notif_releases")

        private val defaultPlatformLabels: Set<String> = setOf(
            "Netflix",
            "Disney+",
            "HBO Max",
            "Prime Video",
            "Apple TV+",
        )
    }
}
