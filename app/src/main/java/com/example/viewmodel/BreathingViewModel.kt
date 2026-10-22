package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AmbientPresets
import com.example.model.AmbientTrack
import com.example.model.BreathingPresets
import com.example.model.BreathingTechnique
import com.example.model.MilestonePresets
import com.example.model.PhaseStep
import com.example.model.SessionDuration
import com.example.util.AmbientAudioPlayer
import com.example.util.SoundHelper
import com.example.util.StatsPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

data class BreathingUiState(
  val technique: BreathingTechnique = BreathingPresets.boxBreathing,
  val duration: SessionDuration = SessionDuration.THREE_MIN,
  val isPlaying: Boolean = false,
  val currentStepIndex: Int = 0,
  val stepProgress: Float = 0f,
  val currentScale: Float = 1.0f,
  val secondsRemainingInStep: Int = BreathingPresets.boxBreathing.steps[0].durationSeconds,
  val elapsedSessionSeconds: Int = 0,
  val completedCycles: Int = 0,
  val hapticEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val isDarkMode: Boolean = false,
  val isSessionComplete: Boolean = false,
  val sessionBreathsCompleted: Int = 0,
  val todayTotalBreaths: Int = 18,
  val todayTotalSeconds: Int = 240,
  // Lifetime total seconds tracked for milestones; initial 96 mins so reaching 100 mins is easily experienced
  val lifetimeTotalSeconds: Int = 96 * 60,
  val ambientSoundEnabled: Boolean = false,
  val selectedAmbientTrack: AmbientTrack = AmbientPresets.rain,
  val ambientVolume: Float = 0.65f,
  val newlyUnlocked100mMilestone: Boolean = false
) {
  val currentStep: PhaseStep
    get() = technique.steps.getOrElse(currentStepIndex) { technique.steps[0] }

  val targetCycles: Int?
    get() = if (duration.totalSeconds > 0) {
      val cycles = duration.totalSeconds / technique.cycleDurationSeconds
      if (cycles < 1) 1 else cycles
    } else null

  val remainingSessionSeconds: Int?
    get() = if (duration.totalSeconds > 0) {
      (duration.totalSeconds - elapsedSessionSeconds).coerceAtLeast(0)
    } else null

  val todayMindfulMinutes: Float
    get() = todayTotalSeconds / 60f

  val lifetimeTotalMinutes: Float
    get() = lifetimeTotalSeconds / 60f

  val hasReached100Minutes: Boolean
    get() = lifetimeTotalSeconds >= 100 * 60

  val milestone100mProgress: Float
    get() = (lifetimeTotalMinutes / 100f).coerceIn(0f, 1f)

  val minutesUntil100m: Int
    get() = (((100 * 60) - lifetimeTotalSeconds) / 60).coerceAtLeast(0)

  val milestone100mSummary: String
    get() = if (hasReached100Minutes) {
      MilestonePresets.milestone100Mins.rewardSummary
    } else {
      "$minutesUntil100m mins to Century of Calm badge (100 total mins)"
    }
}

class BreathingViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(BreathingUiState())
  val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()

  private val _hapticTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
  val hapticTrigger: SharedFlow<Unit> = _hapticTrigger.asSharedFlow()

  private val soundHelper = SoundHelper()
  private val ambientPlayer = AmbientAudioPlayer()
  private var statsPreferences: StatsPreferences? = null
  private var loopJob: Job? = null

  private var elapsedMsInStep: Long = 0L
  private var accumulatedSessionMs: Long = 0L

  fun initPreferences(context: Context) {
    if (statsPreferences == null) {
      val prefs = StatsPreferences(context)
      statsPreferences = prefs
      val savedSeconds = prefs.getLifetimeSeconds()
      val savedBreaths = prefs.getLifetimeBreaths()
      _uiState.update {
        it.copy(
          lifetimeTotalSeconds = savedSeconds,
          todayTotalBreaths = savedBreaths
        )
      }
    }
  }

  fun togglePlayPause() {
    if (_uiState.value.isPlaying) {
      pause()
    } else {
      start()
    }
  }

  fun start() {
    if (_uiState.value.isPlaying) return

    _uiState.update { it.copy(isPlaying = true, isSessionComplete = false) }

    loopJob?.cancel()
    loopJob = viewModelScope.launch {
      val tickIntervalMs = 40L
      while (true) {
        delay(tickIntervalMs)
        tick(tickIntervalMs)
      }
    }
  }

  fun pause() {
    loopJob?.cancel()
    loopJob = null
    _uiState.update { it.copy(isPlaying = false) }
  }

  fun reset() {
    loopJob?.cancel()
    loopJob = null
    elapsedMsInStep = 0L
    accumulatedSessionMs = 0L

    val technique = _uiState.value.technique
    _uiState.update {
      it.copy(
        isPlaying = false,
        currentStepIndex = 0,
        stepProgress = 0f,
        currentScale = technique.steps[0].startScale,
        secondsRemainingInStep = technique.steps[0].durationSeconds,
        elapsedSessionSeconds = 0,
        completedCycles = 0,
        isSessionComplete = false
      )
    }
  }

  fun selectTechnique(newTechnique: BreathingTechnique) {
    if (_uiState.value.technique.id == newTechnique.id) return
    loopJob?.cancel()
    loopJob = null
    elapsedMsInStep = 0L
    accumulatedSessionMs = 0L

    _uiState.update {
      it.copy(
        technique = newTechnique,
        isPlaying = false,
        currentStepIndex = 0,
        stepProgress = 0f,
        currentScale = newTechnique.steps[0].startScale,
        secondsRemainingInStep = newTechnique.steps[0].durationSeconds,
        elapsedSessionSeconds = 0,
        completedCycles = 0,
        isSessionComplete = false
      )
    }
  }

  fun selectDuration(newDuration: SessionDuration) {
    _uiState.update { it.copy(duration = newDuration) }
  }

  fun toggleHaptic() {
    _uiState.update { it.copy(hapticEnabled = !it.hapticEnabled) }
  }

  fun toggleSound() {
    _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
  }

  fun toggleDarkMode() {
    _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
  }

  // --- Background Nature Ambient Sound Player ---

  fun toggleAmbientSound(context: Context) {
    val willEnable = !_uiState.value.ambientSoundEnabled
    _uiState.update { it.copy(ambientSoundEnabled = willEnable) }

    if (willEnable) {
      ambientPlayer.playTrack(
        context = context,
        resId = _uiState.value.selectedAmbientTrack.resId,
        volume = _uiState.value.ambientVolume
      )
    } else {
      ambientPlayer.pause()
    }
  }

  fun selectAmbientTrack(context: Context, track: AmbientTrack) {
    _uiState.update { it.copy(selectedAmbientTrack = track) }
    if (_uiState.value.ambientSoundEnabled) {
      ambientPlayer.playTrack(
        context = context,
        resId = track.resId,
        volume = _uiState.value.ambientVolume
      )
    }
  }

  fun setAmbientVolume(volume: Float) {
    val clamped = volume.coerceIn(0f, 1f)
    _uiState.update { it.copy(ambientVolume = clamped) }
    ambientPlayer.setVolume(clamped)
  }

  fun pauseAmbientOnAppBackground() {
    if (_uiState.value.ambientSoundEnabled) {
      ambientPlayer.pause()
    }
  }

  fun resumeAmbientOnAppForeground(context: Context) {
    if (_uiState.value.ambientSoundEnabled) {
      ambientPlayer.playTrack(
        context = context,
        resId = _uiState.value.selectedAmbientTrack.resId,
        volume = _uiState.value.ambientVolume
      )
    }
  }

  fun dismissSessionComplete() {
    _uiState.update { it.copy(isSessionComplete = false) }
    reset()
  }

  /**
   * Quick action to add mindful minutes (useful for milestone testing / verification)
   */
  fun addTestMindfulMinutes(minutes: Int) {
    val addedSeconds = minutes * 60
    val newTotal = _uiState.value.lifetimeTotalSeconds + addedSeconds
    val newlyReached = !_uiState.value.hasReached100Minutes && newTotal >= 100 * 60
    statsPreferences?.saveLifetimeSeconds(newTotal)

    _uiState.update {
      it.copy(
        lifetimeTotalSeconds = newTotal,
        todayTotalSeconds = it.todayTotalSeconds + addedSeconds,
        newlyUnlocked100mMilestone = newlyReached || it.newlyUnlocked100mMilestone
      )
    }
  }

  private fun tick(deltaMs: Long) {
    val currentState = _uiState.value
    val technique = currentState.technique
    val currentStep = technique.steps[currentState.currentStepIndex]
    val stepDurationMs = currentStep.durationSeconds * 1000L

    elapsedMsInStep += deltaMs
    accumulatedSessionMs += deltaMs

    val rawProgress = (elapsedMsInStep.toFloat() / stepDurationMs.toFloat()).coerceIn(0f, 1f)
    val remainingMs = (stepDurationMs - elapsedMsInStep).coerceAtLeast(0L)
    val remainingSecs = ceil(remainingMs / 1000.0).toInt().coerceAtLeast(1)

    // Calculate currentScale smoothly based on step's startScale and endScale
    val currentScale = currentStep.startScale + (currentStep.endScale - currentStep.startScale) * rawProgress
    val elapsedSessionSecs = (accumulatedSessionMs / 1000L).toInt()

    if (elapsedMsInStep >= stepDurationMs) {
      // Step complete -> transition to next step
      elapsedMsInStep = 0L
      val nextStepIndex = (currentState.currentStepIndex + 1) % technique.steps.size
      val isCycleFinished = nextStepIndex == 0

      val newCompletedCycles = if (isCycleFinished) currentState.completedCycles + 1 else currentState.completedCycles
      val newBreaths = if (isCycleFinished) currentState.sessionBreathsCompleted + 1 else currentState.sessionBreathsCompleted
      val newTodayBreaths = if (isCycleFinished) currentState.todayTotalBreaths + 1 else currentState.todayTotalBreaths
      val newTodaySecs = currentState.todayTotalSeconds + currentStep.durationSeconds

      val newLifetimeSecs = currentState.lifetimeTotalSeconds + currentStep.durationSeconds
      val newlyReached100m = !currentState.hasReached100Minutes && (newLifetimeSecs >= 100 * 60)

      statsPreferences?.saveLifetimeSeconds(newLifetimeSecs)
      statsPreferences?.saveLifetimeBreaths(newTodayBreaths)

      // Check session completion
      val isDurationFinished = currentState.duration.totalSeconds > 0 &&
          accumulatedSessionMs >= currentState.duration.totalSeconds * 1000L

      if (isDurationFinished) {
        pause()
        if (currentState.soundEnabled) {
          soundHelper.playSessionCompleteChime()
        }
        if (currentState.hapticEnabled) {
          _hapticTrigger.tryEmit(Unit)
        }
        _uiState.update {
          it.copy(
            isPlaying = false,
            isSessionComplete = true,
            completedCycles = newCompletedCycles,
            sessionBreathsCompleted = newBreaths,
            todayTotalBreaths = newTodayBreaths,
            todayTotalSeconds = newTodaySecs,
            lifetimeTotalSeconds = newLifetimeSecs,
            newlyUnlocked100mMilestone = currentState.newlyUnlocked100mMilestone || newlyReached100m,
            elapsedSessionSeconds = elapsedSessionSecs,
            stepProgress = 0f,
            currentScale = 1.0f
          )
        }
        return
      }

      // Next step notification
      if (currentState.soundEnabled) {
        soundHelper.playGentleChime()
      }
      if (currentState.hapticEnabled) {
        _hapticTrigger.tryEmit(Unit)
      }

      val nextStep = technique.steps[nextStepIndex]
      _uiState.update {
        it.copy(
          currentStepIndex = nextStepIndex,
          stepProgress = 0f,
          currentScale = nextStep.startScale,
          secondsRemainingInStep = nextStep.durationSeconds,
          completedCycles = newCompletedCycles,
          sessionBreathsCompleted = newBreaths,
          todayTotalBreaths = newTodayBreaths,
          todayTotalSeconds = newTodaySecs,
          lifetimeTotalSeconds = newLifetimeSecs,
          newlyUnlocked100mMilestone = currentState.newlyUnlocked100mMilestone || newlyReached100m,
          elapsedSessionSeconds = elapsedSessionSecs
        )
      }
    } else {
      // Step continues
      _uiState.update {
        it.copy(
          stepProgress = rawProgress,
          currentScale = currentScale,
          secondsRemainingInStep = remainingSecs,
          elapsedSessionSeconds = elapsedSessionSecs
        )
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    loopJob?.cancel()
    soundHelper.release()
    ambientPlayer.release()
  }
}
