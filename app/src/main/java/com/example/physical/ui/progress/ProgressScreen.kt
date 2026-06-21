package com.example.physical.ui.progress

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.physical.data.repository.FitnessRepository

@Composable
fun ProgressScreen(userName: String, isGuest: Boolean) {
    val context = LocalContext.current
    val repository = remember { FitnessRepository() }
    var totalRuns by remember { mutableStateOf(0) }
    var totalDistance by remember { mutableStateOf(0.0) }
    var weeklyRuns by remember { mutableStateOf(0) }
    var weeklyDistance by remember { mutableStateOf(0.0) }
    var avgPace by remember { mutableStateOf("--") }
    var bestDistance by remember { mutableStateOf(0.0) }
    var loaded by remember { mutableStateOf(false) }

    var locationEnabled by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    var activityEnabled by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(isGuest) {
        if (!isGuest) {
            val result = repository.getRuns()
            result.onSuccess { runs ->
                val weekAgo = System.currentTimeMillis() - 7 * 86400000
                val weekRuns = runs.filter { it.date >= weekAgo }
                totalRuns = runs.size
                totalDistance = runs.sumOf { it.distance }
                weeklyRuns = weekRuns.size
                weeklyDistance = weekRuns.sumOf { it.distance }
                bestDistance = runs.maxOfOrNull { it.distance } ?: 0.0
                avgPace = if (runs.isNotEmpty()) {
                    val totalMin = runs.sumOf { it.duration }
                    val pace = if (totalDistance > 0) totalMin.toDouble() / totalDistance else 0.0
                    if (pace > 0) String.format("%.1f", pace) else "--"
                } else "--"
                loaded = true
            }
        } else {
            loaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "My Progress",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your running stats at a glance",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        if (isGuest) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Text(
                    text = "Sign up to track your progress over time.",
                    fontSize = 13.sp,
                    color = Color(0xE65100),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (loaded) {
            Text(
                text = "OVERALL STATS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = if (!isGuest) "$totalRuns" else "--",
                    label = "Total Runs"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = if (!isGuest) String.format("%.1f", totalDistance) else "--",
                    label = "Total Km"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = if (!isGuest) avgPace else "--",
                    label = "Avg Pace (min/km)"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = if (!isGuest) String.format("%.1f", bestDistance) else "--",
                    label = "Best Run (km)"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "WEEKLY STATS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeeklyCard(
                    modifier = Modifier.weight(1f),
                    value = if (!isGuest) "$weeklyRuns" else "--",
                    label = "Runs This Week"
                )
                WeeklyCard(
                    modifier = Modifier.weight(1f),
                    value = if (!isGuest) String.format("%.1f", weeklyDistance) else "--",
                    label = "Km This Week"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "SETTINGS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggle(
                        title = "Location Access",
                        description = "Required for GPS run tracking",
                        checked = locationEnabled,
                        onCheckedChange = { locationEnabled = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingToggle(
                        title = "Activity Recognition",
                        description = "Step counting and movement detection",
                        checked = activityEnabled,
                        onCheckedChange = { activityEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CONSISTENCY TIPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "\u2022 Run at the same time each day\n" +
                                "\u2022 Start with 15 min and build up\n" +
                                "\u2022 Rest days are just as important\n" +
                                "\u2022 Celebrate small wins",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32).copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            Text(
                text = "Loading...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF2E7D32).copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2E7D32),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD)
            )
        )
    }
}
