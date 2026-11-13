package com.mywidget.app

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ShortcutOpenActivityTest {
    @Test
    fun validUrl_startsExternalViewIntent() {
        val launchIntent = Intent().putExtra(
            ShortcutOpenActivity.EXTRA_URL,
            "https://example.com/path"
        )
        val activity = Robolectric.buildActivity(ShortcutOpenActivity::class.java, launchIntent)
            .create()
            .get()

        val started = shadowOf(activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, started.action)
        assertEquals("https://example.com/path", started.dataString)
    }

    @Test
    fun invalidUrl_doesNotStartExternalIntent() {
        val launchIntent = Intent().putExtra(
            ShortcutOpenActivity.EXTRA_URL,
            "javascript:alert(1)"
        )
        val activity = Robolectric.buildActivity(ShortcutOpenActivity::class.java, launchIntent)
            .create()
            .get()

        assertNull(shadowOf(activity).nextStartedActivity)
    }
}
