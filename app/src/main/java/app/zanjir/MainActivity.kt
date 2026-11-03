package app.zanjir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mlmchat.core.ChatNode
import org.mlmchat.core.Contact
import org.mlmchat.core.Message
import org.mlmchat.core.PendingIdentity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZanjirApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZanjirApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var node by remember { mutableStateOf<ChatNode?>(null) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var selected by remember { mutableStateOf<Contact?>(null) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var identityName by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf("") }
    var invite by remember { mutableStateOf("") }
    var myCard by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("در حال بررسی هویت…") }
    var busy by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var showInvite by remember { mutableStateOf(false) }

    fun reload(n: ChatNode) {
        contacts = n.contacts()
        selected = selected?.let { old -> contacts.firstOrNull { it.sk == old.sk } }
        messages = selected?.let { n.history(it.sk, 200) } ?: emptyList()
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            runCatching {
                if (ChatNode.exists(context)) ChatNode.open(context) else null
            }.onSuccess { opened ->
                node = opened
                if (opened != null) {
                    status = "هویت فعال: ${opened.me.displayName}"
                    runCatching {
                        opened.startDirect()
                        opened.startP2p()
                        opened.setOnline(true)
                    }.onFailure { status = "هویت آماده؛ اتصال شبکه نیازمند بررسی است: ${it.message ?: "خطا"}" }
                    reload(opened)
                } else status = "برای شروع، هویت زنجیر را بساز."
            }.onFailure { status = "راه‌اندازی هسته ناموفق بود: ${it.message ?: "کتابخانه بومی موجود نیست"}" }
        }
    }

    LaunchedEffect(node, selected?.sk) {
        val n = node ?: return@LaunchedEffect
        while (true) {
            withContext(Dispatchers.IO) {
                runCatching {
                    n.pollEvent(500)
                    contacts = n.contacts()
                    messages = selected?.let { n.history(it.sk, 200) } ?: emptyList()
                }
            }
            delay(1200)
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF229ED9), onPrimary = Color.White, secondary = Color(0xFF65BCEB), background = Color(0xFFF4F7F9), surface = Color.White)) {
        Scaffold(
            containerColor = Color(0xFFF4F7F9),
            bottomBar = {
                if (node != null && selected == null) NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(selected = true, onClick = {}, icon = { Text("●") }, label = { Text("گفتگوها") })
                    NavigationBarItem(selected = false, onClick = { showInvite = true }, icon = { Text("◉") }, label = { Text("مخاطبان") })
                    NavigationBarItem(selected = false, onClick = { status = "تنظیمات حساب و حریم خصوصی در حال تکمیل است." }, icon = { Text("⚙") }, label = { Text("تنظیمات") })
                }
            },
            floatingActionButton = {
                if (node != null && selected == null) FloatingActionButton(onClick = { showInvite = !showInvite }, containerColor = Color(0xFF229ED9), contentColor = Color.White) { Text("+") }
            },
            topBar = {
                TopAppBar(
                    title = { Column { Text("زنجیر", fontWeight = FontWeight.Bold); Text("پیام‌رسان", style = MaterialTheme.typography.labelSmall, color = Color.Gray) } },
                    actions = { Text("⋮", modifier = Modifier.padding(horizontal = 18.dp), color = MaterialTheme.colorScheme.primary) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            if (node == null) {
                Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("شروع زنجیر", style = MaterialTheme.typography.headlineMedium)
                    Text(status)
                    OutlinedTextField(value = identityName, onValueChange = { identityName = it }, label = { Text("نام نمایشی") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Button(
                        enabled = identityName.trim().length >= 2 && !busy,
                        onClick = {
                            busy = true
                            scope.launch {
                                val result = withContext(Dispatchers.IO) {
                                    runCatching {
                                        PendingIdentity().use { it.create(context, identityName.trim()) }
                                    }
                                }
                                if (result.isSuccess) {
                                    val created = result.getOrThrow()
                                    node = created
                                    status = "هویت ساخته شد: ${created.me.displayName}"
                                    withContext(Dispatchers.IO) {
                                        runCatching { created.startDirect(); created.startP2p(); created.setOnline(true) }
                                    }
                                    val initial = withContext(Dispatchers.IO) { created.contacts() }
                                    contacts = initial
                                } else {
                                    status = "ساخت هویت ناموفق بود: ${result.exceptionOrNull()?.message ?: "خطای ناشناخته"}"
                                }
                                busy = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (busy) "در حال ساخت…" else "ساخت هویت امن") }
                    Text("کلید هویت روی همین دستگاه ساخته می‌شود و پایگاه داده با Android Keystore محافظت می‌شود.")
                }
            } else if (selected == null) {
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(status, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    OutlinedTextField(value = search, onValueChange = { search = it }, placeholder = { Text("جستجو") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = { search = "" }, label = { Text("همه") })
                        AssistChip(onClick = { status = "فیلتر خوانده‌نشده پس از اتصال وضعیت پیام‌ها فعال می‌شود." }, label = { Text("خوانده‌نشده") })
                    }
                    if (showInvite) {
                    Text("افزودن مخاطب", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = {
                        scope.launch {
                            val card = withContext(Dispatchers.IO) { runCatching { node!!.cardLink() } }
                            card.onSuccess { myCard = it }.onFailure { status = it.message ?: "خطا در ساخت کارت" }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("نمایش کارت / کد دعوت من") }
                    if (myCard.isNotBlank()) {
                        SelectionContainerText(myCard)
                        Text("این پیوند را فقط با مخاطب مورداعتماد خود به اشتراک بگذار.")
                    }
                    OutlinedTextField(value = invite, onValueChange = { invite = it }, label = { Text("پیوند کارت مخاطب") }, modifier = Modifier.fillMaxWidth())
                    Button(enabled = invite.startsWith("mlmchat://c/"), onClick = {
                        scope.launch {
                            val result = withContext(Dispatchers.IO) { runCatching { node!!.addContact(invite.trim()) } }
                            result.onSuccess { invite = ""; reload(node!!) }.onFailure { status = it.message ?: "افزودن مخاطب ناموفق بود" }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("افزودن مخاطب") }
                    }
                    if (contacts.isEmpty()) Text("هنوز گفتگویی ندارید. از + برای افزودن مخاطب استفاده کنید.", modifier = Modifier.padding(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        items(contacts.filter { it.displayName.contains(search, ignoreCase = true) }, key = { it.sk }) { contact ->
                            Surface(color = Color.White, onClick = { selected = contact; scope.launch { messages = withContext(Dispatchers.IO) { node!!.history(contact.sk, 200) } } }) {
                                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Surface(shape = MaterialTheme.shapes.extraLarge, color = Color(0xFFD8EFFB), modifier = Modifier.size(48.dp)) {
                                        Box(contentAlignment = androidx.compose.ui.Alignment.Center) { Text(contact.displayName.take(1).uppercase(), color = Color(0xFF147DB0), fontWeight = FontWeight.Bold) }
                                    }
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(contact.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                        Text(if (contact.verified) "گفتگوی رمزگذاری‌شده · تأییدشده" else "کد امنیتی هنوز تطبیق داده نشده", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Text("›", color = Color.Gray)
                                }
                            }
                            HorizontalDivider(color = Color(0xFFEFF1F3), thickness = 0.5.dp)
                        }
                    }
                }
            } else {
                Column(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { selected = null; messages = emptyList() }) { Text("بازگشت") }
                        Text(selected!!.displayName, style = MaterialTheme.typography.titleLarge)
                    }
                    LazyColumn(Modifier.weight(1f), reverseLayout = false, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(messages, key = { it.seq }) { msg ->
                            Surface(color = if (msg.outgoing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                                Column(Modifier.fillMaxWidth().padding(10.dp)) {
                                    Text(msg.text ?: "[${msg.kind}]")
                                    Text(msg.state.name, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), placeholder = { Text("پیام…") })
                        Button(enabled = draft.isNotBlank(), onClick = {
                            val text = draft.trim()
                            draft = ""
                            scope.launch {
                                val result = withContext(Dispatchers.IO) { runCatching { node!!.sendText(selected!!.sk, text) } }
                                if (result.isSuccess) {
                                    messages = withContext(Dispatchers.IO) { node!!.history(selected!!.sk, 200) }
                                } else {
                                    status = result.exceptionOrNull()?.message ?: "ارسال پیام ناموفق بود"
                                }
                            }
                        }) { Text("ارسال") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionContainerText(value: String) {
    SelectionContainer { Text(value, style = MaterialTheme.typography.bodySmall) }
}
