package com.bear.asset.data.local

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(application: Application) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "bear_asset_secure_prefs",
        masterKeyAlias,
        application,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    fun saveLoginInfo(token: String, userId: Long, username: String, nickname: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_NICKNAME, nickname)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getNickname(): String? = prefs.getString(KEY_NICKNAME, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clearToken() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_NICKNAME)
            .apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
}
