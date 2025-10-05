package com.example.myday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myday.ui.theme.MyDayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeName by viewModel.themeName.collectAsState()
            MyDayTheme(themeName = themeName) {
                when (val screen = viewModel.currentScreen) {
                    is Screen.Home -> HomeScreen(viewModel, screen.page)
                    is Screen.TaskLists -> TaskListsScreen(viewModel)
                    is Screen.Tasks -> TasksScreen(viewModel, screen.listId)
                    is Screen.Notes -> NotesScreen(viewModel)
                    is Screen.NoteDetail -> NoteDetailScreen(viewModel, screen.note)
                }
            }
        }
    }
}