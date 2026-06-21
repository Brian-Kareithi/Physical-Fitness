package com.example.physical.data.repository

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RunTrackingState(
    val isRunning: Boolean = false,
    val elapsedSeconds: Long = 0,
    val distanceKm: Double = 0.0,
    val paceMinPerKm: String = "--",
    val currentLocation: Location? = null
)

class RunTracker(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _trackingState = MutableStateFlow(RunTrackingState())
    val trackingState: StateFlow<RunTrackingState> = _trackingState.asStateFlow()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                onNewLocation(location)
            }
        }
    }

    private var lastLocation: Location? = null
    private var startTime: Long = 0L
    private var totalDistanceMeters = 0.0
    private var isTracking = false
    private var accumulatedSeconds = 0L
    private var lastPauseTime: Long = 0L

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 2000
    ).apply {
        setMinUpdateIntervalMillis(1000)
        setMaxUpdateDelayMillis(5000)
    }.build()

    fun startRun() {
        if (isTracking) return
        isTracking = true
        startTime = System.currentTimeMillis()
        totalDistanceMeters = 0.0
        lastLocation = null
        accumulatedSeconds = 0L
        lastPauseTime = 0L

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )

        _trackingState.value = RunTrackingState(
            isRunning = true,
            elapsedSeconds = 0,
            distanceKm = 0.0,
            paceMinPerKm = "--"
        )
    }

    fun pauseRun() {
        if (!isTracking) return
        isTracking = false
        lastPauseTime = System.currentTimeMillis()
        fusedLocationClient.removeLocationUpdates(locationCallback)

        val currentElapsed = (lastPauseTime - startTime) / 1000
        _trackingState.value = _trackingState.value.copy(
            isRunning = false,
            elapsedSeconds = currentElapsed
        )
    }

    fun resumeRun() {
        if (isTracking) return
        isTracking = true
        if (lastPauseTime > 0) {
            startTime += System.currentTimeMillis() - lastPauseTime
        }
        lastPauseTime = 0L

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )

        _trackingState.value = _trackingState.value.copy(isRunning = true)
    }

    fun stopRun(): RunResult {
        pauseRun()
        isTracking = false

        val elapsedMs = _trackingState.value.elapsedSeconds * 1000
        val distance = totalDistanceMeters / 1000.0
        val pace = if (distance > 0) {
            val paceMin = (elapsedMs / 1000.0) / 60.0 / distance
            String.format("%d:%02d", paceMin.toInt(), ((paceMin % 1) * 60).toInt())
        } else "--:--"

        reset()

        return RunResult(
            distanceKm = distance,
            durationSeconds = _trackingState.value.elapsedSeconds,
            pace = pace
        )
    }

    private fun onNewLocation(location: Location) {
        if (!isTracking) return

        if (lastLocation != null) {
            val distance = location.distanceTo(lastLocation!!).toDouble()
            if (distance > 0 && distance < 200) {
                totalDistanceMeters += distance
            }
        }
        lastLocation = location

        val elapsed = (System.currentTimeMillis() - startTime) / 1000
        val distanceKm = totalDistanceMeters / 1000.0
        val pace = if (distanceKm > 0) {
            val paceMin = elapsed.toDouble() / 60.0 / distanceKm
            String.format("%d:%02d", paceMin.toInt(), ((paceMin % 1) * 60).toInt())
        } else "--"

        _trackingState.value = RunTrackingState(
            isRunning = true,
            elapsedSeconds = elapsed,
            distanceKm = distanceKm,
            paceMinPerKm = pace,
            currentLocation = location
        )
    }

    private fun reset() {
        totalDistanceMeters = 0.0
        lastLocation = null
        startTime = 0L
        accumulatedSeconds = 0L
        lastPauseTime = 0L
        _trackingState.value = RunTrackingState()
    }

    data class RunResult(
        val distanceKm: Double,
        val durationSeconds: Long,
        val pace: String
    )

    companion object {
        var instance: RunTracker? = null
    }
}
