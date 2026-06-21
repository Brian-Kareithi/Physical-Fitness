package com.example.physical.data.repository

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.physical.data.model.ActivityData
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityTracker(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val activityRecognitionClient = ActivityRecognition.getClient(context)

    private val _activityData = MutableStateFlow(ActivityData())
    val activityData: StateFlow<ActivityData> = _activityData.asStateFlow()

    private var initialSteps = -1f
    private var currentActivityType = DetectedActivity.STILL
    private var walkingStartTime = 0L
    private var vehicleStartTime = 0L
    private var accumulatedWalkingMs = 0L
    private var accumulatedVehicleMs = 0L
    private var isTracking = false

    private val transitionPendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityTransitionReceiver::class.java)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun startTracking() {
        if (isTracking) return
        isTracking = true

        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        val request = ActivityTransitionRequest(transitions)
        activityRecognitionClient
            .removeActivityTransitionUpdates(transitionPendingIntent)
        activityRecognitionClient
            .requestActivityTransitionUpdates(request, transitionPendingIntent)
    }

    fun stopTracking() {
        if (!isTracking) return
        isTracking = false

        sensorManager.unregisterListener(this)
        activityRecognitionClient.removeActivityTransitionUpdates(transitionPendingIntent)
    }

    fun onActivityTransition(type: Int, transition: Int) {
        val now = System.currentTimeMillis()

        if (transition == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            when (type) {
                DetectedActivity.WALKING -> {
                    walkingStartTime = now
                    currentActivityType = DetectedActivity.WALKING
                }
                DetectedActivity.IN_VEHICLE -> {
                    vehicleStartTime = now
                    currentActivityType = DetectedActivity.IN_VEHICLE
                }
            }
        } else if (transition == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            when (type) {
                DetectedActivity.WALKING -> {
                    if (walkingStartTime > 0) {
                        accumulatedWalkingMs += now - walkingStartTime
                        walkingStartTime = 0
                    }
                }
                DetectedActivity.IN_VEHICLE -> {
                    if (vehicleStartTime > 0) {
                        accumulatedVehicleMs += now - vehicleStartTime
                        vehicleStartTime = 0
                    }
                }
            }
            currentActivityType = DetectedActivity.STILL
        }

        publishData()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialSteps < 0) {
                initialSteps = event.values[0]
            }
            val steps = (event.values[0] - initialSteps).toInt()
            _activityData.value = _activityData.value.copy(steps = steps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun publishData() {
        val now = System.currentTimeMillis()
        var walkMs = accumulatedWalkingMs
        var vehMs = accumulatedVehicleMs

        if (currentActivityType == DetectedActivity.WALKING && walkingStartTime > 0) {
            walkMs += now - walkingStartTime
        }
        if (currentActivityType == DetectedActivity.IN_VEHICLE && vehicleStartTime > 0) {
            vehMs += now - vehicleStartTime
        }

        _activityData.value = _activityData.value.copy(
            walkingMinutes = walkMs / 60000,
            vehicleMinutes = vehMs / 60000
        )
    }

    class ActivityTransitionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent) ?: return
                val tracker = instance
                for (event in result.transitionEvents) {
                    tracker?.onActivityTransition(
                        event.activityType,
                        event.transitionType
                    )
                }
            }
        }
    }

    companion object {
        var instance: ActivityTracker? = null
    }
}
