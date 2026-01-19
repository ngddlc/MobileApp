package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.utils.getWeekDateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenWithGroupSelection(favorites: MutableList<String>) {
    var allGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<String>("") }
    var expanded by remember { mutableStateOf(false) }

    // Загрузка групп при старте
    LaunchedEffect(Unit) {
        try {
            allGroups = RetrofitInstance.groupApi.getAllGroups()
        } catch (e: Exception) {
            // Можно добавить обработку ошибки позже
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Выпадающий список во всю ширину
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth() // ← Во всю ширину!
        ) {
            TextField(
                value = selectedGroup.ifEmpty { "Выберите группу" },
                onValueChange = { },
                readOnly = true,
                label = { Text("Группа") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth() // ← Во всю ширину!
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth() // ← Во всю ширину!
            ) {
                allGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        trailingIcon = {
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
                        onClick = {
                            selectedGroup = group
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth() // ← Во всю ширину!
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Показ расписания
            if (selectedGroup.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp)) // Небольшой отступ
                var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
                var loading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(selectedGroup) {
                    try {
                        val (start, end) = getWeekDateRange()
                        schedule = RetrofitInstance.api.getSchedule(selectedGroup, start, end)
                    } catch (e: Exception) {
                        error = "Ошибка загрузки расписания"
                    } finally {
                        loading = false
                    }
                }

                if (loading) {
                    CircularProgressIndicator()
                } else if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                } else {
                    ScheduleList(schedule)
                }
            }
        }
    }
}