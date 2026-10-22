package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SessionDuration
import com.example.viewmodel.BreathingUiState

@Composable
fun SessionControls(
  uiState: BreathingUiState,
  onPlayPauseToggle: () -> Unit,
  onReset: () -> Unit,
  onDurationSelected: (SessionDuration) -> Unit,
  onHapticToggle: () -> Unit,
  onSoundToggle: () -> Unit,
  onAmbientClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Duration Selection Row
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Session Length",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Session remaining time badge
        val timeLabel = if (uiState.duration == SessionDuration.INFINITE) {
          val mins = uiState.elapsedSessionSeconds / 60
          val secs = uiState.elapsedSessionSeconds % 60
          String.format("%02d:%02d elapsed", mins, secs)
        } else {
          val rem = uiState.remainingSessionSeconds ?: uiState.duration.totalSeconds
          val mins = rem / 60
          val secs = rem % 60
          String.format("%02d:%02d left", mins, secs)
        }

        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = timeLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("session_timer_badge")
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SessionDuration.entries.forEach { dur ->
          val isSelected = uiState.duration == dur
          FilterChip(
            selected = isSelected,
            onClick = { onDurationSelected(dur) },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (dur == SessionDuration.INFINITE) {
                  Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                  text = dur.label,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
              containerColor = MaterialTheme.colorScheme.surface,
              labelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
              selectedBorderColor = MaterialTheme.colorScheme.primary,
              borderWidth = 1.dp,
              selectedBorderWidth = 1.5.dp
            ),
            modifier = Modifier
              .weight(1f)
              .height(38.dp)
              .testTag("duration_chip_${dur.name.lowercase()}")
          )
        }
      }
    }

    // 2. Ambient Sound Quick Bar
    Surface(
      color = if (uiState.ambientSoundEnabled) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      },
      shape = RoundedCornerShape(14.dp),
      border = BorderStroke(
        width = 1.dp,
        color = if (uiState.ambientSoundEnabled) {
          MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        } else {
          MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        }
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .clickable(onClick = onAmbientClick)
        .testTag("ambient_sound_bar")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null,
            tint = if (uiState.ambientSoundEnabled) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(18.dp)
          )

          Column {
            Text(
              text = if (uiState.ambientSoundEnabled) {
                "Ambient: ${uiState.selectedAmbientTrack.title}"
              } else {
                "Nature Ambience (Off)"
              },
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (uiState.ambientSoundEnabled) "Playing in loop • Tap to adjust" else "Tap to choose calming nature sounds",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = "Adjust ambient sounds",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // 3. Primary Playback & Setting Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Gentle Chime Toggle Button
      IconButton(
        onClick = onSoundToggle,
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = if (uiState.soundEnabled) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
          } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          },
          contentColor = if (uiState.soundEnabled) {
            MaterialTheme.colorScheme.onSecondaryContainer
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          }
        ),
        modifier = Modifier
          .size(48.dp)
          .testTag("sound_toggle_button")
      ) {
        Icon(
          imageVector = if (uiState.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
          contentDescription = if (uiState.soundEnabled) "Disable gentle chime" else "Enable gentle chime",
          modifier = Modifier.size(22.dp)
        )
      }

      // Reset / Stop Button
      OutlinedIconButton(
        onClick = onReset,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
          .size(48.dp)
          .testTag("reset_button")
      ) {
        Icon(
          imageVector = Icons.Default.Replay,
          contentDescription = "Reset session",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(22.dp)
        )
      }

      // Primary Large Play / Pause Button
      FilledIconButton(
        onClick = onPlayPauseToggle,
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
          .size(68.dp)
          .testTag("play_pause_button")
      ) {
        AnimatedContent(
          targetState = uiState.isPlaying,
          transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
          },
          label = "play_pause_icon"
        ) { playing ->
          Icon(
            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (playing) "Pause session" else "Start breathing session",
            modifier = Modifier.size(36.dp)
          )
        }
      }

      // Haptic Feedback Toggle Button
      IconButton(
        onClick = onHapticToggle,
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = if (uiState.hapticEnabled) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
          } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          },
          contentColor = if (uiState.hapticEnabled) {
            MaterialTheme.colorScheme.onPrimaryContainer
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          }
        ),
        modifier = Modifier
          .size(48.dp)
          .testTag("haptic_toggle_button")
      ) {
        Icon(
          imageVector = if (uiState.hapticEnabled) Icons.Default.Vibration else Icons.Outlined.Vibration,
          contentDescription = if (uiState.hapticEnabled) "Disable haptics" else "Enable haptics",
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}
