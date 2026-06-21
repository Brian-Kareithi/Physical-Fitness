package com.example.physical.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(viewModel: HomeViewModel, userName: String, isGuest: Boolean) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(userName, isGuest) {
        viewModel.loadData(userName, isGuest)
    }

    val firstName = userName.substringBefore(" ").ifBlank { "Athlete" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B5E20), Color(0xFF388E3C), Color(0xFF4CAF50))
                    )
                )
                .padding(top = 32.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Text(
                    text = "Welcome back,",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = firstName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (isGuest) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Guest mode \u2022 data not saved",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HeaderStat(
                        modifier = Modifier.weight(1f),
                        value = "${state.activityData.steps}",
                        label = "Steps"
                    )
                    HeaderStat(
                        modifier = Modifier.weight(1f),
                        value = "${state.activityData.walkingMinutes} min",
                        label = "Walking"
                    )
                    HeaderStat(
                        modifier = Modifier.weight(1f),
                        value = "${state.activityData.vehicleMinutes} min",
                        label = "Vehicle"
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    value = if (!state.isGuest) "${state.todayRuns}" else "--",
                    label = "Runs Today",
                    accentColor = Color(0xFF2E7D32)
                )
                DashboardCard(
                    modifier = Modifier.weight(1f),
                    value = if (!state.isGuest) String.format("%.1f", state.todayDistance) else "--",
                    label = "Km Today",
                    accentColor = Color(0xFF1565C0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "THIS WEEK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32),
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!state.isGuest) {
                            WeekStat(value = "${state.weeklyRuns}", label = "Runs")
                            WeekStat(value = String.format("%.1f", state.weeklyDistance), label = "Km")
                            WeekStat(value = "${state.weeklyDuration}", label = "Min")
                        } else {
                            WeekStat(value = "--", label = "Runs")
                            WeekStat(value = "--", label = "Km")
                            WeekStat(value = "--", label = "Min")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LIFETIME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32),
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!state.isGuest) {
                            WeekStat(value = "${state.totalRuns}", label = "Total Runs")
                            WeekStat(value = String.format("%.1f", state.totalDistance), label = "Total Km")
                            WeekStat(value = state.avgPace, label = "Pace (min/km)")
                        } else {
                            WeekStat(value = "--", label = "Total Runs")
                            WeekStat(value = "--", label = "Total Km")
                            WeekStat(value = "--", label = "Pace")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u201C",
                        fontSize = 40.sp,
                        color = Color(0xFF2E7D32).copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "The only bad workout is the one that didn't happen.",
                        fontSize = 15.sp,
                        color = Color(0xFF2E7D32),
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun WeekStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF2E7D32).copy(alpha = 0.65f)
        )
    }
}
