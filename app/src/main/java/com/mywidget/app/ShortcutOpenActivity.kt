package com.mywidget.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class ShortcutOpenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val normalizedUrl = UrlNormalizer.normalize(intent.getStringExtra(EXTRA_URL).orEmpty())
        val opened = runCatching {
            if (normalizedUrl == null) {
                false
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(normalizedUrl)))
                true
            }
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
