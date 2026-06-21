package com.example.physical.ui.runs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physical.data.model.Run
import com.example.physical.data.repository.FitnessRepository
import com.example.physical.data.repository.HomeLocationManager
import com.example.physical.data.repository.RunTracker
import com.example.physical.data.repository.SuggestedRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RunUiState(
    val runs: List<Run> = emptyList(),
    val isGuest: Boolean = false,
    val isLoading: Boolean = false,
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Long = 0,
    val distanceKm: Double = 0.0,
    val pace: String = "--",
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val suggestedRoutes: List<SuggestedRoute> = emptyList(),
    val homeAddress: String? = null
)

class RunViewModel : ViewModel() {
    private val repository = FitnessRepository()

    private val _uiState = MutableStateFlow(RunUiState())
    val uiState: StateFlow<RunUiState> = _uiState.asStateFlow()

    fun loadData(runType: String, isGuest: Boolean) {
        _uiState.value = _uiState.value.copy(isGuest = isGuest, suggestedRoutes = emptyList())
        if (!isGuest) {
            loadRuns(runType)
        }
        loadHomeData()
        observeTracking()
    }

    private fun observeTracking() {
        val tracker = RunTracker.instance ?: return
        viewModelScope.launch {
            tracker.trackingState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    isTracking = state.isRunning,
                    elapsedSeconds = state.elapsedSeconds,
                    distanceKm = state.distanceKm,
                    pace = state.paceMinPerKm
                )
            }
        }
    }

    private fun loadHomeData() {
        val homeManager = HomeLocationManager.instance
        val address = homeManager?.getHomeAddress()
        val routes = homeManager?.getSuggestedRoutes() ?: emptyList()
        _uiState.value = _uiState.value.copy(
            homeAddress = address,
            suggestedRoutes = routes
        )
    }

    private fun loadRuns(type: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getRuns(type)
            result.onSuccess { runs ->
                _uiState.value = _uiState.value.copy(runs = runs, isLoading = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun startRun() {
        val tracker = RunTracker.instance ?: return
        tracker.startRun()
        _uiState.value = _uiState.value.copy(isPaused = false, saveSuccess = false)
    }

    fun pauseRun() {
        RunTracker.instance?.pauseRun()
        _uiState.value = _uiState.value.copy(isPaused = true)
    }

    fun resumeRun() {
        RunTracker.instance?.resumeRun()
        _uiState.value = _uiState.value.copy(isPaused = false)
    }

    fun stopRun(runType: String) {
        val result = RunTracker.instance?.stopRun() ?: return
        val distance = String.format("%.2f", result.distanceKm).toDouble()
        val duration = result.durationSeconds / 60

        if (_uiState.value.isGuest) {
            _uiState.value = _uiState.value.copy(
                saveSuccess = true,
                isTracking = false,
                isPaused = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val run = Run(
                type = runType,
                distance = distance,
                duration = if (duration < 1) 1 else duration
            )
            val saveResult = repository.saveRun(run)
            saveResult.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveSuccess = true,
                    isTracking = false,
                    isPaused = false
                )
                loadRuns(runType)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                    isTracking = false,
                    isPaused = false
                )
            }
        }
    }

    fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else String.format("%02d:%02d", m, s)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
