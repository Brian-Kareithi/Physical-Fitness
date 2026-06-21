package com.example.physical.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physical.data.repository.FitnessRepository

@Composable
fun ProgressScreen(userName: String, isGuest: Boolean) {
    val repository = remember { FitnessRepository() }
    var weeklyRuns by remember { mutableStateOf(0) }
    var weeklyDistance by remember { mutableStateOf(0.0) }
    var avgDuration by remember { mutableStateOf(0.0) }
    var bestDistance by remember { mutableStateOf(0.0) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(isGuest) {
        if (!isGuest) {
            val result = repository.getRuns()
            result.onSuccess { runs ->
                val weekAgo = System.currentTimeMillis() - 7 * 86400000
                val weekRuns = runs.filter { it.date >= weekAgo }
                weeklyRuns = weekRuns.size
                weeklyDistance = weekRuns.sumOf { it.distance }
                avgDuration = if (weekRuns.isNotEmpty()) weekRuns.sumOf { it.duration }.toDouble() / weekRuns.size else 0.0
                bestDistance = runs.maxOfOrNull { it.distance } ?: 0.0
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
        Text(text = "\uD83D\uDCCA", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Progress",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Track your fitness journey",
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
                    text = "Sign up to track your progress over time. Guest mode data is not saved.",
                    fontSize = 13.sp,
                    color = Color(0xE65100),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (loaded && !isGuest) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProgressCard(
                    modifier = Modifier.weight(1f),
                    value = "$weeklyRuns",
                    label = "Runs This Week",
                    color = Color(0xFF2E7D32)
                )
                ProgressCard(
                    modifier = Modifier.weight(1f),
                    value = String.format("%.1f", weeklyDistance),
                    label = "Km This Week",
                    color = Color(0xFF1565C0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProgressCard(
                    modifier = Modifier.weight(1f),
                    value = String.format("%.0f", avgDuration),
                    label = "Avg Min/Run",
                    color = Color(0xFF6A1B9A)
                )
                ProgressCard(
                    modifier = Modifier.weight(1f),
                    value = String.format("%.1f", bestDistance),
                    label = "Best Run (km)",
                    color = Color(0xFFE65100)
                )
            }
        } else if (loaded && isGuest) {
            ProgressCard(
                modifier = Modifier.fillMaxWidth(),
                value = "--",
                label = "Sign up to see stats",
                color = Color(0xFF9E9E9E)
            )
        } else {
            Text(
                text = "Loading...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Consistency Tips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\u2022 Run at the same time each day\n" +
                            "\u2022 Start with 15 min and build up\n" +
                            "\u2022 Rest days are just as important\n" +
                            "\u2022 Celebrate small wins",
                    fontSize = 14.sp,
                    color = Color(0xFF2E7D32).copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProgressCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color
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
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
