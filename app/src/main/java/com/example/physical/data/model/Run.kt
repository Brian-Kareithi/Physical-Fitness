package com.example.physical.data.model

data class Run(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val distance: Double = 0.0,
    val duration: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)
