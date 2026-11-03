package app.zanjir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class Conversation(val title: String, val subtitle: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZanjirApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZanjirApp() {
    var selected by remember { mutableStateOf("chats") }
    val conversations = listOf(
        Conversation("شروع کار با زنجیر", "برای شروع، ابتدا هویت مستقل خود را ایجاد کن"),
        Conversation("راهنمای زنجیر", "این نسخه پوستهٔ اولیه است و هنوز به هسته متصل نشده")
    )
    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("زنجیر", fontWeight = FontWeight.Bold) },
                    actions = { Text("نسخه آزمایشی", modifier = Modifier.padding(end = 16.dp)) }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected == "chats", { selected = "chats" }, icon = {}, label = { Text("گفتگوها") })
                    NavigationBarItem(selected == "contacts", { selected = "contacts" }, icon = {}, label = { Text("مخاطبان") })
                    NavigationBarItem(selected == "settings", { selected = "settings" }, icon = {}, label = { Text("تنظیمات") })
                }
            }
        ) { padding ->
            when (selected) {
                "chats" -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("پیام‌رسان مستقل", style = MaterialTheme.typography.titleLarge)
                                Text("زنجیر با هدف ارتباط مستقیم و امن ساخته می‌شود. قابلیت‌های واقعی پیام‌رسانی پس از اتصال و آزمون هسته فعال خواهند شد.")
                                AssistChip(onClick = {}, label = { Text("هسته هنوز متصل نیست") })
                            }
                        }
                    }
                    items(conversations) { item ->
                        ListItem(
                            headlineContent = { Text(item.title) },
                            supportingContent = { Text(item.subtitle) }
                        )
                        HorizontalDivider()
                    }
                }
                "contacts" -> CenterMessage(padding, "مخاطبان", "افزودن مخاطب با QR پس از اتصال هسته فعال می‌شود.")
                else -> CenterMessage(padding, "تنظیمات", "حریم خصوصی، اعلان‌ها و پشتیبان‌گیری در مراحل بعد اضافه می‌شوند.")
            }
        }
    }
}

@Composable
private fun CenterMessage(padding: PaddingValues, title: String, detail: String) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text(detail, style = MaterialTheme.typography.bodyLarge)
    }
}
