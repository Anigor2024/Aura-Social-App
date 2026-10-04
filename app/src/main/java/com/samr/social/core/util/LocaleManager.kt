package com.samr.social.core.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class LocaleManager(context: Context) {

    private val prefs = context.getSharedPreferences("samr_preferences", Context.MODE_PRIVATE)

    // Default language is Arabic ("ar") as required by Saudi-First specification
    private val _currentLanguage = MutableStateFlow(
        prefs.getString(KEY_LANGUAGE, "ar") ?: "ar"
    )
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _hasSelectedLanguageOnboarding = MutableStateFlow(
        prefs.getBoolean(KEY_HAS_SELECTED_LANG, false)
    )
    val hasSelectedLanguageOnboarding: StateFlow<Boolean> = _hasSelectedLanguageOnboarding.asStateFlow()

    fun setLanguage(languageCode: String) {
        prefs.edit()
            .putString(KEY_LANGUAGE, languageCode)
            .putBoolean(KEY_HAS_SELECTED_LANG, true)
            .apply()
        _currentLanguage.value = languageCode
        _hasSelectedLanguageOnboarding.value = true
    }

    fun isArabic(): Boolean = _currentLanguage.value == "ar"

    fun getLayoutDirection(): LayoutDirection {
        return if (isArabic()) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    fun applyLocaleToContext(context: Context): Context {
        val locale = Locale.forLanguageTag(_currentLanguage.value)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    companion object {
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_HAS_SELECTED_LANG = "has_selected_language_onboarding"
    }
}
