package com.example.myday

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showSecurityQuestionDialog by remember { mutableStateOf(false) }
    
    val showGreeting by viewModel.showGreeting.collectAsState()
    val showQuote by viewModel.showQuote.collectAsState()
    val showNews by viewModel.showNews.collectAsState()
    val newsCategory by viewModel.newsCategory.collectAsState()
    val secureNotesPin by viewModel.secureNotesPin.collectAsState(initial = null)
    val securityQuestion by viewModel.securityQuestion.collectAsState(initial = null)

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
            
            // Security Section
            Text(
                "Security",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            SecuritySection(
                hasPin = !secureNotesPin.isNullOrBlank(),
                hasSecurityQuestion = !securityQuestion.isNullOrBlank(),
                onChangePinClick = { showChangePinDialog = true },
                onSetupRecoveryClick = { showSecurityQuestionDialog = true }
            )
            
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
    
    if (showChangePinDialog) {
        ChangePinDialog(
            currentPin = secureNotesPin,
            onPinChanged = { newPin ->
                viewModel.setSecureNotesPin(newPin)
                showChangePinDialog = false
            },
            onDismiss = { showChangePinDialog = false }
        )
    }
    
    if (showSecurityQuestionDialog) {
        SecurityQuestionDialog(
            currentQuestion = securityQuestion,
            onSave = { question, answer ->
                viewModel.setSecurityQuestion(question)
                viewModel.setSecurityAnswer(answer)
                showSecurityQuestionDialog = false
            },
            onDismiss = { showSecurityQuestionDialog = false }
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

@Composable
private fun SecuritySection(
    hasPin: Boolean,
    hasSecurityQuestion: Boolean,
    onChangePinClick: () -> Unit,
    onSetupRecoveryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Secure Notes Protection",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (hasPin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Change PIN", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Update your secure notes PIN",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = onChangePinClick) {
                        Text("Change")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (hasSecurityQuestion) "Update Recovery" else "Setup Recovery",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            if (hasSecurityQuestion) "Change security question" else "Add security question for PIN recovery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = onSetupRecoveryClick) {
                        Text(if (hasSecurityQuestion) "Update" else "Setup")
                    }
                }
            } else {
                Text(
                    "No PIN set yet. Create your first secure note to set up a PIN.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChangePinDialog(
    currentPin: String?,
    onPinChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                androidx.compose.material.icons.Icons.Default.Lock,
                contentDescription = "Change PIN",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Change PIN") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Enter your current PIN and choose a new one",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = oldPinInput,
                    onValueChange = { 
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            oldPinInput = it
                            error = null
                        }
                    },
                    label = { Text("Current PIN") },
                    placeholder = { Text("Enter current PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                    ),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                
                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = { 
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            newPinInput = it
                            error = null
                        }
                    },
                    label = { Text("New PIN") },
                    placeholder = { Text("Enter new PIN (4-6 digits)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                    ),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                
                OutlinedTextField(
                    value = confirmPinInput,
                    onValueChange = { 
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            confirmPinInput = it
                            error = null
                        }
                    },
                    label = { Text("Confirm New PIN") },
                    placeholder = { Text("Re-enter new PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                    ),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        oldPinInput != currentPin -> {
                            error = "Current PIN is incorrect"
                        }
                        newPinInput.length !in 4..6 -> {
                            error = "New PIN must be 4-6 digits"
                        }
                        newPinInput != confirmPinInput -> {
                            error = "New PINs don't match"
                        }
                        newPinInput == oldPinInput -> {
                            error = "New PIN must be different from current PIN"
                        }
                        else -> {
                            onPinChanged(newPinInput)
                        }
                    }
                },
                enabled = oldPinInput.length >= 4 && newPinInput.length >= 4 && confirmPinInput.length >= 4
            ) {
                Text("Change PIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SecurityQuestionDialog(
    currentQuestion: String?,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val securityQuestions = listOf(
        "What was the name of your first pet?",
        "What city were you born in?",
        "What is your mother's maiden name?",
        "What was the name of your elementary school?",
        "What is your favorite book?",
        "What was your childhood nickname?",
        "In what city did you meet your spouse/partner?",
        "What is the name of your favorite childhood friend?",
        "Custom question..."
    )
    
    var selectedQuestion by remember { mutableStateOf(currentQuestion ?: securityQuestions[0]) }
    var customQuestion by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showCustomInput by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                androidx.compose.material.icons.Icons.Default.Lock,
                contentDescription = "Security Question",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Setup PIN Recovery") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Choose a security question to recover your PIN if forgotten",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    "Security Question",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                securityQuestions.forEach { question ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (question == "Custom question...") {
                                    showCustomInput = true
                                    selectedQuestion = ""
                                } else {
                                    showCustomInput = false
                                    selectedQuestion = question
                                }
                                error = null
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedQuestion == question || (showCustomInput && question == "Custom question..."),
                            onClick = {
                                if (question == "Custom question...") {
                                    showCustomInput = true
                                    selectedQuestion = ""
                                } else {
                                    showCustomInput = false
                                    selectedQuestion = question
                                }
                                error = null
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            question,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                if (showCustomInput) {
                    OutlinedTextField(
                        value = customQuestion,
                        onValueChange = { 
                            customQuestion = it
                            error = null
                        },
                        label = { Text("Your Custom Question") },
                        placeholder = { Text("Enter your security question") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null,
                        singleLine = false,
                        maxLines = 3
                    )
                }
                
                OutlinedTextField(
                    value = answer,
                    onValueChange = { 
                        answer = it
                        error = null
                    },
                    label = { Text("Answer") },
                    placeholder = { Text("Enter your answer") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = false
                )
                
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalQuestion = if (showCustomInput) customQuestion else selectedQuestion
                    when {
                        finalQuestion.isBlank() -> {
                            error = "Please select or enter a security question"
                        }
                        answer.isBlank() -> {
                            error = "Please enter an answer"
                        }
                        answer.length < 3 -> {
                            error = "Answer must be at least 3 characters"
                        }
                        else -> {
                            onSave(finalQuestion, answer.lowercase().trim())
                        }
                    }
                },
                enabled = answer.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}