package com.example.physical.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physical.data.model.ActivityData
import com.example.physical.data.model.Run
import com.example.physical.data.repository.ActivityTracker
import com.example.physical.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val isGuest: Boolean = false,
    val todayRuns: Int = 0,
    val todayDistance: Double = 0.0,
    val weeklyRuns: Int = 0,
    val weeklyDistance: Double = 0.0,
    val weeklyDuration: Long = 0,
    val totalRuns: Int = 0,
    val totalDistance: Double = 0.0,
    val bestDistance: Double = 0.0,
    val avgPace: String = "--",
    val activityData: ActivityData = ActivityData()
)

class HomeViewModel : ViewModel() {
    private val repository = FitnessRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ActivityTracker.instance?.activityData?.collectLatest { data ->
                _uiState.value = _uiState.value.copy(activityData = data)
            }
        }
    }

    fun loadData(userName: String, isGuest: Boolean) {
        _uiState.value = _uiState.value.copy(userName = userName, isGuest = isGuest)
        if (!isGuest) {
            loadRuns()
        }
    }

    private fun loadRuns() {
        viewModelScope.launch {
            val result = repository.getRuns()
            result.onSuccess { runs ->
                val calendar = java.util.Calendar.getInstance()
                val todayStart = calendar.apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.timeInMillis

                val todayEnd = todayStart + 86400000
                val todayRuns = runs.filter { it.date in todayStart until todayEnd }
                val weekAgo = System.currentTimeMillis() - 7 * 86400000
                val weekRuns = runs.filter { it.date >= weekAgo }

                val totalDistance = runs.sumOf { it.distance }
                val best = runs.maxOfOrNull { it.distance } ?: 0.0
                val avgPace = if (runs.isNotEmpty()) {
                    val totalMin = runs.sumOf { it.duration }
                    val pace = if (totalDistance > 0) totalMin.toDouble() / totalDistance else 0.0
                    if (pace > 0) String.format("%.1f", pace) else "--"
                } else "--"

                _uiState.value = _uiState.value.copy(
                    todayRuns = todayRuns.size,
                    todayDistance = todayRuns.sumOf { it.distance },
                    weeklyRuns = weekRuns.size,
                    weeklyDistance = weekRuns.sumOf { it.distance },
                    weeklyDuration = weekRuns.sumOf { it.duration },
                    totalRuns = runs.size,
                    totalDistance = totalDistance,
                    bestDistance = best,
                    avgPace = avgPace
                )
            }
        }
    }
}
