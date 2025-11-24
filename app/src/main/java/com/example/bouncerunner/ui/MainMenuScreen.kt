package com.example.bouncerunner.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(
    highScore: Int,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    // Animated floating elements
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )
    val float2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4FC3F7), // Light blue
                        Color(0xFF29B6F6), // Medium blue
                        Color(0xFF81C784)  // Light green at bottom
                    )
                )
            )
    ) {
        // Decorative clouds and elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Cloud 1
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 40f,
                center = Offset(100f + float1, 150f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 50f,
                center = Offset(140f + float1, 150f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 35f,
                center = Offset(180f + float1, 155f)
            )
            
            // Cloud 2
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = 35f,
                center = Offset(size.width - 150f + float2, 100f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = 45f,
                center = Offset(size.width - 110f + float2, 100f)
            )
            
            // Stars
            for (i in 0..5) {
                val x = (i * 80f) + 50f
                val y = 50f + (i % 2) * 30f
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.6f),
                    radius = 8f,
                    center = Offset(x, y)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated bouncing ball logo
            Canvas(modifier = Modifier.size(120.dp)) {
                val bounce = kotlin.math.abs(kotlin.math.sin(System.currentTimeMillis() / 300.0).toFloat()) * 20f
                
                // Shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.3f),
                    topLeft = Offset(size.width / 2 - 45f, size.height - 20f),
                    size = androidx.compose.ui.geometry.Size(90f, 15f)
                )
                
                // Ball
                drawCircle(
                    color = Color(0xFFFF5252), // Bright red
                    radius = 45f,
                    center = Offset(size.width / 2, size.height / 2 - bounce)
                )
                
                // Highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = 18f,
                    center = Offset(size.width / 2 + 15f, size.height / 2 - bounce - 15f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title with colorful outline
            Text(
                text = "BOUNCE",
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF1565C0),
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
            Text(
                text = "RUNNER",
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD600), // Bright yellow
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFFFF6F00),
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // High Score Badge
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Best: $highScore",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Play Button - Large and colorful
            Button(
                onClick = { 
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPlayClick() 
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(70.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF66BB6A) // Bright green
                ),
                elevation = ButtonDefaults.buttonElevation(12.dp)
            ) {
                Text(
                    text = "▶ PLAY",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Settings Button - Colorful outlined
            OutlinedButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSettingsClick()
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
            ) {
                Text(
                    text = "⚙ Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
