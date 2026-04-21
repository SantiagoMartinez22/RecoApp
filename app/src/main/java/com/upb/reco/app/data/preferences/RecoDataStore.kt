package com.upb.reco.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.recoDataStore: DataStore<Preferences> by preferencesDataStore(name = "reco_preferences")
