package com.example.collegeschedule.data.dto

import com.example.collegeschedule.data.dto.LessonDto

data class ScheduleByDateDto(
    val lessonDate: String,
    val weekday: String,
    val lessons: List<LessonDto>
)