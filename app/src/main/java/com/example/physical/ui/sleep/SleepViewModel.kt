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
    val step: Int = 1,
    val daysPerWeek: Int = 5,
    val hoursPerDay: Float = 8f,
    val workStartHour: Int = 8,
    val workStartMinute: Int = 0,
    val commuteMinutes: Int = 30,
    val jobTitle: String = "",
    val suggestedSleepTime: String = "",
    val suggestedWakeTime: String = "",
    val showSuggestion: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class SleepViewModel : ViewModel() {
    private val repository = FitnessRepository()

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    fun loadData(isGuest: Boolean) {
        _uiState.value = _uiState.value.copy(isGuest = isGuest)
    }

    fun updateDaysPerWeek(days: Int) {
        _uiState.value = _uiState.value.copy(daysPerWeek = days)
    }

    fun updateHoursPerDay(hours: Float) {
        _uiState.value = _uiState.value.copy(hoursPerDay = hours)
    }

    fun updateWorkStartHour(hour: Int) {
        _uiState.value = _uiState.value.copy(workStartHour = hour)
    }

    fun updateWorkStartMinute(minute: Int) {
        _uiState.value = _uiState.value.copy(workStartMinute = minute)
    }

    fun updateCommute(minutes: Int) {
        _uiState.value = _uiState.value.copy(commuteMinutes = minutes)
    }

    fun updateJobTitle(title: String) {
        _uiState.value = _uiState.value.copy(jobTitle = title)
    }

    fun nextStep() {
        val current = _uiState.value.step
        if (current < 5) {
            _uiState.value = _uiState.value.copy(step = current + 1)
        }
    }

    fun previousStep() {
        val current = _uiState.value.step
        if (current > 1) {
            _uiState.value = _uiState.value.copy(step = current - 1)
        }
    }

    fun calculate() {
        val state = _uiState.value
        val endHour = state.workStartHour + state.hoursPerDay.toInt()
        val endMinute = state.workStartMinute + ((state.hoursPerDay % 1) * 60).toInt()

        val sleepTimeMinutes = (endHour * 60 + endMinute + state.commuteMinutes + 60) % (24 * 60)
        val sleepHour = sleepTimeMinutes / 60
        val sleepMinute = sleepTimeMinutes % 60

        val wakeHour = if (state.workStartHour - 1 < 0) 5 else state.workStartHour - 1
        val wakeMinute = if (state.workStartHour - 1 < 0) 0 else 0

        _uiState.value = _uiState.value.copy(
            suggestedSleepTime = String.format("%02d:%02d", sleepHour, sleepMinute),
            suggestedWakeTime = String.format("%02d:%02d", wakeHour, wakeMinute),
            showSuggestion = true,
            step = 6
        )
    }

    fun saveSchedule() {
        if (_uiState.value.isGuest) {
            _uiState.value = _uiState.value.copy(saved = true)
            return
        }
        _uiState.value = _uiState.value.copy(saved = true)
    }

    fun commuteJoke(): String? {
        val min = _uiState.value.commuteMinutes
        return when {
            min >= 180 -> "Three hours?! You could fly to another country in that time. Hope you're catching up on some serious podcasts!"
            min >= 120 -> "Two hours each way? That's a part-time job just sitting in traffic. Maybe time to negotiate that remote work!"
            min >= 90 -> "An hour and a half? You could watch an entire movie. Hope your car has comfortable seats!"
            min >= 60 -> "An hour each way? Ouch. On the bright side, you can learn a new language with all that time."
            else -> null
        }
    }
}
