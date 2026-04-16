package com.reco.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reco.app.data.model.Platform
import com.reco.app.data.preferences.ThemeMode
import com.reco.app.data.preferences.ThemePreferences
import com.reco.app.data.preferences.UserPreferences
import com.reco.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiModel(
    val userName: String,
    val userEmail: String,
    val notifRecommendations: Boolean,
    val notifReleases: Boolean,
    val platformSubscriptions: Set<String>,
    val themeMode: ThemeMode,
)

class SettingsViewModel(
    private val userPreferences: UserPreferences,
    private val themePreferences: ThemePreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val uiModel: StateFlow<SettingsUiModel> = combine(
        userPreferences.userName,
        userPreferences.userEmail,
        userPreferences.notifRecommendations,
        userPreferences.notifReleases,
        userPreferences.platformSubscriptions,
        themePreferences.themeMode,
    ) { values: Array<Any?> ->
        val userName = values[0] as String
        val userEmail = values[1] as String
        val notifReco = values[2] as Boolean
        val notifReleases = values[3] as Boolean
        @Suppress("UNCHECKED_CAST")
        val subscriptions = values[4] as Set<String>
        val themeMode = values[5] as ThemeMode
        SettingsUiModel(
            userName = userName,
            userEmail = userEmail,
            notifRecommendations = notifReco,
            notifReleases = notifReleases,
            platformSubscriptions = subscriptions,
            themeMode = themeMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiModel(
            userName = "tú",
            userEmail = "",
            notifRecommendations = true,
            notifReleases = true,
            platformSubscriptions = Platform.entries.map { it.label }.toSet(),
            themeMode = ThemeMode.SYSTEM,
        ),
    )

    fun setNotifRecommendations(value: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotifRecommendations(value)
        }
    }

    fun setNotifReleases(value: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotifReleases(value)
        }
    }

    fun togglePlatform(platform: Platform) {
        viewModelScope.launch {
            val current = uiModel.value.platformSubscriptions
            val updated = if (platform.label in current) current - platform.label else current + platform.label
            userPreferences.setPlatformSubscriptions(updated)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    suspend fun sendPasswordResetEmail(email: String) = authRepository.sendPasswordResetEmail(email)

    companion object {
        fun Factory(
            userPreferences: UserPreferences,
            themePreferences: ThemePreferences,
            authRepository: AuthRepository,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(SettingsViewModel::class.java))
                return SettingsViewModel(userPreferences, themePreferences, authRepository) as T
            }
        }
    }
}
