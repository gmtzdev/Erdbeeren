package com.example.erdbeeren.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

//    companion object {
//        val USERNAME_KEY = stringPreferencesKey("username")
//        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
//    }
//
//    val usernameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
//        preferences[USERNAME_KEY]
//    }
//
//    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
//        preferences[IS_LOGGED_IN_KEY] ?: false
//    }
//
//    suspend fun saveUserSession(username: String) {
//        context.dataStore.edit { preferences ->
//            preferences[USERNAME_KEY] = username
//            preferences[IS_LOGGED_IN_KEY] = true
//        }
//    }
//
//    suspend fun clearSession() {
//        context.dataStore.edit { preferences ->
//            preferences.clear()
//        }
//    }
}