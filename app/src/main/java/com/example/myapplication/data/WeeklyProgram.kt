package com.example.myapplication.data

data class WeeklyProgram(
    val weekNumber: Int,
    val focus: String,
    val categories: List<ExerciseCategory>
)