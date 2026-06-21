package com.example.physical.data.model

data class SleepSchedule(
    val id: String = "",
    val userId: String = "",
    val workStartHour: Int = 8,
    val workStartMinute: Int = 0,
    val workEndHour: Int = 17,
    val workEndMinute: Int = 0,
    val suggestedSleepHour: Int = 0,
    val suggestedSleepMinute: Int = 0,
    val suggestedWakeHour: Int = 0,
    val suggestedWakeMinute: Int = 0,
    val consistencyScore: Int = 0
)
