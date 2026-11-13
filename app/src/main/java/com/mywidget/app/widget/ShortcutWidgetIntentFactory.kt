package com.mywidget.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mywidget.app.ShortcutOpenActivity

/**
 * Builds the explicit activity intent used by each widget shortcut.
 * The unique data URI prevents Android PendingIntent identity collisions.
 */
internal object ShortcutWidgetIntentFactory {
    fun openShortcut(context: Context, shortcutId: String, url: String): Intent =
        Intent(context, ShortcutOpenActivity::class.java)
            .setData(Uri.parse("mywidget://shortcut/$shortcutId"))
            .putExtra(ShortcutOpenActivity.EXTRA_URL, url)
}
