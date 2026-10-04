package com.samr.social.core.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocaleManager(context: Context) {

    private val prefs = context.getSharedPreferences("samr_preferences", Context.MODE_PRIVATE)

    // Determine initial language: check AppCompatDelegate per-app locale first, fallback to preferences, default to Arabic "ar"
    private val initialLanguage: String = run {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) {
            val tag = appLocales.toLanguageTags().substringBefore(",").substringBefore("-")
            if (tag.isNotEmpty()) tag else "ar"
        } else {
            prefs.getString(KEY_LANGUAGE, "ar") ?: "ar"
        }
    }

    private val _currentLanguage = MutableStateFlow(initialLanguage)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _hasSelectedLanguageOnboarding = MutableStateFlow(
        prefs.getBoolean(KEY_HAS_SELECTED_LANG, false)
    )
    val hasSelectedLanguageOnboarding: StateFlow<Boolean> = _hasSelectedLanguageOnboarding.asStateFlow()

    init {
        // Ensure per-app locale is set if previously recorded
        if (prefs.getBoolean(KEY_HAS_SELECTED_LANG, false)) {
            val appLocales = AppCompatDelegate.getApplicationLocales()
            if (appLocales.isEmpty || !appLocales.toLanguageTags().startsWith(initialLanguage)) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(initialLanguage))
            }
        }
    }

    fun setLanguage(languageCode: String) {
        prefs.edit()
            .putString(KEY_LANGUAGE, languageCode)
            .putBoolean(KEY_HAS_SELECTED_LANG, true)
            .apply()

        _currentLanguage.value = languageCode
        _hasSelectedLanguageOnboarding.value = true

        // Official Android per-app locale API (Android 13+ & backward-compatible through AppCompat)
        val appLocales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    fun isArabic(): Boolean = _currentLanguage.value == "ar"

    fun getLayoutDirection(): LayoutDirection {
        return if (isArabic()) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    companion object {
        const val KEY_LANGUAGE = "selected_language"
        const val KEY_HAS_SELECTED_LANG = "has_selected_language_onboarding"
    }
}
