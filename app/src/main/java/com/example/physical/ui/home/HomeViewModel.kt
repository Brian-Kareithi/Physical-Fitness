package com.example.physical.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physical.data.model.Run
import com.example.physical.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val isGuest: Boolean = false,
    val todayMorningRun: Run? = null,
    val todayEveningRun: Run? = null,
    val weeklyRuns: Int = 0,
    val weeklyDistance: Double = 0.0,
    val weeklyDuration: Long = 0
)

class HomeViewModel : ViewModel() {
    private val repository = FitnessRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

                _uiState.value = _uiState.value.copy(
                    todayMorningRun = todayRuns.find { it.type == "morning" },
                    todayEveningRun = todayRuns.find { it.type == "evening" },
                    weeklyRuns = weekRuns.size,
                    weeklyDistance = weekRuns.sumOf { it.distance },
                    weeklyDuration = weekRuns.sumOf { it.duration }
                )
            }
        }
    }
}
