package com.example.physical.ui.sleep

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SleepScreen(viewModel: SleepViewModel, isGuest: Boolean) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(isGuest) {
        viewModel.loadData(isGuest)
    }

    val accentColor = Color(0xFF6A1B9A)
    val lightAccent = Color(0xFFF3E5F5)
    val progress = state.step.toFloat() / 5f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.showSuggestion) {
            RecommendationContent(state = state, viewModel = viewModel, accentColor = accentColor, lightAccent = lightAccent)
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sleep Schedule",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Answer a few questions to get your personalized sleep plan",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = accentColor,
                    trackColor = lightAccent
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Step ${state.step} of 5",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        when (state.step) {
                            1 -> DaysPerWeekStep(state, viewModel)
                            2 -> HoursPerDayStep(state, viewModel)
                            3 -> WorkStartTimeStep(state, viewModel)
                            4 -> CommuteStep(state, viewModel)
                            5 -> JobStep(state, viewModel, accentColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    if (state.step == 5) {
                        viewModel.calculate()
                    } else {
                        viewModel.nextStep()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    text = if (state.step == 5) "Get My Sleep Plan" else "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DaysPerWeekStep(state: SleepUiState, viewModel: SleepViewModel) {
    Text(
        text = "How many days per week do you work?",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (day in 1..7) {
            val isSelected = day == state.daysPerWeek
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color(0xFF6A1B9A) else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .clickable { viewModel.updateDaysPerWeek(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$day",
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "${state.daysPerWeek} days per week",
        fontSize = 14.sp,
        color = Color(0xFF6A1B9A),
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HoursPerDayStep(state: SleepUiState, viewModel: SleepViewModel) {
    Text(
        text = "How many hours do you work per day?",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "${String.format("%.1f", state.hoursPerDay)} hours",
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6A1B9A),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Slider(
        value = state.hoursPerDay,
        onValueChange = { viewModel.updateHoursPerDay(it) },
        valueRange = 1f..16f,
        steps = 29,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color(0xFF6A1B9A),
            activeTrackColor = Color(0xFF6A1B9A),
            inactiveTrackColor = Color(0xFFF3E5F5)
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("1h", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text("16h", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun WorkStartTimeStep(state: SleepUiState, viewModel: SleepViewModel) {
    Text(
        text = "What time do you start work?",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = String.format("%02d:%02d", state.workStartHour, state.workStartMinute),
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6A1B9A),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Hour",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(8.dp))
    TimeRow(
        current = state.workStartHour,
        range = 0..23,
        onSelect = { viewModel.updateWorkStartHour(it) },
        accentColor = Color(0xFF6A1B9A)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Minute",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
    Spacer(modifier = Modifier.height(8.dp))
    TimeRow(
        current = state.workStartMinute / 15 * 15,
        range = 0..45 step 15,
        onSelect = { viewModel.updateWorkStartMinute(it) },
        accentColor = Color(0xFF6A1B9A)
    )
}

@Composable
private fun CommuteStep(state: SleepUiState, viewModel: SleepViewModel) {
    Text(
        text = "How long is your commute?",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "One-way in minutes",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "${state.commuteMinutes} min",
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6A1B9A),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Slider(
        value = state.commuteMinutes.toFloat(),
        onValueChange = { viewModel.updateCommute(it.toInt()) },
        valueRange = 0f..240f,
        steps = 47,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color(0xFF6A1B9A),
            activeTrackColor = Color(0xFF6A1B9A),
            inactiveTrackColor = Color(0xFFF3E5F5)
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("0 min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text("4 hrs", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }

    val joke = viewModel.commuteJoke()
    if (joke != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Text(
                text = joke,
                fontSize = 13.sp,
                color = Color(0xE65100),
                lineHeight = 20.sp,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
private fun JobStep(state: SleepUiState, viewModel: SleepViewModel, accentColor: Color) {
    Text(
        text = "What's your job?",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(20.dp))

    OutlinedTextField(
        value = state.jobTitle,
        onValueChange = { viewModel.updateJobTitle(it) },
        label = { Text("Job Title") },
        placeholder = { Text("e.g. Software Engineer, Teacher, Nurse...") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Text(
            text = "We'll use your job type to tailor the best sleep recommendations for your lifestyle.",
            fontSize = 13.sp,
            color = Color(0xFF6A1B9A).copy(alpha = 0.8f),
            lineHeight = 20.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun RecommendationContent(
    state: SleepUiState,
    viewModel: SleepViewModel,
    accentColor: Color,
    lightAccent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3E5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83D\uDCA4", fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Sleep Plan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (state.jobTitle.isNotBlank()) {
                    Text(
                        text = "for ${state.jobTitle}",
                        fontSize = 14.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "\uD83C\uDF19",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "Sleep",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.suggestedSleepTime,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.suggestedWakeTime,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = Color(0xFFF3E5F5))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Schedule",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\u2022 ${state.daysPerWeek} days per week\n" +
                            "\u2022 ${String.format("%.0f", state.hoursPerDay)} hours per day\n" +
                            "\u2022 ${state.commuteMinutes} min commute\n" +
                            "\u2022 Sleep after work + commute for best recovery",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.saveSchedule() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
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

        Spacer(modifier = Modifier.height(20.dp))

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
                            "\u2022 Be consistent even on weekends",
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
private fun HorizontalDivider(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

@Composable
private fun TimeRow(
    current: Int,
    range: IntProgression,
    onSelect: (Int) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (value in range) {
            val isSelected = value == current
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) accentColor else Color.Transparent
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(value) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", value),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
