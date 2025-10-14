package com.example.myday

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: MainViewModel) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showNewsCategoryDialog by remember { mutableStateOf(false) }
    
    val showGreeting by viewModel.showGreeting.collectAsState()
    val showQuote by viewModel.showQuote.collectAsState()
    val showNews by viewModel.showNews.collectAsState()
    val newsCategory by viewModel.newsCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Home Customization Section
            Text(
                "Home Screen Widgets",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggle(
                        title = "Greeting",
                        description = "Show personalized greeting",
                        checked = showGreeting,
                        onCheckedChange = { viewModel.setShowGreeting(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingToggle(
                        title = "Daily Quote",
                        description = "Show inspirational quote",
                        checked = showQuote,
                        onCheckedChange = { viewModel.setShowQuote(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingToggle(
                        title = "News Feed",
                        description = "Show latest news headlines",
                        checked = showNews,
                        onCheckedChange = { viewModel.setShowNews(it) }
                    )
                    if (showNews) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("News Category", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    newsCategory.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { showNewsCategoryDialog = true }) {
                                Text("Change")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Appearance Section
            Text(
                "Appearance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ThemeChanger(onClick = { showThemeDialog = true })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Calendar Sync Section
            Text(
                "Calendar Sync",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            GoogleCalendarSyncSection(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(24.dp))
            AppInfo()
        }
    }

    if (showThemeDialog) {
        ThemeSwitcherDialog(viewModel = viewModel, onDismiss = { showThemeDialog = false })
    }
    
    if (showNewsCategoryDialog) {
        NewsCategoryDialog(
            currentCategory = newsCategory,
            onCategorySelected = { category ->
                viewModel.setNewsCategory(category)
                showNewsCategoryDialog = false
            },
            onDismiss = { showNewsCategoryDialog = false }
        )
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ThemeChanger(onClick: () -> Unit) {
    Column {
        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("Change Theme")
        }
    }
}

@Composable
private fun GoogleCalendarSyncSection(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }
    var syncStatus by remember { mutableStateOf("") }
    var hasPermission by remember { mutableStateOf(false) }
    var availableCalendars by remember { mutableStateOf<List<CalendarInfo>>(emptyList()) }
    
    val calendarSync = remember { DeviceCalendarSync(context) }
    
    // Check permission on composition
    androidx.compose.runtime.LaunchedEffect(Unit) {
        hasPermission = calendarSync.hasCalendarPermission()
        if (hasPermission) {
            availableCalendars = calendarSync.getAvailableCalendars()
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            syncStatus = "Permission granted! Ready to sync."
            scope.launch {
                availableCalendars = calendarSync.getAvailableCalendars()
            }
        } else {
            syncStatus = "Calendar permission denied"
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!hasPermission) {
                Text(
                    "Sync your device calendar events with MyDay tasks",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = {
                        permissionLauncher.launch(android.Manifest.permission.READ_CALENDAR)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Calendar Permission")
                }
            } else {
                if (availableCalendars.isNotEmpty()) {
                    Text(
                        "Found ${availableCalendars.size} calendar(s):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    availableCalendars.take(3).forEach { cal ->
                        Text(
                            "• ${cal.name} (${cal.accountName})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            isSyncing = true
                            syncStatus = "Syncing upcoming events..."
                            val events = calendarSync.getAllUpcomingEvents(30)
                            // Import events as tasks
                            events.forEach { task ->
                                viewModel.importCalendarEvent(task)
                            }
                            syncStatus = "✓ Synced ${events.size} event${if (events.size != 1) "s" else ""} from your calendars"
                            isSyncing = false
                        }
                    },
                    enabled = !isSyncing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSyncing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Syncing...")
                        }
                    } else {
                        Text("Sync Calendar Events (Next 30 Days)")
                    }
                }
                
                if (syncStatus.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        syncStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (syncStatus.contains("✓")) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AppInfo() {
    Column {
        Text("About", style = MaterialTheme.typography.titleMedium)
        Text("MyDay App v1.0.0")
    }
}

@Composable
private fun ThemeSwitcherDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val themes = listOf(
        "Default Blue", 
        "Forest Green", 
        "Deep Purple", 
        "Ocean Blue",
        "Sunset Orange",
        "Rose Pink",
        "Teal Mint",
        "Midnight Dark"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a Theme") },
        text = {
            Column {
                themes.forEach { themeName ->
                    Text(
                        text = themeName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setTheme(themeName)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun NewsCategoryDialog(
    currentCategory: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = listOf(
        "general" to "General",
        "world" to "World Events",
        "politics" to "Politics",
        "technology" to "Technology",
        "business" to "Business",
        "entertainment" to "Entertainment",
        "sports" to "Sports",
        "science" to "Science",
        "health" to "Health"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("News Category") },
        text = {
            Column {
                categories.forEach { (id, name) ->
                    Text(
                        text = name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCategorySelected(id)
                            }
                            .padding(vertical = 12.dp),
                        color = if (id == currentCategory) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}