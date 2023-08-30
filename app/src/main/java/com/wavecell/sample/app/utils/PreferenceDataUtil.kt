package com.wavecell.sample.app.utils

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import java.util.*

abstract class PreferenceDataUtil(private val sharedPreferences: SharedPreferences) {
    fun put(key: String, value: String?) {
        editor.putString(key, value).apply()
    }

    protected fun put(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    protected fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    protected fun put(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    protected fun put(key: String, value: Double) {
        editor.putString(key, value.toString()).apply()
    }

    protected fun put(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    protected fun put(key: String, stringSet: Set<String?>) {
        editor.putStringSet(key, stringSet).apply()
    }

    protected fun getString(key: String): String? {
        return getString(key, "")
    }

    protected fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    protected fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    protected fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    protected fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }

    protected fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    protected fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0f)
    }

    protected fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    protected fun getDouble(key: String): Double {
        return getDouble(key, 0.0)
    }

    protected fun getDouble(key: String, defaultValue: Double): Double {
        return try {
            sharedPreferences.getString(key, defaultValue.toString())!!.toDouble()
        } catch (nfe: NumberFormatException) {
            defaultValue
        }
    }

    protected fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    protected fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    protected fun getStringSet(key: String): List<String>? {
        val tasksSet = sharedPreferences.getStringSet(key, HashSet())
        return tasksSet?.toList()
    }

    operator fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    fun remove(vararg keys: String) {
        val sharedPreferencesEditor: Editor = editor
        for (key in keys) {
            sharedPreferencesEditor.remove(key)
        }
        sharedPreferencesEditor.apply()
    }

    fun clear() {
        editor.clear().apply()
    }

    private val editor: Editor
        get() = sharedPreferences.edit()
}
