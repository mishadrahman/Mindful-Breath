package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Offline gentle audio chime helper using ToneGenerator.
 * Fully resilient to emulator environments without audio hardware.
 */
class SoundHelper {
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 35)
    } catch (_: Throwable) {
      toneGenerator = null
    }
  }

  fun playGentleChime() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
    } catch (_: Throwable) {
      // Ignored if audio device is unavailable
    }
  }

  fun playSessionCompleteChime() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 300)
    } catch (_: Throwable) {
      // Ignored
    }
  }

  fun release() {
    try {
      toneGenerator?.release()
      toneGenerator = null
    } catch (_: Throwable) {
      // Ignored
    }
  }
}
