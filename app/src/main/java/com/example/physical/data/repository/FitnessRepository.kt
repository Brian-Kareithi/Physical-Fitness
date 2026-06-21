package com.example.physical.data.repository

import com.example.physical.data.model.Run
import com.example.physical.data.model.SleepSchedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FitnessRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId: String?
        get() = auth.currentUser?.uid

    suspend fun saveRun(run: Run): Result<Unit> {
        return try {
            val data = hashMapOf(
                "userId" to (userId ?: ""),
                "type" to run.type,
                "distance" to run.distance,
                "duration" to run.duration,
                "date" to run.date,
                "notes" to run.notes
            )
            db.collection("runs").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRuns(type: String? = null): Result<List<Run>> {
        return try {
            var query = db.collection("runs")
                .whereEqualTo("userId", userId ?: "")
                .orderBy("date", Query.Direction.DESCENDING)
            if (type != null) {
                query = query.whereEqualTo("type", type)
            }
            val snapshot = query.get().await()
            val runs = snapshot.documents.map { doc ->
                Run(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    type = doc.getString("type") ?: "",
                    distance = doc.getDouble("distance") ?: 0.0,
                    duration = doc.getLong("duration") ?: 0,
                    date = doc.getLong("date") ?: 0,
                    notes = doc.getString("notes") ?: ""
                )
            }
            Result.success(runs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSleepSchedule(schedule: SleepSchedule): Result<Unit> {
        return try {
            val data = hashMapOf(
                "userId" to (userId ?: ""),
                "workStartHour" to schedule.workStartHour,
                "workStartMinute" to schedule.workStartMinute,
                "workEndHour" to schedule.workEndHour,
                "workEndMinute" to schedule.workEndMinute,
                "suggestedSleepHour" to schedule.suggestedSleepHour,
                "suggestedSleepMinute" to schedule.suggestedSleepMinute,
                "suggestedWakeHour" to schedule.suggestedWakeHour,
                "suggestedWakeMinute" to schedule.suggestedWakeMinute,
                "consistencyScore" to schedule.consistencyScore
            )
            db.collection("sleepSchedules").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSleepSchedules(): Result<List<SleepSchedule>> {
        return try {
            val snapshot = db.collection("sleepSchedules")
                .whereEqualTo("userId", userId ?: "")
                .orderBy("consistencyScore", Query.Direction.DESCENDING)
                .get().await()
            val schedules = snapshot.documents.map { doc ->
                SleepSchedule(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    workStartHour = doc.getLong("workStartHour")?.toInt() ?: 8,
                    workStartMinute = doc.getLong("workStartMinute")?.toInt() ?: 0,
                    workEndHour = doc.getLong("workEndHour")?.toInt() ?: 17,
                    workEndMinute = doc.getLong("workEndMinute")?.toInt() ?: 0,
                    suggestedSleepHour = doc.getLong("suggestedSleepHour")?.toInt() ?: 0,
                    suggestedSleepMinute = doc.getLong("suggestedSleepMinute")?.toInt() ?: 0,
                    suggestedWakeHour = doc.getLong("suggestedWakeHour")?.toInt() ?: 0,
                    suggestedWakeMinute = doc.getLong("suggestedWakeMinute")?.toInt() ?: 0,
                    consistencyScore = doc.getLong("consistencyScore")?.toInt() ?: 0
                )
            }
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {}
}
