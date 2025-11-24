package com.example.bouncerunner.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var bounceOffset by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        // Animate bounce
        launch {
            var time = 0f
            while (time < 2000f) {
                bounceOffset = kotlin.math.abs(kotlin.math.sin(time / 200f) * 50f)
                delay(16)
                time += 16f
            }
        }
        
        delay(2000)
        onTimeout()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3),
                        Color(0xFF1976D2)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated bouncing ball
            Canvas(modifier = Modifier.size(100.dp)) {
                val groundY = size.height - 10f
                val ballY = groundY - 40f - bounceOffset
                
                // Shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.3f),
                    topLeft = Offset(size.width / 2 - 30f, groundY - 5f),
                    size = Size(60f, 10f)
                )
                
                // Ball
                drawCircle(
                    color = Color.Red,
                    radius = 40f,
                    center = Offset(size.width / 2, ballY)
                )
                
                // Highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = 15f,
                    center = Offset(size.width / 2 + 10f, ballY - 10f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Bounce Runner",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
