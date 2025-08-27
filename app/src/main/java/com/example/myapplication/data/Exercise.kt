package com.example.myapplication.data

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val tips: String,
    val frequency: String,
    var isCompleted: Boolean = false
)