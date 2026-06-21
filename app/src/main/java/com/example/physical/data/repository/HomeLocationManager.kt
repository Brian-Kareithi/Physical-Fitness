package com.example.physical.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import java.util.Locale

data class SuggestedRoute(
    val name: String,
    val distanceKm: Double,
    val description: String,
    val icon: String
)

class HomeLocationManager(context: Context) {

    private val prefs = context.getSharedPreferences("home_location", Context.MODE_PRIVATE)
    private val geocoder = Geocoder(context, Locale.getDefault())
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun detectHome(onResult: (String) -> Unit) {
        if (getHomeAddress() != null) {
            onResult(getHomeAddress()!!)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                saveLocation(location)
                val address = reverseGeocode(location)
                if (address != null) {
                    prefs.edit().putString("home_address", address).apply()
                    onResult(address)
                } else {
                    onResult("${location.latitude}, ${location.longitude}")
                }
            } else {
                onResult("Unknown")
            }
        }
    }

    fun getHomeAddress(): String? {
        return prefs.getString("home_address", null)
    }

    fun getHomeLat(): Double {
        return prefs.getFloat("home_lat", 0f).toDouble()
    }

    fun getHomeLng(): Double {
        return prefs.getFloat("home_lng", 0f).toDouble()
    }

    fun hasHomeLocation(): Boolean {
        return prefs.contains("home_lat")
    }

    fun updateHomeFromLogin(location: Location) {
        saveLocation(location)
        val address = reverseGeocode(location)
        if (address != null) {
            prefs.edit().putString("home_address", address).apply()
        }
    }

    private fun saveLocation(location: Location) {
        prefs.edit()
            .putFloat("home_lat", location.latitude.toFloat())
            .putFloat("home_lng", location.longitude.toFloat())
            .apply()
    }

    private fun reverseGeocode(location: Location): String? {
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(
                location.latitude, location.longitude, 1
            )
            addresses?.firstOrNull()?.let { addr ->
                val parts = mutableListOf<String>()
                addr.subThoroughfare?.let { parts.add(it) }
                addr.thoroughfare?.let { parts.add(it) }
                addr.locality?.let { parts.add(it) }
                addr.subAdminArea?.let { if (parts.size < 2) parts.add(it) }
                if (parts.isEmpty()) null else parts.joinToString(", ")
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getSuggestedRoutes(): List<SuggestedRoute> {
        if (!hasHomeLocation()) return emptyList()

        return listOf(
            SuggestedRoute(
                name = "Short Loop",
                distanceKm = 1.0,
                description = "Quick 1km loop around your area. Perfect for warm-ups or quick sessions.",
                icon = "\uD83C\uDF1E"
            ),
            SuggestedRoute(
                name = "Moderate Route",
                distanceKm = 3.0,
                description = "3km out-and-back on main roads. Well-lit and safe for morning runs.",
                icon = "\uD83C\uDF1F"
            ),
            SuggestedRoute(
                name = "Long Run",
                distanceKm = 5.0,
                description = "5km loop covering nearby neighborhoods. Great for endurance training.",
                icon = "\uD83C\uDF20"
            ),
            SuggestedRoute(
                name = "Challenge",
                distanceKm = 10.0,
                description = "10km route for experienced runners. Explore beyond your usual area.",
                icon = "\u26A1"
            )
        )
    }

    companion object {
        var instance: HomeLocationManager? = null
    }
}
