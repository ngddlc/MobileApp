package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange

@Composable
fun ScheduleScreenWithGroupSelection(favorites: MutableList<String>) {
    var allGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var filteredGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<String>("") }
    var searchQuery by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Загрузка групп при старте
    LaunchedEffect(Unit) {
        try {
            allGroups = RetrofitInstance.groupApi.getAllGroups()
            filteredGroups = allGroups
        } catch (e: Exception) {
            error = "Не удалось загрузить группы"
        } finally {
            loading = false
        }
    }

    // Фильтрация при вводе
    LaunchedEffect(searchQuery) {
        filteredGroups = if (searchQuery.isBlank()) {
            allGroups
        } else {
            allGroups.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск группы") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Search)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(filteredGroups) { group ->
                    ListItem(
                        headlineContent = { Text(group) },
                        trailingContent = {
                            IconButton(onClick = {
                                if (favorites.contains(group)) {
                                    favorites.remove(group)
                                } else {
                                    favorites.add(group)
                                }
                            }) {
                                Icon(
                                    imageVector = if (favorites.contains(group)) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Outlined.FavoriteBorder
                                    },
                                    contentDescription = "Добавить в избранное"
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            selectedGroup = group
                        }
                    )
                }
            }
        }

        // Показ расписания
        if (selectedGroup.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
            var schedLoading by remember { mutableStateOf(true) }
            var schedError by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(selectedGroup) {
                try {
                    val (start, end) = getWeekDateRange()
                    schedule = RetrofitInstance.api.getSchedule(selectedGroup, start, end)
                } catch (e: Exception) {
                    schedError = "Ошибка загрузки расписания"
                } finally {
                    schedLoading = false
                }
            }

            if (schedLoading) {
                CircularProgressIndicator()
            } else if (schedError != null) {
                Text(schedError!!, color = MaterialTheme.colorScheme.error)
            } else {
                ScheduleList(schedule)
            }
        }
    }
}