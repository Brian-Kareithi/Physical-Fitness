package com.example.physical.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physical.data.model.SleepSchedule
import com.example.physical.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SleepUiState(
    val isGuest: Boolean = false,
    val workStartHour: Int = 8,
    val workStartMinute: Int = 0,
    val workEndHour: Int = 17,
    val workEndMinute: Int = 0,
    val suggestedSleepHour: Int = 0,
    val suggestedSleepMinute: Int = 0,
    val suggestedWakeHour: Int = 0,
    val suggestedWakeMinute: Int = 0,
    val showSuggestion: Boolean = false,
    val saved: Boolean = false
)

class SleepViewModel : ViewModel() {
    private val repository = FitnessRepository()

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    fun loadData(isGuest: Boolean) {
        _uiState.value = _uiState.value.copy(isGuest = isGuest)
    }

    fun updateWorkStart(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(
            workStartHour = hour,
            workStartMinute = minute,
            showSuggestion = false
        )
    }

    fun updateWorkEnd(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(
            workEndHour = hour,
            workEndMinute = minute,
            showSuggestion = false
        )
    }

    fun calculateSleepSchedule() {
        val state = _uiState.value
        val endTotalMinutes = state.workEndHour * 60 + state.workEndMinute
        val startTotalMinutes = state.workStartHour * 60 + state.workStartMinute

        var sleepHour: Int
        var sleepMinute: Int
        var wakeHour: Int
        var wakeMinute: Int

        if (endTotalMinutes >= 21 * 60) {
            sleepHour = state.workEndHour + 1
            sleepMinute = state.workEndMinute
            if (sleepHour >= 24) { sleepHour -= 24 }
            wakeHour = sleepHour + 7
            wakeMinute = sleepMinute
            if (wakeHour >= 24) { wakeHour -= 24 }
        } else if (endTotalMinutes >= 18 * 60) {
            sleepHour = state.workEndHour + 2
            sleepMinute = state.workEndMinute
            if (sleepHour >= 24) { sleepHour -= 24 }
            wakeHour = 6
            wakeMinute = 0
        } else {
            sleepHour = state.workEndHour + 3
            sleepMinute = state.workEndMinute
            if (sleepHour >= 24) { sleepHour -= 24 }
            val sleepTotal = sleepHour * 60 + sleepMinute
            val wakeTotal = startTotalMinutes - 60
            if (wakeTotal < 0) {
                wakeHour = 5
                wakeMinute = 0
            } else {
                val sleepDuration = wakeTotal - sleepTotal
                if (sleepDuration in 420..540) {
                    wakeHour = wakeTotal / 60
                    wakeMinute = wakeTotal % 60
                } else {
                    wakeHour = sleepHour + 8
                    wakeMinute = sleepMinute
                    if (wakeHour >= 24) { wakeHour -= 24 }
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            suggestedSleepHour = sleepHour,
            suggestedSleepMinute = sleepMinute,
            suggestedWakeHour = wakeHour,
            suggestedWakeMinute = wakeMinute,
            showSuggestion = true
        )
    }

    fun saveSchedule() {
        if (_uiState.value.isGuest) {
            _uiState.value = _uiState.value.copy(saved = true)
            return
        }

        val state = _uiState.value
        val schedule = SleepSchedule(
            workStartHour = state.workStartHour,
            workStartMinute = state.workStartMinute,
            workEndHour = state.workEndHour,
            workEndMinute = state.workEndMinute,
            suggestedSleepHour = state.suggestedSleepHour,
            suggestedSleepMinute = state.suggestedSleepMinute,
            suggestedWakeHour = state.suggestedWakeHour,
            suggestedWakeMinute = state.suggestedWakeMinute,
            consistencyScore = 100
        )

        viewModelScope.launch {
            repository.saveSleepSchedule(schedule)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }
}
