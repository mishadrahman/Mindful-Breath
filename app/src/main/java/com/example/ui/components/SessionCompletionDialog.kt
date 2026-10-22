package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.BreathingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionCompletionSheet(
  uiState: BreathingUiState,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = Modifier.testTag("session_completion_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 36.dp, top = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Peaceful Icon with Radial Glow
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Spa,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(36.dp)
        )
      }

      // Title & Congratulations
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Session Complete!",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "You took ${uiState.todayTotalBreaths} mindful breaths today.",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("completion_breath_count_text")
        )
      }

      // 100-Minute Milestone Highlight Badge Banner
      if (uiState.hasReached100Minutes) {
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary),
          shadowElevation = 2.dp,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("completion_milestone_badge")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              color = MaterialTheme.colorScheme.primary,
              shape = CircleShape,
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                  .padding(8.dp)
                  .size(20.dp)
              )
            }
            Column {
              Text(
                text = "Century of Calm (100 Mins)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "Milestone Reached! 100 total minutes of deep mindful presence.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
              )
            }
          }
        }
      } else {
        // Milestone Progress toward 100 minutes
        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Path to 100 Mindful Minutes",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${uiState.lifetimeTotalMinutes.toInt()} / 100 mins",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      // Session Summary Card
      Card(
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${uiState.sessionBreathsCompleted}",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Session Breaths",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            modifier = Modifier
              .height(30.dp)
              .size(width = 1.dp, height = 30.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          ) {}

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutesStr = String.format("%.1f", uiState.lifetimeTotalMinutes)
            Text(
              text = minutesStr,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
            Text(
              text = "Total Mins",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            modifier = Modifier
              .height(30.dp)
              .size(width = 1.dp, height = 30.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          ) {}

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = uiState.technique.formula,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Technique",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Inspirational mindful quote
      Text(
        text = "“Feelings come and go like clouds in a windy sky. Conscious breathing is my anchor.”",
        style = MaterialTheme.typography.bodySmall.copy(
          fontStyle = FontStyle.Italic,
          lineHeight = 18.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      // Primary Done Button
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("completion_done_button")
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = "Done",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
      }
    }
  }
}
