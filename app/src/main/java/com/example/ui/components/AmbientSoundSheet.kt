package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AmbientPresets
import com.example.model.AmbientTrack
import com.example.viewmodel.BreathingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundSheet(
  uiState: BreathingUiState,
  onToggleAmbient: () -> Unit,
  onSelectTrack: (AmbientTrack) -> Unit,
  onVolumeChange: (Float) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val infiniteTransition = rememberInfiniteTransition(label = "equalizer_pulse")
  val eqPulse by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "eq_pulse"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = Modifier.testTag("ambient_sound_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 36.dp, top = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header with Master Toggle Switch
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = if (uiState.ambientSoundEnabled) {
              MaterialTheme.colorScheme.primaryContainer
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            },
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = null,
              tint = if (uiState.ambientSoundEnabled) {
                MaterialTheme.colorScheme.onPrimaryContainer
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
              modifier = Modifier
                .padding(10.dp)
                .size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Nature Ambience",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (uiState.ambientSoundEnabled) "Looping peaceful background sounds" else "Off",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Switch(
          checked = uiState.ambientSoundEnabled,
          onCheckedChange = { onToggleAmbient() },
          colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("ambient_master_switch")
        )
      }

      // Track Selection Cards
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AmbientPresets.allTracks.forEach { track ->
          val isSelected = uiState.selectedAmbientTrack.id == track.id
          val isPlayingThis = isSelected && uiState.ambientSoundEnabled

          AmbientTrackCard(
            track = track,
            icon = getTrackIcon(track.id),
            isSelected = isSelected,
            isPlaying = isPlayingThis,
            eqPulse = eqPulse,
            onClick = { onSelectTrack(track) }
          )
        }
      }

      // Volume Control Slider (Visible when active)
      AnimatedVisibility(visible = uiState.ambientSoundEnabled) {
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Ambient Volume",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${(uiState.ambientVolume * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )

              Slider(
                value = uiState.ambientVolume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                  thumbColor = MaterialTheme.colorScheme.primary,
                  activeTrackColor = MaterialTheme.colorScheme.primary,
                  inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                  .weight(1f)
                  .testTag("ambient_volume_slider")
              )

              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // Dismiss Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("dismiss_ambient_sheet")
        ) {
          Text(
            text = "Done",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}

@Composable
private fun AmbientTrackCard(
  track: AmbientTrack,
  icon: ImageVector,
  isSelected: Boolean,
  isPlaying: Boolean,
  eqPulse: Float,
  onClick: () -> Unit
) {
  val containerColor = if (isSelected) {
    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
  } else {
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
  }

  val contentColor = if (isSelected) {
    MaterialTheme.colorScheme.onPrimaryContainer
  } else {
    MaterialTheme.colorScheme.onSurface
  }

  val border = if (isSelected) {
    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
  } else {
    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
  }

  Surface(
    color = containerColor,
    shape = RoundedCornerShape(16.dp),
    border = border,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("ambient_track_${track.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Surface(
          color = if (isSelected) {
            MaterialTheme.colorScheme.primary
          } else {
            MaterialTheme.colorScheme.surfaceVariant
          },
          shape = CircleShape,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) {
              MaterialTheme.colorScheme.onPrimary
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier
              .padding(9.dp)
              .size(20.dp)
          )
        }

        Column {
          Text(
            text = track.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = contentColor
          )
          Text(
            text = track.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor.copy(alpha = 0.75f)
          )
        }
      }

      if (isPlaying) {
        Surface(
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Playing",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}

private fun getTrackIcon(id: String): ImageVector {
  return when (id) {
    "rain" -> Icons.Default.WaterDrop
    "ocean" -> Icons.Default.Waves
    "zen_bowl" -> Icons.Default.SelfImprovement
    "forest_wind" -> Icons.Default.Air
    else -> Icons.Default.GraphicEq
  }
}
