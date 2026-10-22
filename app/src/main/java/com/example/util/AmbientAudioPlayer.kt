package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.annotation.RawRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Robust background ambient sound player that loops local raw audio resources
 * with gentle fade-in and fade-out transitions.
 * Fully resilient to any emulator audio hardware limitation.
 */
class AmbientAudioPlayer {

  private var mediaPlayer: MediaPlayer? = null
  private var currentResId: Int? = null
  private var targetVolume: Float = 0.7f
  private var fadeJob: Job? = null
  private val playerScope = CoroutineScope(Dispatchers.Default)

  fun playTrack(context: Context, @RawRes resId: Int, volume: Float = targetVolume) {
    targetVolume = volume

    if (mediaPlayer != null && currentResId == resId) {
      if (!isPlaying()) {
        resume()
      } else {
        setVolume(volume)
      }
      return
    }

    // Switch or new track
    fadeJob?.cancel()
    releaseCurrentPlayer()

    try {
      val appContext = context.applicationContext ?: context
      val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()

      val player = MediaPlayer.create(appContext, resId, audioAttributes, 0)
        ?: MediaPlayer.create(appContext, resId)
        ?: return

      player.isLooping = true
      player.setVolume(0f, 0f)
      player.start()

      mediaPlayer = player
      currentResId = resId

      // Gentle fade in
      fadeIn(volume)
    } catch (_: Throwable) {
      releaseCurrentPlayer()
    }
  }

  fun pause() {
    fadeJob?.cancel()
    val player = mediaPlayer ?: return
    fadeJob = playerScope.launch {
      fadeOut(durationMs = 250)
      try {
        if (player.isPlaying) {
          player.pause()
        }
      } catch (_: Throwable) {
        // Ignored
      }
    }
  }

  fun resume() {
    val player = mediaPlayer ?: return
    try {
      if (!player.isPlaying) {
        player.setVolume(0f, 0f)
        player.start()
        fadeIn(targetVolume)
      }
    } catch (_: Throwable) {
      // Ignored
    }
  }

  fun stop() {
    fadeJob?.cancel()
    val player = mediaPlayer ?: return
    fadeJob = playerScope.launch {
      fadeOut(durationMs = 300)
      releaseCurrentPlayer()
    }
  }

  fun setVolume(volume: Float) {
    targetVolume = volume.coerceIn(0f, 1f)
    if (fadeJob?.isActive != true) {
      try {
        mediaPlayer?.setVolume(targetVolume, targetVolume)
      } catch (_: Throwable) {
        // Ignored
      }
    }
  }

  fun isPlaying(): Boolean {
    return try {
      mediaPlayer?.isPlaying == true
    } catch (_: Throwable) {
      false
    }
  }

  private fun fadeIn(targetVol: Float, durationMs: Long = 400L) {
    fadeJob?.cancel()
    val player = mediaPlayer ?: return
    fadeJob = playerScope.launch {
      val steps = 12
      val interval = durationMs / steps
      for (i in 1..steps) {
        if (!isActive) break
        val current = (targetVol * (i.toFloat() / steps)).coerceIn(0f, 1f)
        try {
          player.setVolume(current, current)
        } catch (_: Throwable) {
          break
        }
        delay(interval)
      }
      try {
        player.setVolume(targetVol, targetVol)
      } catch (_: Throwable) {}
    }
  }

  private suspend fun fadeOut(durationMs: Long = 300L) {
    val player = mediaPlayer ?: return
    val steps = 10
    val interval = durationMs / steps
    val startVol = targetVolume
    for (i in 1..steps) {
      val current = (startVol * (1f - (i.toFloat() / steps))).coerceIn(0f, 1f)
      try {
        player.setVolume(current, current)
      } catch (_: Throwable) {
        break
      }
      delay(interval)
    }
    try {
      player.setVolume(0f, 0f)
    } catch (_: Throwable) {}
  }

  private fun releaseCurrentPlayer() {
    try {
      mediaPlayer?.apply {
        if (isPlaying) {
          stop()
        }
        release()
      }
    } catch (_: Throwable) {
      // Ignored
    } finally {
      mediaPlayer = null
      currentResId = null
    }
  }

  fun release() {
    fadeJob?.cancel()
    releaseCurrentPlayer()
  }
}
