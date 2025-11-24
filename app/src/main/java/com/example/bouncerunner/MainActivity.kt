package com.example.bouncerunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bouncerunner.ui.theme.BounceRunnerTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bouncerunner.ui.GameScreen
import com.example.bouncerunner.ui.MainMenuScreen
import com.example.bouncerunner.ui.SplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bouncerunner.game.GameViewModel
import androidx.compose.runtime.getValue
import com.example.bouncerunner.audio.SoundManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        soundManager = SoundManager()
        
        setContent {
            BounceRunnerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val hapticFeedback = LocalHapticFeedback.current
                    
                    // Custom factory approach - create manually
                    val gameViewModel = remember {
                        GameViewModel(
                            application = application,
                            soundManager = soundManager,
                            onHapticFeedback = { intensity ->
                                when (intensity) {
                                    0 -> hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    1 -> hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    2 -> hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        )
                    }
                    
                    val gameState by gameViewModel.gameState
                    
                    // Settings management
                    val settingsManager = remember { com.example.bouncerunner.game.SettingsManager(application) }
                    var currentSettings by remember { mutableStateOf(settingsManager.loadSettings()) }
                    
                    // Update SoundManager with settings
                    LaunchedEffect(currentSettings) {
                        soundManager.soundEnabled = currentSettings.soundEnabled
                        soundManager.musicEnabled = currentSettings.musicEnabled
                    }

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(onTimeout = {
                                navController.navigate("menu") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }
                        composable("menu") {
                            MainMenuScreen(
                                highScore = gameState.highScore,
                                onPlayClick = {
                                    gameViewModel.startGame() // Reset game state
                                    navController.navigate("game")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }
                        composable("settings") {
                            com.example.bouncerunner.ui.SettingsScreen(
                                settings = currentSettings,
                                onSettingsChange = { newSettings ->
                                    currentSettings = newSettings
                                    settingsManager.saveSettings(newSettings)
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("game") {
                            GameScreen(
                                viewModel = gameViewModel,
                                onNavigateToMenu = {
                                    navController.popBackStack("menu", inclusive = false)
                                }
                            )
                        }
                    }
                    
                    DisposableEffect(Unit) {
                        onDispose {
                            soundManager.release()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BounceRunnerTheme {
        Greeting("Android")
    }
}
