package com.example.physical.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val quotes = listOf(
    "The only bad workout is the one that didn't happen.",
    "Strength does not come from the body. It comes from the will.",
    "Take care of your body. It's the only place you have to live.",
    "The harder you work, the luckier you get.",
    "Sleep is the best meditation.",
    "Your body can stand almost anything. It's your mind you have to convince.",
    "The secret of getting ahead is getting started.",
    "Consistency is more important than perfection.",
    "A year from now you'll wish you started today.",
    "Don't limit your challenges. Challenge your limits."
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val quote by remember { mutableStateOf(quotes.random()) }
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF388E3C)
                    )
                )
            )
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\uD83C\uDFC3",
            fontSize = 64.sp,
            modifier = Modifier.alpha(alphaAnim)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Physical",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.alpha(alphaAnim)
        )

        Text(
            text = "FITNESS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 8.sp,
            modifier = Modifier.alpha(alphaAnim)
        )

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "\u201C$quote\u201D",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alphaAnim)
        )
    }
}
