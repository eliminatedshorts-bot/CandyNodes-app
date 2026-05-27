package com.example.ui.components

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class Bookmark(
    val id: String,
    val title: String,
    val url: String,
    val isSystem: Boolean = false
)

class BookmarksManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("candynodes_bookmarks", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Bookmark::class.java)
    private val adapter = moshi.adapter<List<Bookmark>>(listType)

    // Preset system bookmarks for fast navigation
    val defaultBookmarks = listOf(
        Bookmark(
            id = "sys_gp",
            title = "CandyNodes GP (Web Console)",
            url = "https://gp.candynodes.xyz/",
            isSystem = true
        ),
        Bookmark(
            id = "sys_billing",
            title = "Client Portal & Hosting",
            url = "https://candynodes.xyz/",
            isSystem = true
        ),
        Bookmark(
            id = "sys_status",
            title = "Node Status Monitor",
            url = "https://status.candynodes.xyz/",
            isSystem = true
        ),
        Bookmark(
            id = "sys_discord",
            title = "Guild / Community Help Discord",
            url = "https://discord.gg/candynodes",
            isSystem = true
        )
    )

    fun getBookmarks(): List<Bookmark> {
        val json = prefs.getString("user_bookmarks", null)
        val userList = if (json != null) {
            try {
                adapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
        return defaultBookmarks + userList
    }

    fun addBookmark(title: String, url: String): Boolean {
        if (title.isBlank() || url.isBlank()) return false
        val cleanUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
        val current = getUserBookmarks()
        // Prevent duplicate URLs
        if (current.any { it.url.equals(cleanUrl, ignoreCase = true) }) return false

        val newBookmark = Bookmark(
            id = "user_${System.currentTimeMillis()}",
            title = title,
            url = cleanUrl,
            isSystem = false
        )
        val updated = current + newBookmark
        saveUserBookmarks(updated)
        return true
    }

    fun removeBookmark(id: String) {
        val current = getUserBookmarks()
        val updated = current.filter { it.id != id }
        saveUserBookmarks(updated)
    }

    private fun getUserBookmarks(): List<Bookmark> {
        val json = prefs.getString("user_bookmarks", null) ?: return emptyList()
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveUserBookmarks(list: List<Bookmark>) {
        val json = adapter.toJson(list)
        prefs.edit().putString("user_bookmarks", json).apply()
    }
}
