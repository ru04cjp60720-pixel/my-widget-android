package com.mywidget.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mywidget.app.ShortcutOpenActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ShortcutWidgetIntentFactoryTest {
    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun openShortcut_targetsInternalActivityAndCarriesUrlAndStableIdentity() {
        val intent = ShortcutWidgetIntentFactory.openShortcut(
            context,
            "shortcut-123",
            "https://example.com/page"
        )

        assertEquals(ShortcutOpenActivity::class.java.name, intent.component?.className)
        assertEquals("https://example.com/page", intent.getStringExtra(ShortcutOpenActivity.EXTRA_URL))
        assertEquals(Uri.parse("mywidget://shortcut/shortcut-123"), intent.data)
    }

    @Test
    fun openShortcut_usesDifferentPendingIntentIdentityForDifferentShortcuts() {
        val first = ShortcutWidgetIntentFactory.openShortcut(context, "first", "https://one.example")
        val second = ShortcutWidgetIntentFactory.openShortcut(context, "second", "https://two.example")

        assertNotEquals(first.data, second.data)
    }
}
