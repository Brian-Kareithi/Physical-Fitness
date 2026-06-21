package com.example.physical.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
            text = "Welcome${if (state.userName.isNotBlank()) ", ${state.userName}" else ""}!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (state.isGuest) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Running in guest mode. Data won't be saved.",
                fontSize = 13.sp,
                color = Color(0xFFFF9800),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TODAY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2E7D32),
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TodayCard(
                modifier = Modifier.weight(1f),
                emoji = "\uD83C\uDF05",
                title = "Morning Run",
                status = if (state.todayMorningRun != null) "Done!" else "Pending",
                statusColor = if (state.todayMorningRun != null) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
            )
            TodayCard(
                modifier = Modifier.weight(1f),
                emoji = "\uD83C\uDF07",
                title = "Evening Run",
                status = if (state.todayEveningRun != null) "Done!" else "Pending",
                statusColor = if (state.todayEveningRun != null) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "THIS WEEK",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2E7D32),
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), value = "${state.weeklyRuns}", label = "Runs")
            StatCard(modifier = Modifier.weight(1f), value = String.format("%.1f", state.weeklyDistance), label = "Km")
            StatCard(modifier = Modifier.weight(1f), value = "${state.weeklyDuration}", label = "Minutes")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "QUOTE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2E7D32),
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Text(
                text = "\"The only bad workout is the one that didn't happen.\"",
                fontSize = 14.sp,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp),
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TodayCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    status: String,
    statusColor: Color
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
