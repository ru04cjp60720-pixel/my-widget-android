package com.mywidget.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class ShortcutOpenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL)
        val opened = if (!url.isNullOrBlank()) {
            runCatching {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }.isSuccess
        } else {
            false
        }

        if (!opened) {
            Toast.makeText(this, "無法開啟此網址，請確認網址或安裝瀏覽器", Toast.LENGTH_LONG).show()
        }
        finish()
    }

    companion object {
        const val EXTRA_URL = "com.mywidget.app.EXTRA_SHORTCUT_URL"
    }
}
