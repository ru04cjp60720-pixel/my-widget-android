package com.mywidget.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Shortcut(val id: String = UUID.randomUUID().toString(), val title: String, val url: String)

object ShortcutStore {
    private const val PREFS = "my_widget_preferences"
    private const val KEY_SHORTCUTS = "shortcuts"

    fun getAll(context: Context): List<Shortcut> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_SHORTCUTS, "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                val title = item.optString("title").trim()
                val url = item.optString("url").trim()
                if (title.isBlank() || url.isBlank()) null
                else Shortcut(item.optString("id"), title, url)
            }
        }.getOrDefault(emptyList())
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
