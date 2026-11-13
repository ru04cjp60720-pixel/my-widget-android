package com.mywidget.app

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.mywidget.app.data.Shortcut
import com.mywidget.app.data.ShortcutStore
import com.mywidget.app.widget.ShortcutWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { ShortcutManagerScreen() } } }
    }
}

@Composable
private fun ShortcutManagerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var shortcuts by remember { mutableStateOf(ShortcutStore.getAll(context)) }
    var editing by remember { mutableStateOf<Shortcut?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    fun refresh() {
        shortcuts = ShortcutStore.getAll(context)
        scope.launch { ShortcutWidget().updateAll(context) }
    }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("My Widget", style = MaterialTheme.typography.headlineMedium)
        Text("管理桌面快捷鍵", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { editing = null; showEditor = true }, modifier = Modifier.fillMaxWidth()) {
            Text("＋ 新增快捷鍵")
        }
        if (shortcuts.isEmpty()) {
            Text("目前沒有快捷鍵。新增網址後，即可在桌面 Widget 中使用。")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(shortcuts, key = { it.id }) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.url, style = MaterialTheme.typography.bodySmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { editing = item; showEditor = true }) { Text("編輯") }
                                TextButton(onClick = {
                                    ShortcutStore.saveAll(context, shortcuts.filterNot { it.id == item.id })
                                    refresh()
                                }) { Text("刪除") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        var title by remember(editing) { mutableStateOf(editing?.title.orEmpty()) }
        var url by remember(editing) { mutableStateOf(editing?.url.orEmpty()) }
        val normalizedUrl = normalizeHttpUrl(url)
        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text(if (editing == null) "新增快捷鍵" else "編輯快捷鍵") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(title, { title = it }, label = { Text("名稱") }, singleLine = true)
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("網址") },
                        placeholder = { Text("https://example.com") },
                        singleLine = true,
                        isError = url.isNotBlank() && normalizedUrl == null,
                        supportingText = {
                            if (url.isNotBlank() && normalizedUrl == null) {
                                Text("請輸入有效的 http 或 https 網址，例如 https://example.com")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(enabled = title.isNotBlank() && normalizedUrl != null, onClick = {
                    val normalized = normalizedUrl ?: return@Button
                    val updated = if (editing == null) shortcuts + Shortcut(title = title.trim(), url = normalized)
                    else shortcuts.map { if (it.id == editing?.id) it.copy(title = title.trim(), url = normalized) else it }
                    ShortcutStore.saveAll(context, updated)
                    showEditor = false
                    refresh()
                }) { Text("儲存") }
            },
            dismissButton = { TextButton(onClick = { showEditor = false }) { Text("取消") } }
        )
    }
}


private fun normalizeHttpUrl(raw: String): String? {
    val input = raw.trim()
    if (input.isBlank()) return null

    val hasExplicitScheme = Regex("^[A-Za-z][A-Za-z0-9+.-]*:").containsMatchIn(input)
    val candidate = if (hasExplicitScheme) input else "https://$input"
    val uri = runCatching { Uri.parse(candidate) }.getOrNull() ?: return null
    val scheme = uri.scheme?.lowercase()
    if (scheme != "http" && scheme != "https") return null
    if (uri.host.isNullOrBlank()) return null
    return candidate
}
