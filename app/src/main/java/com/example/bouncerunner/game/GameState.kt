package com.example.bouncerunner.game

data class GameState(
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val score: Int = 0,
    val highScore: Int = 0,
    val playerY: Float = 0f,
    val playerVelocity: Float = 0f,
    val jumpsRemaining: Int = 2,
    val obstacles: List<Obstacle> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val environmentObjects: List<EnvironmentObject> = emptyList(),
    val lives: Int = 3,
    val maxLives: Int = 3,
    val isInvincible: Boolean = false,
    val invincibleUntil: Long = 0,
    val collectibles: List<Collectible> = emptyList(),
    val activePowerUps: List<ActivePowerUp> = emptyList(),
    val comboCount: Int = 0,
    val lastJumpTime: Long = 0,
    val lifeChangeText: String = "",
    val lifeChangeTime: Long = 0,
    val collisionFlashTime: Long = 0,
    val shakeTime: Long = 0
)

data class Obstacle(
    val id: Long,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: ObstacleType = ObstacleType.GROUND,
    val animationOffset: Float = 0f
)

enum class ObstacleType {
    GROUND,
    FLYING,
    TALL,
    CACTUS,
    TREE,
    ROCK,
    BIRD,
    CLOUD_OBSTACLE
}

data class Particle(
    val id: Long,
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Long,
    val life: Float
)

data class EnvironmentObject(
    val id: Long,
    val x: Float,
    val y: Float,
    val type: EnvironmentType,
    val alpha: Float = 1.0f
)

enum class EnvironmentType {
    CLOUD,
    STAR,
    SUN,
    MOON
}

data class Collectible(
    val id: Long,
    val x: Float,
    val y: Float,
    val type: CollectibleType,
    val isCollected: Boolean = false
)

enum class CollectibleType {
    LIFE,
    TRIPLE_JUMP,
    SHIELD,
    MAGNET,
    INVINCIBILITY,
    DESTROYER
}

data class ActivePowerUp(
    val type: PowerUpType,
    val startTime: Long,
    val duration: Float
)

enum class PowerUpType {
    TRIPLE_JUMP,
    SHIELD,
    MAGNET,
    INVINCIBILITY,
    DESTROYER
}
