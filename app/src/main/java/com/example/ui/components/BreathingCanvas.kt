package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BreathingPhaseType
import com.example.ui.theme.OrbSageGlow
import com.example.ui.theme.OrbTealGlow
import com.example.viewmodel.BreathingUiState

@Composable
fun BreathingCanvas(
  uiState: BreathingUiState,
  onPlayPauseToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = uiState.isDarkMode
  val currentStep = uiState.currentStep
  val isPlaying = uiState.isPlaying

  // Ambient pulsing wave animation for outer aura
  val infiniteTransition = rememberInfiniteTransition(label = "ambient_pulse")
  val ambientAuraPulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "aura_pulse"
  )

  // Primary colors based on current phase and theme
  val primaryPhaseColor = when (currentStep.type) {
    BreathingPhaseType.INHALE -> OrbSageGlow
    BreathingPhaseType.HOLD_IN -> OrbTealGlow
    BreathingPhaseType.EXHALE -> MaterialTheme.colorScheme.primary
    BreathingPhaseType.HOLD_OUT -> MaterialTheme.colorScheme.secondary
  }

  val coreBgColor = if (isDark) {
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
  } else {
    MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
  }

  val orbitColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(310.dp)
      .testTag("breathing_canvas_container"),
    contentAlignment = Alignment.Center
  ) {
    // 1. Background Ambient Glow & Concentric Ripple Circles
    Canvas(
      modifier = Modifier
        .size(300.dp)
        .testTag("breathing_canvas_ambient")
    ) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val maxRadius = size.minDimension / 2f

      // Outer soft ring 1
      drawCircle(
        color = primaryPhaseColor.copy(alpha = if (isPlaying) 0.08f * ambientAuraPulse else 0.05f),
        radius = maxRadius * 0.98f * ambientAuraPulse,
        center = center
      )

      // Outer soft ring 2
      drawCircle(
        color = primaryPhaseColor.copy(alpha = if (isPlaying) 0.14f * ambientAuraPulse else 0.09f),
        radius = maxRadius * 0.82f * ambientAuraPulse,
        center = center
      )

      // Delicate dashed/concentric guide orbit
      drawCircle(
        color = orbitColor,
        radius = maxRadius * 0.68f,
        center = center,
        style = Stroke(
          width = 1.5.dp.toPx(),
          cap = StrokeCap.Round
        )
      )
    }

    // 2. Central Pulsating Breathing Circle (Scaling 1.0x to 1.6x)
    // Entire orb is directly clickable to toggle Start/Pause
    Box(
      modifier = Modifier
        .size(175.dp)
        .scale(uiState.currentScale)
        .shadow(
          elevation = if (isPlaying) 16.dp else 8.dp,
          shape = CircleShape,
          spotColor = primaryPhaseColor.copy(alpha = 0.5f),
          ambientColor = primaryPhaseColor.copy(alpha = 0.25f)
        )
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              coreBgColor,
              primaryPhaseColor.copy(alpha = if (isDark) 0.25f else 0.15f),
              coreBgColor
            )
          )
        )
        .clickable(onClick = onPlayPauseToggle)
        .testTag("breathing_animated_orb"),
      contentAlignment = Alignment.Center
    ) {
      // Progress Arc around the central circle edge
      Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 4.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

        // Track
        drawCircle(
          color = primaryPhaseColor.copy(alpha = 0.15f),
          radius = diameter / 2f,
          center = center,
          style = Stroke(width = strokeWidth)
        )

        // Active sweep arc based on phase progress
        val sweepAngle = uiState.stepProgress * 360f
        drawArc(
          brush = Brush.sweepGradient(
            colors = listOf(
              primaryPhaseColor.copy(alpha = 0.4f),
              primaryPhaseColor
            )
          ),
          startAngle = -90f,
          sweepAngle = sweepAngle,
          useCenter = false,
          topLeft = topLeft,
          size = androidx.compose.ui.geometry.Size(diameter, diameter),
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }
    }

    // 3. Central Stage Content (Text stays crisp and non-distorted, also clickable)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .size(170.dp)
        .clip(CircleShape)
        .clickable(onClick = onPlayPauseToggle)
        .padding(10.dp)
    ) {
      // Current Action Text in bold (e.g. "Inhale", "Hold", "Exhale")
      Text(
        text = if (isPlaying) currentStep.label else "Ready",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        ),
        color = if (isPlaying) primaryPhaseColor else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("breathing_action_label")
      )

      Spacer(modifier = Modifier.height(2.dp))

      // Large Countdown Number or Play indicator
      if (isPlaying) {
        Text(
          text = "${uiState.secondsRemainingInStep}",
          style = MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 42.sp,
            letterSpacing = (-1).sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("breathing_countdown_text")
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Start breathing",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${currentStep.durationSeconds}s",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Total cycles completed counter or Start prompt
      val cycleText = if (uiState.targetCycles != null) {
        "Cycle ${uiState.completedCycles + 1} of ${uiState.targetCycles}"
      } else {
        "Cycle ${uiState.completedCycles + 1}"
      }

      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        shape = CircleShape,
        modifier = Modifier.padding(top = 4.dp)
      ) {
        Text(
          text = if (isPlaying) cycleText else "Tap Orb to Begin",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          ),
          color = if (isPlaying) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag("breathing_cycle_counter")
        )
      }
    }
  }

  // Guidance phrase below canvas
  Text(
    text = if (isPlaying) currentStep.guidance else "Find a comfortable posture and breathe naturally.",
    style = MaterialTheme.typography.bodyMedium.copy(
      fontWeight = FontWeight.Normal,
      lineHeight = 18.sp
    ),
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign = TextAlign.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 2.dp)
      .testTag("breathing_guidance_text")
  )
}
