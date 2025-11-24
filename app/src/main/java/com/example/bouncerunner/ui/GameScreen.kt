package com.example.bouncerunner.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bouncerunner.game.GameViewModel
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToMenu: () -> Unit
) {
    val gameState by viewModel.gameState
    
    // Pass screen size to ViewModel
    androidx.compose.ui.platform.LocalConfiguration.current.let { config ->
        val density = androidx.compose.ui.platform.LocalDensity.current
        val widthPx = with(density) { config.screenWidthDp.dp.toPx() }
        val heightPx = with(density) { config.screenHeightDp.dp.toPx() }
        
        androidx.compose.runtime.LaunchedEffect(widthPx, heightPx) {
            viewModel.setScreenSize(widthPx, heightPx)
        }
    }

    // Dynamic Background
    val backgroundColor = if (gameState.score < 50) {
        listOf(Color(0xFF87CEEB), Color(0xFFE0F7FA)) // Day
    } else if (gameState.score < 100) {
        listOf(Color(0xFFFFCC80), Color(0xFFFFE0B2)) // Sunset
    } else {
        listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF)) // Night
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Brush.verticalGradient(backgroundColor))
            .clickable { viewModel.onTap() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val groundLevel = size.height * 0.75f
            
            // Draw Environment Objects (behind everything)
            gameState.environmentObjects.forEach { envObj ->
                when (envObj.type) {
                    com.example.bouncerunner.game.EnvironmentType.CLOUD -> {
                        // Draw fluffy cloud (multiple circles)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = 30f,
                            center = Offset(envObj.x, envObj.y)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = 25f,
                            center = Offset(envObj.x + 25f, envObj.y)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = 20f,
                            center = Offset(envObj.x - 20f, envObj.y + 5f)
                        )
                    }
                    com.example.bouncerunner.game.EnvironmentType.SUN -> {
                        // Draw sun with rays
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = 50f,
                            center = Offset(envObj.x, envObj.y)
                        )
                        // Sun rays (simple lines)
                        for (i in 0..7) {
                            val angle = (i * 45f) * (Math.PI / 180f)
                            val startX = envObj.x + kotlin.math.cos(angle).toFloat() * 60f
                            val startY = envObj.y + kotlin.math.sin(angle).toFloat() * 60f
                            val endX = envObj.x + kotlin.math.cos(angle).toFloat() * 80f
                            val endY = envObj.y + kotlin.math.sin(angle).toFloat() * 80f
                            drawLine(
                                color = Color(0xFFFFD700),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 5f
                            )
                        }
                    }
                    com.example.bouncerunner.game.EnvironmentType.MOON -> {
                        // Draw moon
                        drawCircle(
                            color = Color(0xFFE0E0E0),
                            radius = 50f,
                            center = Offset(envObj.x, envObj.y)
                        )
                        // Moon craters
                        drawCircle(
                            color = Color(0xFFC0C0C0),
                            radius = 12f,
                            center = Offset(envObj.x - 15f, envObj.y - 10f)
                        )
                        drawCircle(
                            color = Color(0xFFC0C0C0),
                            radius = 8f,
                            center = Offset(envObj.x + 10f, envObj.y + 5f)
                        )
                        drawCircle(
                            color = Color(0xFFC0C0C0),
                            radius = 6f,
                            center = Offset(envObj.x, envObj.y + 15f)
                        )
                    }
                    com.example.bouncerunner.game.EnvironmentType.STAR -> {
                        // Draw twinkling star
                        drawCircle(
                            color = Color.White.copy(alpha = envObj.alpha),
                            radius = 3f,
                            center = Offset(envObj.x, envObj.y)
                        )
                    }
                }
            }
            
            // Draw Ground
            drawRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(0f, groundLevel),
                size = androidx.compose.ui.geometry.Size(size.width, size.height - groundLevel)
            )
            
            // Draw Ground Line (Top Edge)
            drawLine(
                color = Color(0xFF388E3C),
                start = Offset(0f, groundLevel),
                end = Offset(size.width, groundLevel),
                strokeWidth = 10f
            )

            // Draw Shadow


            // Draw Obstacles with varied types and contrasting colors
            gameState.obstacles.forEach { obstacle ->
                when (obstacle.type) {
                    com.example.bouncerunner.game.ObstacleType.CACTUS -> {
                        // Cactus: Bright green with dark outline
                        val brightGreen = Color(0xFF00E676)
                        val darkGreen = Color(0xFF1B5E20)
                        // Main body
                        drawRect(
                            color = brightGreen,
                            topLeft = Offset(obstacle.x + obstacle.width * 0.35f, obstacle.y - obstacle.height),
                            size = androidx.compose.ui.geometry.Size(obstacle.width * 0.3f, obstacle.height)
                        )
                        // Outline
                        drawRect(
                            color = darkGreen,
                            topLeft = Offset(obstacle.x + obstacle.width * 0.35f, obstacle.y - obstacle.height),
                            size = androidx.compose.ui.geometry.Size(obstacle.width * 0.3f, obstacle.height),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                        // Left arm
                        drawRect(
                            color = brightGreen,
                            topLeft = Offset(obstacle.x, obstacle.y - obstacle.height * 0.6f),
                            size = androidx.compose.ui.geometry.Size(obstacle.width * 0.35f, obstacle.height * 0.15f)
                        )
                        // Right arm
                        drawRect(
                            color = brightGreen,
                            topLeft = Offset(obstacle.x + obstacle.width * 0.65f, obstacle.y - obstacle.height * 0.7f),
                            size = androidx.compose.ui.geometry.Size(obstacle.width * 0.35f, obstacle.height * 0.15f)
                        )
                    }
                    com.example.bouncerunner.game.ObstacleType.TREE -> {
                        // Tree: Dark brown trunk + vibrant green leaves
                        drawRect(
                            color = Color(0xFF3E2723), // Very dark brown
                            topLeft = Offset(obstacle.x + obstacle.width * 0.35f, obstacle.y - obstacle.height * 0.6f),
                            size = androidx.compose.ui.geometry.Size(obstacle.width * 0.3f, obstacle.height * 0.6f)
                        )
                        drawCircle(
                            color = Color(0xFF66BB6A), // Vibrant green
                            radius = obstacle.width * 0.45f,
                            center = Offset(obstacle.x + obstacle.width * 0.5f, obstacle.y - obstacle.height * 0.7f)
                        )
                        // Dark green outline on leaves
                        drawCircle(
                            color = Color(0xFF2E7D32),
                            radius = obstacle.width * 0.45f,
                            center = Offset(obstacle.x + obstacle.width * 0.5f, obstacle.y - obstacle.height * 0.7f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                    com.example.bouncerunner.game.ObstacleType.ROCK -> {
                        // Rock: Dark gray with light highlights
                        drawRoundRect(
                            color = Color(0xFF424242), // Darker gray
                            topLeft = Offset(obstacle.x, obstacle.y - obstacle.height),
                            size = androidx.compose.ui.geometry.Size(obstacle.width, obstacle.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
                        )
                        // Highlight
                        drawRoundRect(
                            color = Color(0xFF9E9E9E), // Light gray highlight
                            topLeft = Offset(obstacle.x + 5f, obstacle.y - obstacle.height + 5f),
                            size = androidx.compose.ui.geometry.Size(obstacle.width * 0.4f, obstacle.height * 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                        )
                    }
                    com.example.bouncerunner.game.ObstacleType.BIRD -> {
                        // Bird: Bright orange with yellow accent
                        val centerX = obstacle.x + obstacle.width * 0.5f
                        val topY = obstacle.y - obstacle.height
                        drawLine(
                            color = Color(0xFFFF6D00), // Bright orange
                            start = Offset(centerX, topY),
                            end = Offset(obstacle.x, topY + obstacle.height * 0.4f),
                            strokeWidth = 8f
                        )
                        drawLine(
                            color = Color(0xFFFF6D00),
                            start = Offset(centerX, topY),
                            end = Offset(obstacle.x + obstacle.width, topY + obstacle.height * 0.4f),
                            strokeWidth = 8f
                        )
                        // Yellow accent in middle
                        drawCircle(
                            color = Color(0xFFFFD600),
                            radius = 8f,
                            center = Offset(centerX, topY + 5f)
                        )
                    }
                    com.example.bouncerunner.game.ObstacleType.CLOUD_OBSTACLE -> {
                        // Cloud obstacle: White with dark border
                        val cloudColor = Color(0xFFF5F5F5)
                        val borderColor = Color(0xFF757575)
                        drawCircle(
                            color = cloudColor,
                            radius = obstacle.height * 0.4f,
                            center = Offset(obstacle.x + obstacle.width * 0.3f, obstacle.y - obstacle.height * 0.5f)
                        )
                        drawCircle(
                            color = cloudColor,
                            radius = obstacle.height * 0.5f,
                            center = Offset(obstacle.x + obstacle.width * 0.6f, obstacle.y - obstacle.height * 0.5f)
                        )
                        drawCircle(
                            color = cloudColor,
                            radius = obstacle.height * 0.35f,
                            center = Offset(obstacle.x + obstacle.width * 0.8f, obstacle.y - obstacle.height * 0.4f)
                        )
                        // Border
                        drawCircle(
                            color = borderColor,
                            radius = obstacle.height * 0.5f,
                            center = Offset(obstacle.x + obstacle.width * 0.6f, obstacle.y - obstacle.height * 0.5f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                        )
                    }
                    else -> {
                        // Default rendering
                        drawRect(
                            color = Color(0xFF5D4037),
                            topLeft = Offset(obstacle.x, obstacle.y - obstacle.height),
                            size = androidx.compose.ui.geometry.Size(obstacle.width, obstacle.height)
                        )
                    }
                }
            }
            
            // Draw Player (smaller) - Moved after obstacles for correct Z-order
            val playerColor = if (gameState.isInvincible && (System.currentTimeMillis() / 100) % 2 == 0L) {
                Color.White // Flash white when invincible
            } else {
                Color.Red
            }
            
            // Shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(200f - 32f, groundLevel - 8f),
                size = androidx.compose.ui.geometry.Size(64f, 16f)
            )
            
            // Player Ball
            drawCircle(
                color = playerColor,
                radius = 40f,
                center = Offset(200f, gameState.playerY) 
            )
            
            // Draw Collectibles
            gameState.collectibles.forEach { collectible ->
                if (!collectible.isCollected) {
                    when (collectible.type) {
                        com.example.bouncerunner.game.CollectibleType.LIFE -> {
                            // Draw heart
                            val heartSize = 40f
                            drawCircle(
                                color = Color(0xFFFF5252), // Bright red
                                radius = heartSize / 2,
                                center = Offset(collectible.x, collectible.y)
                            )
                            // Heart shine
                            drawCircle(
                                color = Color.White.copy(alpha = 0.4f),
                                radius = heartSize / 4,
                                center = Offset(collectible.x + 8f, collectible.y - 8f)
                            )
                        }
                        com.example.bouncerunner.game.CollectibleType.TRIPLE_JUMP -> {
                            // Draw triple jump icon (3 arrows)
                            val arrowSize = 35f
                            val spacing = 12f
                            for (i in 0..2) {
                                val yOffset = (i - 1) * spacing
                                // Arrow up
                                drawLine(
                                    color = Color(0xFF00E676), // Bright green
                                    start = Offset(collectible.x, collectible.y + yOffset),
                                    end = Offset(collectible.x, collectible.y + yOffset - arrowSize),
                                    strokeWidth = 6f
                                )
                                // Arrow head left
                                drawLine(
                                    color = Color(0xFF00E676),
                                    start = Offset(collectible.x, collectible.y + yOffset - arrowSize),
                                    end = Offset(collectible.x - 10f, collectible.y + yOffset - arrowSize + 10f),
                                    strokeWidth = 6f
                                )
                                // Arrow head right
                                drawLine(
                                    color = Color(0xFF00E676),
                                    start = Offset(collectible.x, collectible.y + yOffset - arrowSize),
                                    end = Offset(collectible.x + 10f, collectible.y + yOffset - arrowSize + 10f),
                                    strokeWidth = 6f
                                )
                            }
                            // Glow effect
                            drawCircle(
                                color = Color(0xFF00E676).copy(alpha = 0.2f),
                                radius = 30f,
                                center = Offset(collectible.x, collectible.y)
                            )
                        }
                        else -> {}
                    }
                }
            }
            
            // Draw Particles
            gameState.particles.forEach { particle ->
                drawCircle(
                    color = Color(particle.color).copy(alpha = particle.life),
                    radius = 5f,
                    center = Offset(particle.x, particle.y)
                )
            }
        }

        // Collision Flash Effect - Red screen flash
        val flashAlpha = if (System.currentTimeMillis() - gameState.collisionFlashTime < 150) {
            0.5f // Increased intensity
        } else {
            0f
        }
        if (flashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = flashAlpha))
            )
        }
        
        // Screen Shake Effect
        val shakeOffset = if (System.currentTimeMillis() - gameState.shakeTime < 300) {
            val randomX = (Math.random() * 40 - 20).toFloat()
            val randomY = (Math.random() * 40 - 20).toFloat()
            Offset(randomX, randomY)
        } else {
            Offset.Zero
        }
        
        // Apply shake to the whole screen content (except HUD) by wrapping it? 
        // Actually, we can't easily wrap the Canvas now without restructuring.
        // Instead, let's just render the "Hit" text and Life Change text which are overlays.

        // Life Change Text Animation
        val timeSinceChange = System.currentTimeMillis() - gameState.lifeChangeTime
        if (timeSinceChange < 1000 && gameState.lifeChangeText.isNotEmpty()) {
            val alpha = 1f - (timeSinceChange / 1000f)
            val offsetY = (timeSinceChange / 10f) // Float up
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp), // Center-ish
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gameState.lifeChangeText,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (gameState.lifeChangeText.contains("+")) Color.Green else Color.Red,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = -offsetY
                            this.alpha = alpha
                            translationX = shakeOffset.x // Apply shake to text too
                            translationY = -offsetY + shakeOffset.y
                        }
                )
            }
        }

        // HUD - Redesigned Layout
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Score (left) + Lives (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score display (left)
                Row(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Score: ${gameState.score}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Best: ${gameState.highScore}",
                        color = Color.Yellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lives hearts (right) - smaller, no white background
                    repeat(gameState.maxLives) { index ->
                        Text(
                            text = if (index < gameState.lives) "❤️" else "🖤",
                            fontSize = 22.sp
                        )
                        if (index < gameState.maxLives - 1) {
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Pause button
                    if (!gameState.isGameOver) {
                        androidx.compose.material3.IconButton(
                            onClick = { 
                                if (gameState.isPaused) viewModel.resumeGame() 
                                else viewModel.pauseGame() 
                            },
                            modifier = Modifier
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                text = if (gameState.isPaused) "▶" else "⏸",
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Second Row: Active Power-Ups (centered)
            androidx.compose.animation.AnimatedVisibility(
                visible = gameState.activePowerUps.isNotEmpty(),
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically()
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                        .background(
                            color = Color(0xFF00E676).copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    gameState.activePowerUps.forEach { powerUp ->
                        when (powerUp.type) {
                            com.example.bouncerunner.game.PowerUpType.TRIPLE_JUMP -> {
                                Text(
                                    text = "⬆️",
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Triple Jump ${powerUp.duration.toInt()}s",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        // Pause Overlay
        if (gameState.isPaused && !gameState.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) {}, // Block clicks
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PAUSED",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        androidx.compose.material3.Button(
                            onClick = { viewModel.resumeGame() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Resume")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        androidx.compose.material3.OutlinedButton(
                            onClick = onNavigateToMenu,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Main Menu")
                        }
                    }
                }
            }
        }

        if (gameState.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) {}, // Block clicks
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GAME OVER",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Score: ${gameState.score}",
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        androidx.compose.material3.Button(
                            onClick = { viewModel.restartGame() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Try Again")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        androidx.compose.material3.OutlinedButton(
                            onClick = onNavigateToMenu,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Main Menu")
                        }
                    }
                }
            }
        }
    }
}
