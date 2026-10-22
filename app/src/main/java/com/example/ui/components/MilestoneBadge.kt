package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.BreathingUiState

@Composable
fun MilestoneBadge(
  uiState: BreathingUiState,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val hasReached100 = uiState.hasReached100Minutes
  val currentMinutes = uiState.lifetimeTotalMinutes.toInt()

  if (hasReached100) {
    // 🏅 100 Minutes Master Milestone Badge
    Surface(
      color = MaterialTheme.colorScheme.primaryContainer,
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary),
      shadowElevation = 2.dp,
      modifier = modifier
        .clip(RoundedCornerShape(20.dp))
        .clickable(onClick = onClick)
        .testTag("milestone_100m_badge")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = "100 Minutes Milestone Achieved",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "100m Calm",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          ),
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Surface(
          color = MaterialTheme.colorScheme.primary,
          shape = CircleShape,
          modifier = Modifier.size(6.dp)
        ) {}
      }
    }
  } else {
    // Subtle Progress toward 100m
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
      shape = RoundedCornerShape(16.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
      modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .testTag("milestone_progress_badge")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Spa,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(13.dp)
        )
        Text(
          text = "$currentMinutes/100m",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
