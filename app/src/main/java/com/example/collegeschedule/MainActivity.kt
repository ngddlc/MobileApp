package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.ui.schedule.ScheduleScreenWithGroupSelection
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp()
            }
        }
    }
}

// ------------------- App Destinations -------------------
enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    HOME("Расписание", Icons.Default.Home),
    FAVORITES("Избранное", Icons.Default.Favorite),
    PROFILE("Профиль", Icons.Default.AccountBox)
}

// ------------------- Main Composable -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeScheduleApp() {

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Глобальный список избранного (живёт пока приложение открыто)
    val favorites = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentDestination.label, style = MaterialTheme.typography.titleLarge) }
            )
        },
        bottomBar = {
            NavigationBar {
                AppDestinations.values().forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestinations.HOME -> ScheduleScreenWithGroupSelection(favorites)
                AppDestinations.FAVORITES -> FavoritesScreen(favorites)
                AppDestinations.PROFILE -> ProfileScreen()
            }
        }
    }
}

// ------------------- Favorites Screen -------------------
@Composable
fun FavoritesScreen(favorites: MutableList<String>) {
    Surface(modifier = Modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            Text(
                text = "Нет избранных групп",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(24.dp)
            )
        } else {
            Column(modifier = Modifier.padding(12.dp)) {
                favorites.forEach { group ->
                    Text(
                        text = group,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ------------------- Profile Screen -------------------
@Composable
fun ProfileScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Профиль студента",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(24.dp)
        )
    }
}