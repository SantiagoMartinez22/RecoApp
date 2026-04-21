package com.upb.reco.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
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

    val customLists: Flow<Map<String, Set<String>>> = context.recoDataStore.data
        .map { prefs -> decodeCustomLists(prefs[CUSTOM_LISTS_KEY].orEmpty()) }

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

    suspend fun setCustomLists(lists: Map<String, Set<String>>) {
        context.recoDataStore.edit { prefs ->
            prefs[CUSTOM_LISTS_KEY] = encodeCustomLists(lists)
        }
    }

    suspend fun createList(name: String) {
        val normalized = name.trim()
        if (normalized.isBlank()) return
        context.recoDataStore.edit { prefs ->
            val current = decodeCustomLists(prefs[CUSTOM_LISTS_KEY].orEmpty()).toMutableMap()
            current.putIfAbsent(normalized, emptySet())
            prefs[CUSTOM_LISTS_KEY] = encodeCustomLists(current)
        }
    }

    suspend fun deleteList(name: String) {
        val normalized = name.trim()
        if (normalized.isBlank()) return
        context.recoDataStore.edit { prefs ->
            val current = decodeCustomLists(prefs[CUSTOM_LISTS_KEY].orEmpty()).toMutableMap()
            current.remove(normalized)
            prefs[CUSTOM_LISTS_KEY] = encodeCustomLists(current)
        }
    }

    suspend fun addToList(listName: String, itemKey: String) {
        val normalizedListName = listName.trim()
        val normalizedItemKey = itemKey.trim()
        if (normalizedListName.isBlank() || normalizedItemKey.isBlank()) return
        context.recoDataStore.edit { prefs ->
            val current = decodeCustomLists(prefs[CUSTOM_LISTS_KEY].orEmpty()).toMutableMap()
            val items = current[normalizedListName].orEmpty().toMutableSet()
            items.add(normalizedItemKey)
            current[normalizedListName] = items
            prefs[CUSTOM_LISTS_KEY] = encodeCustomLists(current)
        }
    }

    suspend fun removeFromList(listName: String, itemKey: String) {
        val normalizedListName = listName.trim()
        val normalizedItemKey = itemKey.trim()
        if (normalizedListName.isBlank() || normalizedItemKey.isBlank()) return
        context.recoDataStore.edit { prefs ->
            val current = decodeCustomLists(prefs[CUSTOM_LISTS_KEY].orEmpty()).toMutableMap()
            val items = current[normalizedListName].orEmpty().toMutableSet()
            items.remove(normalizedItemKey)
            current[normalizedListName] = items
            prefs[CUSTOM_LISTS_KEY] = encodeCustomLists(current)
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
            prefs.remove(CUSTOM_LISTS_KEY)
        }
    }

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val FAVORITES_IDS_KEY = stringSetPreferencesKey("fav_movie_ids")
        private val CUSTOM_LISTS_KEY = stringPreferencesKey("custom_lists_json")
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

        private fun encodeCustomLists(lists: Map<String, Set<String>>): String {
            val root = JSONObject()
            lists.forEach { (name, ids) ->
                root.put(name, JSONArray(ids.toList()))
            }
            return root.toString()
        }

        private fun decodeCustomLists(serialized: String): Map<String, Set<String>> {
            if (serialized.isBlank()) return emptyMap()
            return try {
                val root = JSONObject(serialized)
                val result = linkedMapOf<String, Set<String>>()
                val names = root.keys()
                while (names.hasNext()) {
                    val name = names.next()
                    val values = root.optJSONArray(name) ?: JSONArray()
                    val items = linkedSetOf<String>()
                    for (index in 0 until values.length()) {
                        val value = values.optString(index).trim()
                        if (value.isNotBlank()) {
                            items.add(value)
                        }
                    }
                    result[name] = items
                }
                result
            } catch (_: Exception) {
                emptyMap()
            }
        }
    }
}
