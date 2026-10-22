package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AmbientPresets
import com.example.model.BreathingPhaseType
import com.example.model.BreathingPresets
import com.example.model.MilestonePresets
import com.example.model.SessionDuration
import com.example.viewmodel.BreathingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mindful Breath", appName)
  }

  @Test
  fun `verify MainActivity launches cleanly without crash`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
    val activity = controller.create().get()
    assertNotNull(activity)
  }

  @Test
  fun `verify breathing presets have valid configurations`() {
    assertEquals(7, BreathingPresets.allTechniques.size)

    val box = BreathingPresets.boxBreathing
    assertEquals("4-4-4-4", box.formula)
    assertEquals(16, box.cycleDurationSeconds)
    assertEquals(4, box.steps.size)
    assertEquals(BreathingPhaseType.INHALE, box.steps[0].type)
    assertEquals(BreathingPhaseType.HOLD_IN, box.steps[1].type)
    assertEquals(BreathingPhaseType.EXHALE, box.steps[2].type)
    assertEquals(BreathingPhaseType.HOLD_OUT, box.steps[3].type)

    val r478 = BreathingPresets.relaxation478
    assertEquals("4-7-8", r478.formula)
    assertEquals(19, r478.cycleDurationSeconds)

    val equal = BreathingPresets.equalBreathing
    assertEquals("5-5", equal.formula)
    assertEquals(10, equal.cycleDurationSeconds)

    val coherent = BreathingPresets.coherentBreathing
    assertEquals("6-6", coherent.formula)
    assertEquals(12, coherent.cycleDurationSeconds)

    val sigh = BreathingPresets.physiologicalSigh
    assertEquals("3+1-6", sigh.formula)
    assertEquals(10, sigh.cycleDurationSeconds)

    val calm711 = BreathingPresets.deepCalm711
    assertEquals("4-7", calm711.formula)
    assertEquals(11, calm711.cycleDurationSeconds)

    val vitality = BreathingPresets.energizingBreath
    assertEquals("4-2-4-2", vitality.formula)
    assertEquals(12, vitality.cycleDurationSeconds)
  }

  @Test
  fun `verify ambient sound presets and resource validity`() {
    assertEquals(4, AmbientPresets.allTracks.size)

    val rain = AmbientPresets.rain
    assertEquals("rain", rain.id)
    assertEquals(R.raw.ambient_rain, rain.resId)

    val ocean = AmbientPresets.ocean
    assertEquals("ocean", ocean.id)
    assertEquals(R.raw.ambient_ocean, ocean.resId)

    val bowl = AmbientPresets.zenBowl
    assertEquals("zen_bowl", bowl.id)
    assertEquals(R.raw.ambient_zen_bowl, bowl.resId)

    val wind = AmbientPresets.forestWind
    assertEquals("forest_wind", wind.id)
    assertEquals(R.raw.ambient_forest_wind, wind.resId)
  }

  @Test
  fun `verify 100 minute milestone system configuration and unlock`() {
    val milestone100 = MilestonePresets.milestone100Mins
    assertEquals(100, milestone100.targetMinutes)
    assertEquals("Century of Calm", milestone100.title)
    assertTrue(milestone100.rewardSummary.contains("100 total minutes"))

    val viewModel = BreathingViewModel()
    // Initial state (96 minutes by default)
    assertEquals(96f, viewModel.uiState.value.lifetimeTotalMinutes, 0.1f)
    assertFalse(viewModel.uiState.value.hasReached100Minutes)
    assertEquals(4, viewModel.uiState.value.minutesUntil100m)
    assertTrue(viewModel.uiState.value.milestone100mSummary.contains("4 mins to Century of Calm badge"))

    // Adding 5 minutes crosses the 100-minute threshold
    viewModel.addTestMindfulMinutes(5)
    assertTrue(viewModel.uiState.value.hasReached100Minutes)
    assertEquals(101f, viewModel.uiState.value.lifetimeTotalMinutes, 0.1f)
    assertEquals(1.0f, viewModel.uiState.value.milestone100mProgress, 0.01f)
    assertTrue(viewModel.uiState.value.milestone100mSummary.contains("Milestone Achieved!"))
  }

  @Test
  fun `verify viewModel ambient sound controls`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val viewModel = BreathingViewModel()

    assertFalse(viewModel.uiState.value.ambientSoundEnabled)
    assertEquals(AmbientPresets.rain.id, viewModel.uiState.value.selectedAmbientTrack.id)

    viewModel.toggleAmbientSound(context)
    assertTrue(viewModel.uiState.value.ambientSoundEnabled)

    viewModel.selectAmbientTrack(context, AmbientPresets.ocean)
    assertEquals("ocean", viewModel.uiState.value.selectedAmbientTrack.id)

    viewModel.setAmbientVolume(0.85f)
    assertEquals(0.85f, viewModel.uiState.value.ambientVolume, 0.01f)

    viewModel.toggleAmbientSound(context)
    assertFalse(viewModel.uiState.value.ambientSoundEnabled)
  }

  @Test
  fun `verify viewModel initial state and actions`() {
    val viewModel = BreathingViewModel()
    val state = viewModel.uiState.value

    assertEquals(BreathingPresets.boxBreathing.id, state.technique.id)
    assertEquals(SessionDuration.THREE_MIN, state.duration)
    assertFalse(state.isPlaying)
    assertEquals(1.0f, state.currentScale, 0.01f)

    viewModel.selectTechnique(BreathingPresets.relaxation478)
    assertEquals("478", viewModel.uiState.value.technique.id)

    viewModel.selectDuration(SessionDuration.FIVE_MIN)
    assertEquals(SessionDuration.FIVE_MIN, viewModel.uiState.value.duration)

    val initialDark = viewModel.uiState.value.isDarkMode
    viewModel.toggleDarkMode()
    assertEquals(!initialDark, viewModel.uiState.value.isDarkMode)
  }
}
