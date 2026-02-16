package com.kappa.app.core.localization

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object AppLanguageManager {

    private const val DEFAULT_LANGUAGE_TAG = "en"
    private val supportedTags = setOf("en", "pt", "es")

    fun resolveLanguageTag(value: String?): String? {
        val normalized = value?.trim()?.lowercase(Locale.ENGLISH).orEmpty()
        if (normalized.isBlank()) return null

        return when (normalized) {
            "en", "english", "ingles" -> "en"
            "pt", "pt-br", "portuguese", "portugues" -> "pt"
            "es", "spanish", "espanol", "espanhol" -> "es"
            else -> null
        }
    }

    fun normalizeOrDefault(value: String?): String {
        val tag = resolveLanguageTag(value) ?: value?.trim()?.lowercase(Locale.ENGLISH)
        return if (tag != null && tag in supportedTags) tag else DEFAULT_LANGUAGE_TAG
    }

    fun applyLanguage(value: String?) {
        val tag = normalizeOrDefault(value)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
