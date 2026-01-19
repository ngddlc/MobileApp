package com.example.collegeschedule.data.dto

data class LessonDto(
    val lessonNumber: Int,
    val time: String,
    val groupParts: Map<String, LessonPartDto?>
)