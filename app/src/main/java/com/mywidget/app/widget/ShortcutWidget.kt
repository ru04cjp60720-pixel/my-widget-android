package com.mywidget.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity as actionStartActivityForActivity
import androidx.glance.appwidget.action.actionStartActivity as actionStartActivityForIntent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mywidget.app.MainActivity
import com.mywidget.app.ShortcutOpenActivity
import com.mywidget.app.data.ShortcutStore

class ShortcutWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val shortcuts = ShortcutStore.getAll(context)
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize()
                    .background(ColorProvider(Color(0xFF202124)))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (shortcuts.isEmpty()) {
                    Text(
                        text = "My Widget｜點擊設定快捷鍵",
                        style = TextStyle(color = ColorProvider(Color.White)),
                        modifier = GlanceModifier.fillMaxWidth()
                            .clickable(actionStartActivityForActivity<MainActivity>())
                            .padding(8.dp)
                    )
                } else {
                    shortcuts.take(8).forEach { shortcut ->
                        Row(
                            modifier = GlanceModifier.fillMaxWidth()
                                .clickable(actionStartActivityForIntent(
                                    Intent(context, ShortcutOpenActivity::class.java)
                                        .setData(Uri.parse("mywidget://shortcut/${shortcut.id}"))
                                        .putExtra(ShortcutOpenActivity.EXTRA_URL, shortcut.url)
                                ))
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "•  ${shortcut.title}",
                                style = TextStyle(color = ColorProvider(Color.White))
                            )
                        }
                    }
                }
            }
        }
    }
}

class ShortcutWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget()
}
