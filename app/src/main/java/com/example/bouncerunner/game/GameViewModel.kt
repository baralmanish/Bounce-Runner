package com.example.bouncerunner.game

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import com.example.bouncerunner.audio.SoundManager

class GameViewModel(
    application: Application,
    private val soundManager: SoundManager? = null,
    private val onHapticFeedback: ((Int) -> Unit)? = null // 0=light, 1=medium, 2=heavy
) : AndroidViewModel(application) {
    private val _gameState = mutableStateOf(GameState())
    val gameState: State<GameState> = _gameState

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("bounce_runner_prefs", Context.MODE_PRIVATE)
    
    private var gameLoopJob: Job? = null
    private var screenWidth = 0f
    private var screenHeight = 0f
    private var groundLevel = 1000f // Default, will be updated

    fun setScreenSize(width: Float, height: Float) {
        if (screenWidth == width && screenHeight == height) return
        screenWidth = width
        screenHeight = height
        groundLevel = height * 0.75f // Ground at 75% of screen height (25% from bottom)
        
        // Reset player if just starting
        if (_gameState.value.score == 0 && !_gameState.value.isGameOver) {
             _gameState.value = _gameState.value.copy(playerY = groundLevel - 50f)
        }
    }

    private val gravity = 0.6f
    private val jumpStrength = -12f // Reduced default bounce (was -15f)
    private val playerRadius = 40f // Smaller player ball

    init {
        loadHighScore()
        startGame()
    }

    private fun loadHighScore() {
        val savedHighScore = sharedPreferences.getInt("high_score", 0)
        _gameState.value = _gameState.value.copy(highScore = savedHighScore)
    }

    private fun saveHighScore(score: Int) {
        sharedPreferences.edit().putInt("high_score", score).apply()
    }

    fun startGame() {
        val currentHighScore = _gameState.value.highScore
        _gameState.value = GameState(
            playerY = groundLevel - playerRadius, // Start on ground
            highScore = currentHighScore,
            environmentObjects = initializeEnvironment()
        )
        gameOverTime = 0L
        soundManager?.startBackgroundMusic()
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive) {
                updateGame(16L) // Approx 60 FPS
                delay(16L)
            }
        }
    }

    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = true)
        soundManager?.stopBackgroundMusic()
        onHapticFeedback?.invoke(0) // Light haptic
    }

    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
        soundManager?.startBackgroundMusic()
    }

    private var timeSinceLastSpawn = 0L
    private var spawnInterval = 5000L // Slower spawn interval (5 seconds for easier start)
    private var timeSinceLastCollectible = 0L
    private var collectibleSpawnInterval = 8000L // Spawn collectible every 8 seconds
    private var obstacleSpeed = 2.5f // Slower initial speed (was 3f)
    private var envUpdateCounter = 0L

    private fun initializeEnvironment(): List<EnvironmentObject> {
        val objects = mutableListOf<EnvironmentObject>()
        
        // Add sun or moon based on initial score (always sun at start)
        objects.add(EnvironmentObject(
            id = System.nanoTime(),
            x = screenWidth * 0.8f,
            y = 150f,
            type = EnvironmentType.SUN
        ))
        
        // Add some initial clouds
        for (i in 0..2) {
            objects.add(EnvironmentObject(
                id = System.nanoTime() + i,
                x = (Math.random().toFloat() * screenWidth),
                y = 100f + (Math.random().toFloat() * 200f),
                type = EnvironmentType.CLOUD
            ))
        }
        
        return objects
    }

    private fun spawnParticles(x: Float, y: Float, count: Int = 10, color: Long = 0xFFFFFFFF) {
        val currentParticles = _gameState.value.particles
        val newParticles = (0 until count).map {
            Particle(
                id = System.nanoTime() + it,
                x = x,
                y = y,
                velocityX = (Math.random().toFloat() - 0.5f) * 20f,
                velocityY = (Math.random().toFloat() - 0.5f) * 20f,
                color = color,
                life = 1.0f
            )
        }
        _gameState.value = _gameState.value.copy(particles = currentParticles + newParticles)
    }

    private fun updateGame(deltaTime: Long) {
        val currentState = _gameState.value
        if (currentState.isGameOver || currentState.isPaused) return

        // Difficulty Scaling
        // Gradually increase speed        // Game Loop Logic
        val difficultyMultiplier = 1f + (currentState.score / 200f) // Slower difficulty ramp (was 100f)
        val currentSpeed = obstacleSpeed * difficultyMultiplier
        val currentSpawnInterval = (spawnInterval / difficultyMultiplier).toLong().coerceAtLeast(1000L)

        // Physics update
        var newVelocity = currentState.playerVelocity + gravity
        var newY = currentState.playerY + newVelocity

        // Floor collision (simple bounce)
        // playerY is center. Ground is at groundLevel.
        // Collision when playerY + playerRadius >= groundLevel
        var jumps = currentState.jumpsRemaining
        
        // Check if triple jump is active
        val hasTripleJump = currentState.activePowerUps.any { it.type == PowerUpType.TRIPLE_JUMP }
        val maxJumps = if (hasTripleJump) 3 else 2
        
        if (newY + playerRadius >= groundLevel) {
            newY = groundLevel - playerRadius
            newVelocity = jumpStrength // Auto bounce
            jumps = maxJumps // Reset jumps on bounce
            soundManager?.playBounce()
            onHapticFeedback?.invoke(0) // Light haptic on bounce
        }

        // Obstacle Spawning
        timeSinceLastSpawn += deltaTime
        var newObstacles = currentState.obstacles.map { 
            it.copy(x = it.x - currentSpeed) 
        }.filter { it.x + it.width > 0 } // Remove off-screen obstacles

        if (timeSinceLastSpawn >= currentSpawnInterval) {
            timeSinceLastSpawn = 0
            
            // Varied obstacle types based on score
            val canSpawnFlying = currentState.score > 50
            val random = Math.random().toFloat()
            
            val type = when {
                !canSpawnFlying -> {
                    // Ground obstacles only
                    when {
                        random < 0.4f -> ObstacleType.CACTUS
                        random < 0.7f -> ObstacleType.ROCK
                        else -> ObstacleType.TREE
                    }
                }
                else -> {
                    // Mix of ground and flying
                    when {
                        random < 0.25f -> ObstacleType.CACTUS
                        random < 0.45f -> ObstacleType.ROCK
                        random < 0.6f -> ObstacleType.TREE
                        random < 0.8f -> ObstacleType.BIRD
                        else -> ObstacleType.CLOUD_OBSTACLE
                    }
                }
            }
            
            // Size and position based on type
            val (obstacleWidth, obstacleHeight, obstacleY) = when (type) {
                ObstacleType.CACTUS -> Triple(60f, 120f, groundLevel)
                ObstacleType.TREE -> Triple(100f, 140f, groundLevel)
                ObstacleType.ROCK -> Triple(80f, 60f, groundLevel)
                ObstacleType.BIRD -> Triple(70f, 50f, groundLevel - 200f - (Math.random().toFloat() * 150f))
                ObstacleType.CLOUD_OBSTACLE -> Triple(90f, 60f, groundLevel - 250f - (Math.random().toFloat() * 100f))
                else -> Triple(100f, 100f, groundLevel)
            }

            val newObstacle = Obstacle(
                id = System.currentTimeMillis(),
                x = screenWidth + 100f,
                y = obstacleY,
                width = obstacleWidth,
                height = obstacleHeight,
                type = type
            )
            newObstacles = newObstacles + newObstacle
        }

        // Collectible Spawning
        timeSinceLastCollectible += deltaTime
        var newCollectibles = currentState.collectibles.map {
            it.copy(x = it.x - currentSpeed)
        }.filter { it.x + 40f > 0 && !it.isCollected }
        
        if (timeSinceLastCollectible >= collectibleSpawnInterval) {
            timeSinceLastCollectible = 0
            // Randomly choose collectible type
            val collectibleType = if (Math.random() < 0.6) {
                CollectibleType.LIFE
            } else {
                CollectibleType.TRIPLE_JUMP
            }
            val newCollectible = Collectible(
                id = System.currentTimeMillis(),
                x = screenWidth + 50f,
                y = groundLevel - 200f, // Floating above ground
                type = collectibleType
            )
            newCollectibles = newCollectibles + newCollectible
        }
        
        // Collision Detection with Obstacles
        val playerLogicX = 200f
        var newLives = currentState.lives
        var isInvincible = currentState.isInvincible
        var invincibleUntil = currentState.invincibleUntil
        var newParticles = currentState.particles // Get current particles
        
        // Check if invincibility expired
        if (isInvincible && System.currentTimeMillis() > invincibleUntil) {
            isInvincible = false
        }
        
        for (obstacle in newObstacles) {
            val playerLeft = playerLogicX - playerRadius
            val playerRight = playerLogicX + playerRadius
            val playerTop = newY - playerRadius
            val playerBottom = newY + playerRadius
            
            val obstacleLeft = obstacle.x
            val obstacleRight = obstacle.x + obstacle.width
            val obstacleTop = obstacle.y - obstacle.height
            val obstacleBottom = obstacle.y
            
            if (playerRight > obstacleLeft && 
                playerLeft < obstacleRight && 
                playerBottom > obstacleTop && 
                playerTop < obstacleBottom) {
                
                if (!isInvincible) {
                    newLives -= 1
                    soundManager?.playCollision()
                    onHapticFeedback?.invoke(2) // Heavy haptic
                    
                    // Set life change notification
                    val currentTime = System.currentTimeMillis()
                    
                    // Create collision particles (red/orange explosion)
                    val collisionParticles = List(15) { i ->
                        val angle = (i * 360f / 15f) * (Math.PI / 180f)
                        val speed = 8f + (Math.random() * 4f).toFloat()
                        Particle(
                            id = currentTime + i,
                            x = playerLogicX,
                            y = newY,
                            velocityX = (Math.cos(angle) * speed).toFloat(),
                            velocityY = (Math.sin(angle) * speed).toFloat(),
                            color = if (Math.random() > 0.5) 0xFFFF5252 else 0xFFFF6D00, // Red or orange
                            life = 1.0f
                        )
                    }
                    newParticles = newParticles + collisionParticles
                    
                    _gameState.value = currentState.copy(
                        lifeChangeText = "-1 ❤️",
                        lifeChangeTime = currentTime,
                        collisionFlashTime = currentTime,
                        shakeTime = currentTime,
                        particles = newParticles
                    )
                    
                    // Grant temporary invincibility (1 second)
                    isInvincible = true
                    invincibleUntil = currentTime + 1000L
                    
                    // Check for game over
                    if (newLives <= 0) {
                        gameOverTime = currentTime
                    }
                }
                break
            }
        }
        
        // Collectible Collision Detection
        var newActivePowerUps = currentState.activePowerUps.map { powerUp ->
            powerUp.copy(
                duration = powerUp.duration - (deltaTime / 1000f)
            )
        }.filter { it.duration > 0 }
        
        newCollectibles = newCollectibles.map { collectible ->
            if (collectible.isCollected) return@map collectible
            
            val collectibleSize = 40f
            val playerLeft = playerLogicX - playerRadius
            val playerRight = playerLogicX + playerRadius
            val playerTop = newY - playerRadius
            val playerBottom = newY + playerRadius
            
            val collectibleLeft = collectible.x - collectibleSize / 2
            val collectibleRight = collectible.x + collectibleSize / 2
            val collectibleTop = collectible.y - collectibleSize / 2
            val collectibleBottom = collectible.y + collectibleSize / 2
            
            if (playerRight > collectibleLeft &&
                playerLeft < collectibleRight &&
                playerBottom > collectibleTop &&
                playerTop < collectibleBottom) {
                
                // Collect it!
                when (collectible.type) {
                    CollectibleType.LIFE -> {
                        if (newLives < currentState.maxLives) {
                            newLives += 1
                            // Set life change notification
                            _gameState.value = currentState.copy(
                                lifeChangeText = "+1 ❤️",
                                lifeChangeTime = System.currentTimeMillis()
                            )
                        }
                        soundManager?.playCollectible()
                        onHapticFeedback?.invoke(0) // Light haptic
                    }
                    CollectibleType.TRIPLE_JUMP -> {
                        // Activate triple jump power-up for 10 seconds
                        val tripleJumpPowerUp = ActivePowerUp(
                            type = PowerUpType.TRIPLE_JUMP,
                            duration = 10f,
                            startTime = System.currentTimeMillis()
                        )
                        newActivePowerUps = newActivePowerUps.filter { it.type != PowerUpType.TRIPLE_JUMP } + tripleJumpPowerUp
                        soundManager?.playPowerUp()
                        onHapticFeedback?.invoke(1) // Medium haptic
                    }
                    else -> {}
                }
                
                collectible.copy(isCollected = true)
            } else {
                collectible
            }
        }

        var newScore = currentState.score + 1
        var newHighScore = currentState.highScore
        if (newScore > newHighScore) {
            newHighScore = newScore
            saveHighScore(newHighScore)
        }
        
        // Score milestone sound (every 10 points)
        if (newScore % 10 == 0 && newScore != currentState.score) {
            soundManager?.playScore()
            onHapticFeedback?.invoke(1) // Medium haptic
        }

        // Update environment objects
        envUpdateCounter += deltaTime
        var newEnvObjects = currentState.environmentObjects
        
        if (envUpdateCounter >= 50) { // Update every ~50ms
            envUpdateCounter = 0
            
            // Update clouds (parallax scroll)
            newEnvObjects = newEnvObjects.map { obj ->
                if (obj.type == EnvironmentType.CLOUD) {
                    val newX = obj.x - 1f // Slow movement
                    if (newX < -100f) {
                        // Respawn on right
                        obj.copy(x = screenWidth + 50f, y = 100f + (Math.random().toFloat() * 200f))
                    } else {
                        obj.copy(x = newX)
                    }
                } else if (obj.type == EnvironmentType.STAR) {
                    // Twinkle stars
                    obj.copy(alpha = 0.3f + (Math.random().toFloat() * 0.7f))
                } else {
                    obj
                }
            }.toMutableList()
            
            // Update sun/moon based on score
            val shouldHaveMoon = newScore >= 100
            val hasMoon = newEnvObjects.any { it.type == EnvironmentType.MOON }
            val hasSun = newEnvObjects.any { it.type == EnvironmentType.SUN }
            
            if (shouldHaveMoon && !hasMoon) {
                // Replace sun with moon and add stars
                newEnvObjects = newEnvObjects.filter { it.type != EnvironmentType.SUN }.toMutableList()
                newEnvObjects.add(EnvironmentObject(
                    id = System.nanoTime(),
                    x = screenWidth * 0.8f,
                    y = 150f,
                    type = EnvironmentType.MOON
                ))
                // Add stars
                for (i in 0..15) {
                    newEnvObjects.add(EnvironmentObject(
                        id = System.nanoTime() + i,
                        x = Math.random().toFloat() * screenWidth,
                        y = Math.random().toFloat() * (groundLevel * 0.8f),
                        type = EnvironmentType.STAR,
                        alpha = Math.random().toFloat()
                    ))
                }
            } else if (!shouldHaveMoon && !hasSun && hasMoon) {
                // Replace moon with sun and remove stars
                newEnvObjects = newEnvObjects.filter { it.type != EnvironmentType.MOON && it.type != EnvironmentType.STAR }.toMutableList()
                newEnvObjects.add(EnvironmentObject(
                    id = System.nanoTime(),
                    x = screenWidth * 0.8f,
                    y = 150f,
                    type = EnvironmentType.SUN
                ))
            }
        }

        // Update particles
        val updatedParticles = currentState.particles
            .map { it.copy(
                x = it.x + it.velocityX,
                y = it.y + it.velocityY,
                life = it.life - (deltaTime / 1000f) // Decrease life over time
            ) }
            .filter { it.life > 0 } // Remove dead particles

        _gameState.value = currentState.copy(
            playerY = newY,
            playerVelocity = newVelocity,
            score = newScore,
            highScore = newHighScore,
            obstacles = newObstacles,
            isGameOver = newLives <= 0,
            jumpsRemaining = jumps,
            particles = updatedParticles,
            environmentObjects = newEnvObjects,
            lives = newLives,
            isInvincible = isInvincible,
            invincibleUntil = invincibleUntil,
            collectibles = newCollectibles,
            activePowerUps = newActivePowerUps
        )
    }

    private var gameOverTime = 0L

    fun restartGame() {
        if (System.currentTimeMillis() - gameOverTime < 500) return // 500ms cooldown
        startGame()
    }

    fun onTap() {
        val currentState = _gameState.value
        if (currentState.isGameOver) {
            restartGame()
            return
        }
        
        if (currentState.isPaused) return // Don't allow jumping while paused
        
        if (currentState.jumpsRemaining > 0) {
            soundManager?.playJump()
            onHapticFeedback?.invoke(0) // Light haptic
            spawnParticles(200f, currentState.playerY, 8, 0xFFFFFF00) // Yellow sparks (reduced)
            _gameState.value = currentState.copy(
                playerVelocity = -18f, // Higher jump on press (was -12f)
                jumpsRemaining = currentState.jumpsRemaining - 1
            )
        }
    }
}
