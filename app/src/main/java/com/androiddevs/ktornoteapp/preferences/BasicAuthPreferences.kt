package com.androiddevs.ktornoteapp.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.androiddevs.ktornoteapp.other.Constants.ENCRYPTED_SHARED_PREF_NAME
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_PASSWORD


class BasicAuthPreferences(private val context: Context) {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val prefs = EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_SHARED_PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getStoredEmail(): String {
        return prefs.getString(KEY_LOGGED_IN_EMAIL, "")!!
    }

    fun setStoredEmail(email: String) {
        prefs
            .edit()
            .putString(KEY_LOGGED_IN_EMAIL, email)
            .apply()
    }

    fun getStoredPassword(): String {
        return prefs.getString(KEY_PASSWORD, "")!!
    }

    fun setStoredPassword(password: String) {
        prefs
            .edit()
            .putString(KEY_PASSWORD, password)
            .apply()
    }
}