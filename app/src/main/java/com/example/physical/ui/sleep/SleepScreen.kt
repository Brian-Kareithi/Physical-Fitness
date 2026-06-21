package com.example.physical.ui.sleep

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SleepScreen(viewModel: SleepViewModel, isGuest: Boolean) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(isGuest) {
        viewModel.loadData(isGuest)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "\uD83D\uDCA4", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sleep Schedule",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Optimize your sleep around your work schedule",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (state.isGuest) {
                    Text(
                        text = "Guest mode: data won't be saved",
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "Work Schedule",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Work Start Time",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TimeSelector(
                        label = "Hour",
                        value = state.workStartHour,
                        range = 0..23,
                        onValueChange = { viewModel.updateWorkStart(it, state.workStartMinute) },
                        modifier = Modifier.weight(1f)
                    )
                    TimeSelector(
                        label = "Minute",
                        value = state.workStartMinute / 15 * 15,
                        range = 0..45 step 15,
                        onValueChange = { viewModel.updateWorkStart(state.workStartHour, it) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Work End Time",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TimeSelector(
                        label = "Hour",
                        value = state.workEndHour,
                        range = 0..23,
                        onValueChange = { viewModel.updateWorkEnd(it, state.workEndMinute) },
                        modifier = Modifier.weight(1f)
                    )
                    TimeSelector(
                        label = "Minute",
                        value = state.workEndMinute / 15 * 15,
                        range = 0..45 step 15,
                        onValueChange = { viewModel.updateWorkEnd(state.workEndHour, it) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.calculateSleepSchedule() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A1B9A)
                    )
                ) {
                    Text("Get Sleep Recommendation", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (state.showSuggestion) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recommended Sleep Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A1B9A)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "\uD83D\uDE34",
                                fontSize = 32.sp
                            )
                            Text(
                                text = "Sleep",
                                fontSize = 12.sp,
                                color = Color(0xFF6A1B9A).copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format("%02d:%02d", state.suggestedSleepHour, state.suggestedSleepMinute),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A1B9A)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "\u2600\uFE0F",
                                fontSize = 32.sp
                            )
                            Text(
                                text = "Wake Up",
                                fontSize = 12.sp,
                                color = Color(0xFF6A1B9A).copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format("%02d:%02d", state.suggestedWakeHour, state.suggestedWakeMinute),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A1B9A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Work ends at ${String.format("%02d:%02d", state.workEndHour, state.workEndMinute)}. " +
                                "Sleep after work for best recovery.",
                        fontSize = 13.sp,
                        color = Color(0xFF6A1B9A).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.saveSchedule() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A1B9A)
                        )
                    ) {
                        Text("Save Schedule", fontWeight = FontWeight.SemiBold)
                    }

                    if (state.saved) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Schedule saved!",
                            fontSize = 13.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sleep Tips",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\u2022 Sleep 7-9 hours for optimal recovery\n" +
                            "\u2022 Avoid screens 30 min before bed\n" +
                            "\u2022 Keep your room cool and dark\n" +
                            "\u2022 Be consistent with sleep/wake times\n" +
                            "\u2022 Sleep soon after work for better rest",
                    fontSize = 13.sp,
                    color = Color(0xFF2E7D32).copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TimeSelector(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (h in listOf(value - 1, value, value + 1)) {
                val actual = when {
                    h < range.first -> range.last
                    h > range.last -> range.first
                    else -> h
                }
                val isSelected = actual == value
                Card(
                    onClick = { onValueChange(actual) },
                    modifier = Modifier.padding(2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF6A1B9A) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = String.format("%02d", actual),
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
