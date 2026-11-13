package com.mywidget.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Shortcut(val id: String = UUID.randomUUID().toString(), val title: String, val url: String)

object ShortcutStore {
    private const val PREFS = "my_widget_preferences"
    private const val KEY_SHORTCUTS = "shortcuts"
    private const val KEY_CORRUPT_BACKUP = "shortcuts_corrupt_backup"

    fun getAll(context: Context): List<Shortcut> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SHORTCUTS, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            val usedIds = mutableSetOf<String>()
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                val title = item.optString("title").trim()
                val url = item.optString("url").trim()
                if (title.isBlank() || url.isBlank()) return@mapNotNull null

                val storedId = item.optString("id").trim()
                val id = if (storedId.isNotBlank() && usedIds.add(storedId)) {
                    storedId
                } else {
                    UUID.randomUUID().toString().also { usedIds.add(it) }
                }
                Shortcut(id = id, title = title, url = url)
            }
        } catch (_: Exception) {
            // Keep the original payload before returning an empty list. A later save
            // must not silently destroy the only copy of malformed user data.
            if (raw.isNotBlank() && raw != "[]") {
                prefs.edit().putString(KEY_CORRUPT_BACKUP, raw).apply()
            }
            emptyList()
        }
    }

    fun saveAll(context: Context, shortcuts: List<Shortcut>) {
        val array = JSONArray()
        shortcuts.forEach { item ->
            array.put(JSONObject().put("id", item.id).put("title", item.title).put("url", item.url))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_SHORTCUTS, array.toString()).apply()
    }
}
