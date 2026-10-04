package com.samr.social.core.util

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(
        "samr_session",
        Context.MODE_PRIVATE
    )

    private val _isDemoSession = MutableStateFlow(
        prefs.getBoolean(KEY_DEMO_SESSION, false)
    )
    val isDemoSession: StateFlow<Boolean> = _isDemoSession.asStateFlow()

    fun startDemoSession() {
        prefs.edit()
            .putBoolean(KEY_DEMO_SESSION, true)
            .apply()
        _isDemoSession.value = true
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_DEMO_SESSION, false)
            .apply()
        _isDemoSession.value = false
    }

    private companion object {
        const val KEY_DEMO_SESSION = "demo_session_active"
    }
}
