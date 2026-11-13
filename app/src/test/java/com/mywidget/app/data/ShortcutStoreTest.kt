package com.mywidget.app.data

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ShortcutStoreTest {
    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Before
    fun clearPreferences() {
        context.getSharedPreferences("my_widget_preferences", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun saveAndRead_shortcutsRoundTrip() {
        val expected = listOf(
            Shortcut(id = "one", title = "Docs", url = "https://example.com"),
            Shortcut(id = "two", title = "Shop", url = "https://shop.example")
        )

        ShortcutStore.saveAll(context, expected)

        assertEquals(expected, ShortcutStore.getAll(context))
    }

    @Test
    fun getAll_repairsMissingAndDuplicateIdsAndPersistsThem() {
        val prefs = context.getSharedPreferences("my_widget_preferences", Context.MODE_PRIVATE)
        prefs.edit().putString(
            "shortcuts",
            """[{"id":"same","title":"First","url":"https://one.example"},{"id":"same","title":"Second","url":"https://two.example"},{"title":"Third","url":"https://three.example"}]"""
        ).commit()

        val firstRead = ShortcutStore.getAll(context)
        val secondRead = ShortcutStore.getAll(context)

        assertEquals(3, firstRead.size)
        assertEquals(3, firstRead.map { it.id }.toSet().size)
        assertEquals(firstRead, secondRead)
        assertFalse(firstRead.any { it.id.isBlank() })
    }

    @Test
    fun getAll_malformedJsonPreservesOriginalBackup() {
        val prefs = context.getSharedPreferences("my_widget_preferences", Context.MODE_PRIVATE)
        val corrupt = """{"not":"an array"""
        prefs.edit().putString("shortcuts", corrupt).commit()

        assertTrue(ShortcutStore.getAll(context).isEmpty())
        assertEquals(corrupt, prefs.getString("shortcuts_corrupt_backup", null))
        assertEquals(corrupt, prefs.getString("shortcuts", null))
    }

    @Test
    fun saveAll_replacesCurrentDataWithoutDeletingCorruptBackup() {
        val prefs = context.getSharedPreferences("my_widget_preferences", Context.MODE_PRIVATE)
        prefs.edit().putString("shortcuts_corrupt_backup", "previous-corrupt-data").commit()

        ShortcutStore.saveAll(context, listOf(Shortcut(id = "valid", title = "Valid", url = "https://valid.example")))

        assertEquals("previous-corrupt-data", prefs.getString("shortcuts_corrupt_backup", null))
        assertEquals("valid", ShortcutStore.getAll(context).single().id)
    }
}
