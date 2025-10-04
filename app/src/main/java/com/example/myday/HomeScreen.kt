@file:OptIn(UnstableApi::class, ExperimentalFoundationApi::class)

package com.example.myday

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.CoroutineScope

// Helper functions to convert between legacy Date and modern LocalDate
fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSwitcherDialog(viewModel = viewModel, onDismiss = { showThemeDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Day") },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showThemeDialog = true
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Notes") },
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(2) }
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Tabs(pagerState = pagerState, scope = scope)
            Pager(pagerState = pagerState, viewModel = viewModel)
        }
    }
}

@Composable
fun Tabs(pagerState: PagerState, scope: CoroutineScope) {    val titles = listOf("Calendar", "Tasks", "Notes")

    TabRow(
        selectedTabIndex = pagerState.currentPage,indicator = { tabPositions ->
            // Check if the tabPositions list is not empty to avoid an index out of bounds exception.
            if (tabPositions.isNotEmpty()) {
                TabRowDefaults.Indicator(
                    // Correctly access the TabPosition using the current page index.
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
            )
        }
    }
}

@Composable
fun Pager(pagerState: PagerState, viewModel: MainViewModel) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> CalendarScreen(viewModel = viewModel)
            1 -> TaskListsScreen(viewModel = viewModel)
            2 -> NotesScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun CalendarScreen(viewModel: MainViewModel) {
    val tasks: List<Task> by viewModel.tasks.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    if (showAddTaskDialog) {
        AddTaskFromHomeDialog(
            viewModel = viewModel,
            onDismiss = { showAddTaskDialog = false },
            selectedDate = selectedDate.toDate()
        )
    }

    val tasksForSelectedDate by remember(tasks, selectedDate) {
        mutableStateOf(tasks.filter { task ->
            try {
                // Assuming task.dateTime is "yyyy-MM-dd HH:mm:ss"
                val taskDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.dateTime.substring(0, 10))
                taskDate?.toLocalDate() == selectedDate
            } catch (e: Exception) {
                false
            }
        })
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "My Calendar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SimpleCalendarView(
                    tasks = tasks,
                    selectedDate = selectedDate.toDate(),
                    onDateClick = { date ->
                        selectedDate = date.toLocalDate()
                    },
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    onMonthChange = { month, year ->
                        currentMonth = month
                        currentYear = year
                    }
                )
            }
        }
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TaskViewer(
                    selectedDate = selectedDate,
                    tasks = tasksForSelectedDate,
                    onAddTaskClicked = { showAddTaskDialog = true },
                    onToggleTask = { task ->
                        viewModel.toggleTaskCompleted(task.id)
                    }
                )
            }
        }
    }
}

/**
 * A placeholder screen to display a list of all tasks.
 */
@Composable
fun TasksScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No tasks yet. Add one from the calendar!")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "All Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(tasks, key = { it.id }) { task ->
                TaskItem(task = task, onToggle = { viewModel.toggleTaskCompleted(task.id) })
            }
        }
    }
}

@Composable
private fun ThemeSwitcherDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val themes = listOf("Default Blue", "Forest Green", "Deep Purple")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskFromHomeDialog(viewModel: MainViewModel, onDismiss: () -> Unit, selectedDate: Date?) {
    var taskDescription by remember { mutableStateOf("") }
    val taskLists by viewModel.taskLists.collectAsState()

    // Correctly find the list based on the current state
    val generalList = taskLists.find { it.name == "General" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    generalList?.let {
                        viewModel.addTask(
                            description = taskDescription,
                            listId = it.id,
                            date = selectedDate
                        )
                        onDismiss()
                    }
                },
                enabled = taskDescription.isNotBlank() && generalList != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class CalendarDay(val dayNumberText: String, val date: Date)

@Composable
fun SimpleCalendarView(
    tasks: List<Task>,
    selectedDate: Date?,
    onDateClick: (Date) -> Unit,
    currentMonth: Int,
    currentYear: Int,
    onMonthChange: (Int, Int) -> Unit
) {
    val monthStartDate = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    fun goToNextMonth() {
        val newCalendar = Calendar.getInstance().apply {
            time = monthStartDate.time
            add(Calendar.MONTH, 1)
        }
        onMonthChange(newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.YEAR))
    }

    fun goToPreviousMonth() {
        val newCalendar = Calendar.getInstance().apply {
            time = monthStartDate.time
            add(Calendar.MONTH, -1)
        }
        onMonthChange(newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.YEAR))
    }

    val daysInMonth = monthStartDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeekSystem = Calendar.getInstance().firstDayOfWeek
    val firstDayOfWeekOfMonth = monthStartDate.get(Calendar.DAY_OF_WEEK)
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthStartDate.time)
    val emptyCells = (firstDayOfWeekOfMonth - firstDayOfWeekSystem + 7) % 7

    val dayCellsData = remember(currentYear, currentMonth) {
        (1..emptyCells).map { null } + (1..daysInMonth).map { day ->
            val dayCalendar = Calendar.getInstance().apply {
                time = monthStartDate.time
                set(Calendar.DAY_OF_MONTH, day)
            }
            CalendarDay(day.toString(), dayCalendar.time)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = ::goToPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = ::goToNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val dayFormatter = SimpleDateFormat("E", Locale.getDefault())
            val tempCal = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeekSystem) }
            repeat(7) {
                Text(
                    text = dayFormatter.format(tempCal.time).take(1), // Single letter for day
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                tempCal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
           
            modifier = Modifier.height(260.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
        ) {
            items(dayCellsData) { calendarDayData ->
                if (calendarDayData != null) {
                    val isSelected = selectedDate?.toLocalDate() == calendarDayData.date.toLocalDate()
                    val tasksForDayCount = tasks.count { task ->
                        try {
                            task.dateTime.startsWith(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendarDayData.date))
                        } catch (e: Exception) {
                            false
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .clickable { onDateClick(calendarDayData.date) }
                    ) {
                        Text(
                            text = calendarDayData.dayNumberText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (tasksForDayCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 4.dp)
                                    .size(6.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.aspectRatio(1f)) // Empty cell
                }
            }
        }
    }
}


@Composable
fun TaskViewer(
    selectedDate: LocalDate,
    tasks: List<Task>,
    onAddTaskClicked: () -> Unit,
    onToggleTask: (Task) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("eeee, MMMM d")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tasks for", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        selectedDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onAddTaskClicked,
                    modifier = Modifier.clip(CircleShape),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Divider()

            if (tasks.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.heightIn(min = 100.dp, max = 250.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(task = task, onToggle = { onToggleTask(task) })
                    }
                }
            } else {
                EmptyState()
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = "Task Status",
            tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else LocalContentColor.current.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = task.description,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted) LocalContentColor.current.copy(alpha = 0.5f) else LocalContentColor.current,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = task.dateTime, // Note: You may want to format this date/time string
                color = LocalContentColor.current.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "No tasks",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No tasks for this day.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            "Enjoy your free time!",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}