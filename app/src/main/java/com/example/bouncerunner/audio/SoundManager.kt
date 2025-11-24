package com.example.bouncerunner.audio

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SoundManager {
    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var musicJob: Job? = null
    private var isMusicPlaying = false
    
    var soundEnabled = true
    var musicEnabled = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playJump() {
        if (!soundEnabled) return
        scope.launch {
            try {
                // Pleasant high note
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_8, 35)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playBounce() {
        if (!soundEnabled) return
        scope.launch {
            try {
                // Soft, pleasant bounce
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_4, 25)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playCollision() {
        if (!soundEnabled) return
        scope.launch {
            try {
                // Gentle notification sound instead of harsh
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 200)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playScore() {
        if (!soundEnabled) return
        scope.launch {
            try {
                // Pleasant reward tone
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun playCollectible() {
        if (!soundEnabled) return
        scope.launch {
            try {
                // Pleasant pickup sound
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 50)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun playPowerUp() {
        if (!soundEnabled) return
        scope.launch {
            try {
                // Exciting power-up sound
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 80)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startBackgroundMusic() {
        if (!musicEnabled || isMusicPlaying) return
        isMusicPlaying = true
        
        musicJob = scope.launch {
            val melody = listOf(
                ToneGenerator.TONE_DTMF_5 to 200L,
                ToneGenerator.TONE_DTMF_7 to 200L,
                ToneGenerator.TONE_DTMF_9 to 200L,
                ToneGenerator.TONE_DTMF_7 to 200L
            )
            
            while (isActive && isMusicPlaying && musicEnabled) {
                melody.forEach { (tone, duration) ->
                    if (!isMusicPlaying || !musicEnabled) return@launch
                    try {
                        toneGenerator?.startTone(tone, duration.toInt())
                        delay(duration + 100)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                delay(500)
            }
        }
    }

    fun stopBackgroundMusic() {
        isMusicPlaying = false
        musicJob?.cancel()
        musicJob = null
    }

    fun release() {
        stopBackgroundMusic()
        toneGenerator?.release()
        toneGenerator = null
    }
}
