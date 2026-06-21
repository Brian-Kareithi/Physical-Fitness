package com.example.physical.data.repository

import com.example.physical.data.model.FoodSuggestion
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

    companion object {
        val kenyanFoods = listOf(
            FoodSuggestion(
                name = "Ugali",
                description = "Stiff maize meal porridge, Kenya's staple food. Pairs perfectly with sukuma wiki or nyama choma.",
                nutrients = "Carbs (37g), Fiber (2g), Iron (1.5mg), B-Vitamins",
                benefits = "Sustained energy release, affordable, filling. Great pre-run fuel.",
                emoji = "\uD83C\uDF3E"
            ),
            FoodSuggestion(
                name = "Sukuma Wiki",
                description = "Collard greens sauteed with onions and tomatoes. The name means 'push the week' - it's affordable and stretches meals.",
                nutrients = "Vitamin A (206%), Vit C (134%), Iron (8%), Calcium (14%), Fiber (3g)",
                benefits = "Boosts immunity, strengthens bones, aids muscle recovery.",
                emoji = "\uD83E\uDD66"
            ),
            FoodSuggestion(
                name = "Nyama Choma",
                description = "Traditional roasted goat or beef. Kenya's favorite weekend meal, often enjoyed with family and friends.",
                nutrients = "Protein (26g), Iron (15%), Zinc (35%), B12 (38%)",
                benefits = "Builds muscle, repairs tissue, boosts testosterone for better workouts.",
                emoji = "\uD83C\uDF56"
            ),
            FoodSuggestion(
                name = "Githeri",
                description = "Hearty mix of boiled maize and beans. A complete protein source popular in central Kenya.",
                nutrients = "Protein (15g), Fiber (12g), Iron (20%), Folate (28%)",
                benefits = "Complete protein (rice+beans combo), steady glucose, great for meal prep.",
                emoji = "\uD83E\uDDCA"
            ),
            FoodSuggestion(
                name = "Omena",
                description = "Small silver dried fish, typically served in a stew with tomatoes. Affordable protein for coastal communities.",
                nutrients = "Calcium (40%), Protein (20g), Omega-3 (1.2g), Iron (25%)",
                benefits = "Strengthens joints, anti-inflammatory, supports cardiovascular health.",
                emoji = "\uD83D\uDC1F"
            ),
            FoodSuggestion(
                name = "Matoke",
                description = "Green bananas boiled or mashed. A staple in western Kenya, naturally alkaline and easy to digest.",
                nutrients = "Potassium (22%), Vit C (20%), Fiber (4g), Magnesium (12%)",
                benefits = "Electrolyte replenishment, prevents cramps, gentle on stomach.",
                emoji = "\uD83C\uDF4C"
            ),
            FoodSuggestion(
                name = "Mursik",
                description = "Fermented milk in traditional gourd. A Kalenjin delicacy rich in probiotics.",
                nutrients = "Probiotics, Calcium (28%), Protein (8g), Vitamin D (15%)",
                benefits = "Gut health, stronger bones, improved digestion and nutrient absorption.",
                emoji = "\uD83E\uDD5B"
            ),
            FoodSuggestion(
                name = "Mukimo",
                description = "Mashed potatoes mixed with pumpkin leaves, maize, and onions. A hearty central Kenya meal.",
                nutrients = "Carbs (45g), Vit A (35%), Iron (10%), Fiber (5g)",
                benefits = "Post-workout carb replenishment, vitamin-rich, satisfying.",
                emoji = "\uD83E\uDD54"
            ),
            FoodSuggestion(
                name = "Fish from Lake Victoria",
                description = "Fresh tilapia or Nile perch, often fried or grilled. World-class freshwater fish.",
                nutrients = "Omega-3 (1.5g), Protein (23g), Selenium (45%), B12 (30%)",
                benefits = "Brain health, reduces inflammation, muscle repair, thyroid function.",
                emoji = "\uD83D\uDC20"
            ),
            FoodSuggestion(
                name = "Kachumbari",
                description = "Fresh tomato and onion salsa with coriander and lemon juice. The perfect side for any Kenyan meal.",
                nutrients = "Vit C (40%), Antioxidants, Lycopene, Fiber (2g), Zero fat",
                benefits = "Hydration, immune boost, aids digestion, low-calorie nutrient bomb.",
                emoji = "\uD83C\uDF45"
            )
        )
    }
}
