package com.example.bouncerunner.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import com.example.bouncerunner.game.Settings

@Composable
fun SettingsScreen(
    settings: Settings,
    onSettingsChange: (Settings) -> Unit,
    onBackClick: () -> Unit
) {
    var currentSettings by remember { mutableStateOf(settings) }
    
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
        // Decorative elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Small decorative circles
            for (i in 0..3) {
                val x = size.width - (i * 100f) - 50f
                val y = 80f + (i % 2) * 40f
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 25f,
                    center = Offset(x, y)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "← Back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title - White Settings Icon (No background)
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "SETTINGS",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                style = MaterialTheme.typography.displayLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFF1565C0),
                        offset = Offset(3f, 3f),
                        blurRadius = 6f
                    )
                )
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Sound Effects Toggle
                    SettingRow(
                        icon = "🔊",
                        title = "Sound Effects",
                        description = "Game sounds",
                        checked = currentSettings.soundEnabled,
                        onCheckedChange = { enabled ->
                            currentSettings = currentSettings.copy(soundEnabled = enabled)
                            onSettingsChange(currentSettings)
                        }
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.LightGray
                    )
                    
                    // Background Music Toggle
                    SettingRow(
                        icon = "🎵",
                        title = "Background Music",
                        description = "During gameplay",
                        checked = currentSettings.musicEnabled,
                        onCheckedChange = { enabled ->
                            currentSettings = currentSettings.copy(musicEnabled = enabled)
                            onSettingsChange(currentSettings)
                        }
                    )
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.LightGray
                    )
                    
                    // Haptic Feedback Toggle
                    SettingRow(
                        icon = "📳",
                        title = "Vibration",
                        description = "Haptic feedback",
                        checked = currentSettings.hapticEnabled,
                        onCheckedChange = { enabled ->
                            currentSettings = currentSettings.copy(hapticEnabled = enabled)
                            onSettingsChange(currentSettings)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Version info
            Text(
                text = "Bounce Runner v1.0",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: String,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 28.sp // Slightly smaller icon
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 17.sp, // Smaller title
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp, // Smaller description
                    color = Color.Gray
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF66BB6A),
                checkedTrackColor = Color(0xFFA5D6A7),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}
