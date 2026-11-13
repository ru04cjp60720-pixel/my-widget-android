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

    @Synchronized
    fun getAll(context: Context): List<Shortcut> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_SHORTCUTS, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            val usedIds = mutableSetOf<String>()
            val normalizedArray = JSONArray()
            var idsChanged = false

            val shortcuts = (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index)
                if (item == null) {
                    normalizedArray.put(array.opt(index))
                    return@mapNotNull null
                }

                val title = (item.opt("title") as? String)?.trim().orEmpty()
                val url = (item.opt("url") as? String)?.trim().orEmpty()
                if (title.isBlank() || url.isBlank()) {
                    normalizedArray.put(item)
                    return@mapNotNull null
                }

                val storedId = (item.opt("id") as? String)?.trim().orEmpty()
                val id = if (storedId.isNotBlank() && usedIds.add(storedId)) {
                    storedId
                } else {
                    idsChanged = true
                    UUID.randomUUID().toString().also { usedIds.add(it) }
                }

                val normalizedItem = if (id == storedId) {
                    item
                } else {
                    JSONObject(item.toString()).put("id", id)
                }
                normalizedArray.put(normalizedItem)
                Shortcut(id = id, title = title, url = url)
            }

            // Persist repaired IDs so the app and widget receive the same stable IDs
            // on every subsequent read. Keep invalid/unrecognized array entries intact.
            if (idsChanged) {
                prefs.edit().putString(KEY_SHORTCUTS, normalizedArray.toString()).apply()
            }
            shortcuts
        } catch (_: Exception) {
            // Preserve malformed source data before returning an empty list.
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
