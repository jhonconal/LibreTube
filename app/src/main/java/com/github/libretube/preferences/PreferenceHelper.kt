package com.github.libretube.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.libretube.obj.CustomInstance
import com.github.libretube.obj.Streams
import com.github.libretube.obj.WatchHistoryItem
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

object PreferenceHelper {
    private val TAG = "PreferenceHelper"

    fun setString(context: Context, key: String?, value: String?) {
        val editor = getDefaultSharedPreferencesEditor(context)
        editor.putString(key, value)
        editor.apply()
    }

    fun setInt(context: Context, key: String?, value: Int) {
        val editor = getDefaultSharedPreferencesEditor(context)
        editor.putInt(key, value)
        editor.apply()
    }

    fun setLong(context: Context, key: String?, value: Long) {
        val editor = getDefaultSharedPreferencesEditor(context)
        editor.putLong(key, value)
        editor.apply()
    }

    fun setBoolean(context: Context, key: String?, value: Boolean) {
        val editor = getDefaultSharedPreferencesEditor(context)
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String?, defValue: String?): String? {
        val settings: SharedPreferences = getDefaultSharedPreferences(context)
        return settings.getString(key, defValue)
    }

    fun getInt(context: Context, key: String?, defValue: Int): Int {
        val settings: SharedPreferences = getDefaultSharedPreferences(context)
        return settings.getInt(key, defValue)
    }

    fun getLong(context: Context, key: String?, defValue: Long): Long {
        val settings: SharedPreferences = getDefaultSharedPreferences(context)
        return settings.getLong(key, defValue)
    }

    fun getBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        val settings: SharedPreferences = getDefaultSharedPreferences(context)
        return settings.getBoolean(key, defValue)
    }

    fun clearPreferences(context: Context) {
        val editor = getDefaultSharedPreferencesEditor(context)
        editor.clear().apply()
    }

    fun removePreference(context: Context, value: String?) {
        val editor = getDefaultSharedPreferencesEditor(context)
        editor.remove(value).apply()
    }

    fun getToken(context: Context): String {
        val sharedPref = context.getSharedPreferences("token", Context.MODE_PRIVATE)
        return sharedPref?.getString("token", "")!!
    }

    fun setToken(context: Context, newValue: String) {
        val editor = context.getSharedPreferences("token", Context.MODE_PRIVATE).edit()
        editor.putString("token", newValue).apply()
    }

    fun getUsername(context: Context): String {
        val sharedPref = context.getSharedPreferences("username", Context.MODE_PRIVATE)
        return sharedPref.getString("username", "")!!
    }

    fun setUsername(context: Context, newValue: String) {
        val editor = context.getSharedPreferences("username", Context.MODE_PRIVATE).edit()
        editor.putString("username", newValue).apply()
    }

    fun saveCustomInstance(context: Context, customInstance: CustomInstance) {
        val editor = getDefaultSharedPreferencesEditor(context)
        val gson = Gson()

        val customInstancesList = getCustomInstances(context)
        customInstancesList += customInstance

        val json = gson.toJson(customInstancesList)
        editor.putString("customInstances", json).apply()
    }

    fun getCustomInstances(context: Context): ArrayList<CustomInstance> {
        val settings = getDefaultSharedPreferences(context)
        val gson = Gson()
        val json: String = settings.getString("customInstances", "")!!
        val type: Type = object : TypeToken<List<CustomInstance?>?>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    fun getHistory(context: Context): List<String> {
        return try {
            val settings = getDefaultSharedPreferences(context)
            val set: Set<String> = settings.getStringSet("search_history", HashSet())!!
            set.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveHistory(context: Context, historyList: List<String>) {
        val editor = getDefaultSharedPreferencesEditor(context)
        val set: Set<String> = HashSet(historyList)
        editor.putStringSet("search_history", set).apply()
    }

    fun addToWatchHistory(context: Context, videoId: String, streams: Streams) {
        val editor = getDefaultSharedPreferencesEditor(context)
        val gson = Gson()

        val watchHistoryItem = WatchHistoryItem(
            videoId,
            streams.title,
            streams.uploadDate,
            streams.uploader,
            streams.uploaderUrl?.replace("/channel/", ""),
            streams.uploaderAvatar,
            streams.thumbnailUrl,
            streams.duration
        )

        val watchHistory = getWatchHistory(context)

        // delete entries that have the same videoId
        var indexToRemove = Int.MAX_VALUE
        watchHistory.forEachIndexed { index, item ->
            if (item.videoId == videoId) indexToRemove = index
        }
        if (indexToRemove != Int.MAX_VALUE) watchHistory.removeAt(indexToRemove)

        watchHistory += watchHistoryItem

        val json = gson.toJson(watchHistory)
        editor.putString("watch_history", json).apply()
    }

    fun getWatchHistory(context: Context): ArrayList<WatchHistoryItem> {
        val settings = getDefaultSharedPreferences(context)
        val gson = Gson()
        val json: String = settings.getString("watch_history", "")!!
        val type: Type = object : TypeToken<List<WatchHistoryItem?>?>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    private fun getDefaultSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun getDefaultSharedPreferencesEditor(context: Context): SharedPreferences.Editor {
        return getDefaultSharedPreferences(context).edit()
    }
}