package com.example.physical.ui.runs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physical.data.model.Run
import com.example.physical.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RunUiState(
    val runs: List<Run> = emptyList(),
    val isGuest: Boolean = false,
    val isLoading: Boolean = false,
    val distanceInput: String = "",
    val durationInput: String = "",
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class RunViewModel : ViewModel() {
    private val repository = FitnessRepository()

    private val _uiState = MutableStateFlow(RunUiState())
    val uiState: StateFlow<RunUiState> = _uiState.asStateFlow()

    fun loadRuns(type: String, isGuest: Boolean) {
        _uiState.value = _uiState.value.copy(isGuest = isGuest)
        if (!isGuest) {
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
    }

    fun updateDistance(value: String) {
        _uiState.value = _uiState.value.copy(distanceInput = value, saveSuccess = false)
    }

    fun updateDuration(value: String) {
        _uiState.value = _uiState.value.copy(durationInput = value, saveSuccess = false)
    }

    fun saveRun(type: String) {
        val distance = _uiState.value.distanceInput.toDoubleOrNull() ?: return
        val duration = _uiState.value.durationInput.toLongOrNull() ?: return
        if (distance <= 0 || duration <= 0) return

        if (_uiState.value.isGuest) {
            _uiState.value = _uiState.value.copy(
                saveSuccess = true,
                distanceInput = "",
                durationInput = ""
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val run = Run(
                type = type,
                distance = distance,
                duration = duration
            )
            val result = repository.saveRun(run)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveSuccess = true,
                    distanceInput = "",
                    durationInput = ""
                )
                loadRuns(type, false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
