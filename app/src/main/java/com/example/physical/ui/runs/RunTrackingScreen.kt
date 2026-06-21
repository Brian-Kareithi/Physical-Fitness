package com.example.physical.ui.runs

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physical.data.model.Run
import com.example.physical.data.repository.SuggestedRoute
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RunTrackingScreen(
    viewModel: RunViewModel,
    userName: String,
    isGuest: Boolean
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(isGuest) {
        viewModel.loadData(isGuest)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Text(
                text = "Runs",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item {
            if (state.isTracking || state.isPaused) {
                TrackingCard(state = state, viewModel = viewModel)
            } else {
                StartCard(state = state, viewModel = viewModel)
            }
        }

        if (!state.isTracking && !state.isPaused && state.suggestedRoutes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SUGGESTED ROUTES NEAR HOME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32),
                    letterSpacing = 3.sp
                )
            }
            items(state.suggestedRoutes) { route ->
                SuggestedRouteCard(route = route, onStart = { viewModel.startRun() })
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RUN HISTORY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E7D32),
                letterSpacing = 4.sp
            )
        }

        if (state.runs.isEmpty()) {
            item {
                Text(
                    text = "No runs logged yet. Tap Start and go!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }

        items(state.runs.take(10)) { run ->
            RunCard(run = run)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun TrackingCard(
    state: RunUiState,
    viewModel: RunViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (state.isPaused) "PAUSED" else "RUNNING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = if (state.isPaused) Color(0xFFFF9800) else Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = viewModel.formatTime(state.elapsedSeconds),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = String.format("%.2f", state.distanceKm), unit = "km")
                StatItem(value = state.pace, unit = "/km")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (state.isPaused) {
                    ControlButton(
                        text = "Resume",
                        color = Color(0xFF4CAF50),
                        onClick = { viewModel.resumeRun() }
                    )
                } else {
                    ControlButton(
                        text = "Pause",
                        color = Color(0xFFFF9800),
                        onClick = { viewModel.pauseRun() }
                    )
                }
                ControlButton(
                    text = "Stop",
                    color = Color(0xFFE53935),
                    onClick = { viewModel.stopRun() }
                )
            }

            if (state.saveSuccess) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Run saved!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun StartCard(
    state: RunUiState,
    viewModel: RunViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.homeAddress != null) {
                Text(
                    text = state.homeAddress,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "Ready?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
                    .clickable { viewModel.startRun() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "START",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to start GPS tracking",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StatItem(value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = unit,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ControlButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SuggestedRouteCard(route: SuggestedRoute, onStart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${route.name} \u2022 ${String.format("%.0f", route.distanceKm)}km",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = route.description,
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32).copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun RunCard(run: Run) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = String.format("%.2f km", run.distance),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${run.duration} min",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Text(
                text = dateFormat.format(Date(run.date)),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}
