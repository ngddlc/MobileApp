package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.schedule.ScheduleList
import com.example.collegeschedule.ui.schedule.ScheduleScreenWithGroupSelection
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme
import com.example.collegeschedule.utils.getWeekDateRange

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

//App Destinations
enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    HOME("Расписание", Icons.Default.Home),
    FAVORITES("Избранное", Icons.Default.Favorite),
    PROFILE("Профиль", Icons.Default.AccountBox)
}

//Main Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeScheduleApp() {

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Глобальный список избранного
    val favorites = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentDestination.label, style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            NavigationBar {
                AppDestinations.values().forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

//Favorites Screen
@Composable
fun FavoritesScreen(favorites: MutableList<String>) {
    var selectedGroup by remember { mutableStateOf<String>("") }
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Избранное",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Список избранных групп — прокручиваемый
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (favorites.isEmpty()) {
                    item {
                        Text(
                            text = "Нет избранных групп",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(favorites) { group ->
                        ListItem(
                            headlineContent = { Text(group) },
                            modifier = Modifier.clickable {
                                selectedGroup = group
                            }
                        )
                    }
                }
            }
        }

        // Расписание выбранной группы
        if (selectedGroup.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Расписание для: $selectedGroup",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var scheduleLoading by remember { mutableStateOf(true) }
            var scheduleError by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(selectedGroup) {
                try {
                    scheduleLoading = true
                    val (start, end) = getWeekDateRange()
                    schedule = RetrofitInstance.api.getSchedule(selectedGroup, start, end)
                } catch (e: Exception) {
                    scheduleError = "Ошибка загрузки расписания"
                } finally {
                    scheduleLoading = false
                }
            }

            if (scheduleLoading) {
                CircularProgressIndicator()
            } else if (scheduleError != null) {
                Text(scheduleError!!, color = MaterialTheme.colorScheme.error)
            } else {
                ScheduleList(schedule)
            }
        }
    }
}

//Profile Screen
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Профиль студента",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Здесь может быть ваша информация",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}