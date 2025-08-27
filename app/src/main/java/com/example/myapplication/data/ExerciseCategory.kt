package com.example.myapplication.data

data class ExerciseCategory(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>
)