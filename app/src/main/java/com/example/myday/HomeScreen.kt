package com.example.myday


import android.R.attr.enabled
import android.R.attr.type
import androidx.media3.common.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import java.util.Date
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val importantTasks = viewModel.tasks.value.filter { it.priority != Priority.NONE }
    val groupedTasks = importantTasks.groupBy { it.listId }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedDateForTask by remember { mutableStateOf<Date?>(null) }
    val calendar = Calendar.getInstance()
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                actions = {
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Change Theme")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true
                selectedDateForTask = null}) {
                Icon(Icons.Default.Add, contentDescription = "Add a task")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                Button(
                    onClick = { viewModel.onNavigateToTaskLists() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("View All Task Lists")
                }
            }
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
                        tasksWithDates = viewModel.tasksWithDates,
                        selectedDate = selectedDateForTask,
                        onDateClick = { date ->
                            selectedDateForTask = date
                            showAddTaskDialog = true  // Show the dialog when a date is clicked
                        },
                        currentMonth = currentMonth,
                        currentYear = currentYear,
                        onMonthChange = { month, year ->
                            currentMonth = month
                            currentYear = year
                        },
                        totalSpacingHeight = 16.dp
                    )
            }
                }


        groupedTasks.forEach { (listId, tasks) ->
                val listName = viewModel.taskLists.value.find { it.id == listId }?.name ?: "Unknown List"

            item {
                    Text(
                        text = listName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(tasks) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriorityIndicator(priority = task.priority) {}
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.description,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(
                                text = task.dateTime,
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemeSwitcherDialog(viewModel) { showThemeDialog = false }
    }

    if (showAddTaskDialog) {
        AddTaskFromHomeDialog(viewModel, { showAddTaskDialog = false }, selectedDateForTask)
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
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedList by remember { mutableStateOf<TaskList?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description") }
                )

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    TextField(
                        value = selectedList?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Add to list") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        viewModel.taskLists.value.forEach { list ->
                            DropdownMenuItem(
                                text = { Text(list.name) },
                                onClick = {
                                    selectedList = list
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedList?.let {
                        viewModel.addTask(taskDescription, it.id, selectedDate)
                        onDismiss()
                    }
                },
                enabled = taskDescription.isNotBlank() && selectedList != null
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
    tasksWithDates: List<Date>,
    selectedDate: Date?,
    onDateClick: (Date) -> Unit,
    currentMonth: Int,
    currentYear: Int,
    onMonthChange: (Int, Int) -> Unit,
    totalSpacingHeight: Dp // This parameter is currently unused in the grid height calculation.
    // It could be used for other spacing within this composable.
) {
    val monthStartDate = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    fun goToNextMonth() {
        val newCalendar = Calendar.getInstance().apply {
            time = monthStartDate.time // Start from the current month's start date
            add(Calendar.MONTH, 1)
        }
        val nextMonth = newCalendar.get(Calendar.MONTH)
        val nextYear = newCalendar.get(Calendar.YEAR)
        onMonthChange(nextMonth, nextYear)
    }

    fun goToPreviousMonth() {
        val newCalendar = Calendar.getInstance().apply {
            time = monthStartDate.time // Start from the current month's start date
            add(Calendar.MONTH, -1)
        }
        val prevMonth = newCalendar.get(Calendar.MONTH)
        val prevYear = newCalendar.get(Calendar.YEAR)
        onMonthChange(prevMonth, prevYear)
    }

    val daysInMonth = monthStartDate.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeekSystem = Calendar.getInstance().firstDayOfWeek
    val firstDayOfWeekOfMonth = monthStartDate.get(Calendar.DAY_OF_WEEK)

    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthStartDate.time)

    val emptyCells = (firstDayOfWeekOfMonth - firstDayOfWeekSystem + 7) % 7
    val dayCellsData = remember(currentYear, currentMonth, emptyCells, daysInMonth) {
        val cells = mutableListOf<CalendarDay?>()
        repeat(emptyCells) { cells.add(null) }
        for (day in 1..daysInMonth) {
            val dayCalendar = Calendar.getInstance().apply {
                time = monthStartDate.time
                add(Calendar.DAY_OF_MONTH, day - 1)
            }
            cells.add(CalendarDay(day.toString(), dayCalendar.time))
        }
        cells
    }

    Log.d(
        "SimpleCalendarView",
        "Month: $monthName, Days: $daysInMonth, Empty: $emptyCells, 1stDayOfMonthInWeek: $firstDayOfWeekOfMonth, System1stDay: $firstDayOfWeekSystem"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { goToPreviousMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            IconButton(onClick = { goToNextMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val dayFormatter = remember { SimpleDateFormat("E", Locale.getDefault()) }
            val daysOfWeekHeader = remember(firstDayOfWeekSystem) {
                val tempDisplayCal = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeekSystem)
                }
                List(7) {
                    val dayName = dayFormatter.format(tempDisplayCal.time)
                    tempDisplayCal.add(Calendar.DAY_OF_MONTH, 1)
                    dayName
                }
            }
            daysOfWeekHeader.forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Vertical spacer

        if (dayCellsData.isNotEmpty()) {
            val numberOfRows = (dayCellsData.size + 6) / 7
            val cellHeight = 48.dp
            val gridVerticalSpacing = 4.dp // Spacing between rows in the grid

            val actualRows = if (numberOfRows > 0) numberOfRows else 1
            val totalCellsOnlyHeight = cellHeight * actualRows

            // Calculate total vertical spacing within the grid
            val gridInternalVerticalSpacing = if (actualRows > 1) {
                gridVerticalSpacing * (actualRows - 1)
            } else {
                0.dp
            }
            val gridHeight = totalCellsOnlyHeight + gridInternalVerticalSpacing

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight), // Use the calculated gridHeight
                verticalArrangement = Arrangement.spacedBy(gridVerticalSpacing),
                horizontalArrangement = Arrangement.spacedBy(4.dp), // Spacing between columns
                userScrollEnabled = false,
            ) {
                items(dayCellsData) { calendarDayData ->
                    if (calendarDayData != null) {
                        val isSelected = selectedDate?.let {
                            val cal1 = Calendar.getInstance().apply { time = it }
                            val cal2 = Calendar.getInstance().apply { time = calendarDayData.date }
                            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                        } ?: false

                        val hasTasks = tasksWithDates.any { taskDate ->
                            val cal1 = Calendar.getInstance().apply { time = taskDate }
                            val cal2 = Calendar.getInstance().apply { time = calendarDayData.date }
                            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onDateClick(calendarDayData.date) }
                        ) {
                            Text(
                                text = calendarDayData.dayNumberText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (hasTasks) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 4.dp) // Adjust padding for the indicator
                                        .size(4.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.aspectRatio(1f)) // Empty cell
                    }
                }
            }
        } else {
            Text("Calendar data is unavailable.", modifier = Modifier.padding(16.dp))
            Log.e("SimpleCalendarView", "Calendar data (dayCells) is unavailable or empty.")
        }
    }
    // Note: The data class 'calendarDay' was defined at the top of this snippet.
    // If you had 'data class calendarDay(...)' here, it would be a local class,
    // which is fine, but make sure its usage in dayCellsData.add(...) matches.
    // I used 'CalendarDay' (capital C) assuming the top-level definition.
}
