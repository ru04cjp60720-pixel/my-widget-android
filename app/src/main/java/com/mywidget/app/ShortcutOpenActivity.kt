package com.mywidget.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class ShortcutOpenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rawUrl = intent.getStringExtra(EXTRA_URL)
        val opened = runCatching {
            val uri = rawUrl?.let(Uri::parse) ?: return@runCatching false
            val scheme = uri.scheme?.lowercase()
            if ((scheme != "http" && scheme != "https") || uri.host.isNullOrBlank()) {
                return@runCatching false
            }
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        }.getOrDefault(false)

        if (!opened) {
            Toast.makeText(this, "無法開啟此網址，請確認網址或安裝瀏覽器", Toast.LENGTH_LONG).show()
        }
        finish()
    }

    companion object {
        const val EXTRA_URL = "com.mywidget.app.EXTRA_SHORTCUT_URL"
    }
}
