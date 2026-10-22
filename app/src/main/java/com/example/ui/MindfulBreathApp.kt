package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Info
import com.example.model.MilestonePresets
import com.example.ui.components.AboutPrivacyDialog
import com.example.ui.components.AmbientSoundSheet
import com.example.ui.components.BreathingCanvas
import com.example.ui.components.SessionCompletionSheet
import com.example.ui.components.SessionControls
import com.example.ui.components.TechniqueSelector
import com.example.viewmodel.BreathingUiState
import com.example.viewmodel.BreathingViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindfulBreathApp(
  viewModel: BreathingViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  val hapticFeedback = LocalHapticFeedback.current

  var showStatsDialog by remember { mutableStateOf(false) }
  var showAmbientSheet by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }

  // Initialize offline persistent stats on startup
  LaunchedEffect(Unit) {
    viewModel.initPreferences(context)
  }

  // Haptic feedback reaction to phase transitions
  LaunchedEffect(viewModel.hapticTrigger) {
    viewModel.hapticTrigger.collectLatest {
      if (uiState.hapticEnabled) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
      }
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing),
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Mindful Breath",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Relax & Focus",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          // Mindful Statistics & Milestones Button
          IconButton(
            onClick = { showStatsDialog = true },
            modifier = Modifier
              .size(44.dp)
              .testTag("stats_button")
          ) {
            Icon(
              imageVector = Icons.Default.QueryStats,
              contentDescription = "Mindful statistics & milestones",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )
          }

          // Light / Dark Theme Toggle
          IconButton(
            onClick = { viewModel.toggleDarkMode() },
            modifier = Modifier
              .size(44.dp)
              .testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = if (uiState.isDarkMode) "Switch to light mode" else "Switch to dark mode",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.surface
  ) { innerPadding ->
    val scrollState = rememberScrollState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(scrollState),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // 1. Horizontal Breathing Technique Selector
        TechniqueSelector(
          selectedTechnique = uiState.technique,
          onTechniqueSelected = { viewModel.selectTechnique(it) },
          modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
        )

        // 2. Central Breathing Stage Animation & Countdown
        BreathingCanvas(
          uiState = uiState,
          onPlayPauseToggle = { viewModel.togglePlayPause() },
          modifier = Modifier.padding(vertical = 4.dp)
        )
      }

      // 3. Bottom Controls, Ambient Bar & Duration Selectors
      SessionControls(
        uiState = uiState,
        onPlayPauseToggle = { viewModel.togglePlayPause() },
        onReset = { viewModel.reset() },
        onDurationSelected = { viewModel.selectDuration(it) },
        onHapticToggle = { viewModel.toggleHaptic() },
        onSoundToggle = { viewModel.toggleSound() },
        onAmbientClick = { showAmbientSheet = true },
        modifier = Modifier.padding(bottom = 12.dp)
      )
    }

    // Ambient Nature Sounds Sheet
    if (showAmbientSheet) {
      AmbientSoundSheet(
        uiState = uiState,
        onToggleAmbient = { viewModel.toggleAmbientSound(context) },
        onSelectTrack = { viewModel.selectAmbientTrack(context, it) },
        onVolumeChange = { viewModel.setAmbientVolume(it) },
        onDismiss = { showAmbientSheet = false }
      )
    }

    // Completion Bottom Sheet
    if (uiState.isSessionComplete) {
      SessionCompletionSheet(
        uiState = uiState,
        onDismiss = { viewModel.dismissSessionComplete() }
      )
    }

    // Mindful Stats & Milestones Dialog
    if (showStatsDialog) {
      MindfulMilestonesDialog(
        uiState = uiState,
        onAddTestMinutes = { viewModel.addTestMindfulMinutes(5) },
        onOpenPrivacy = { showAboutDialog = true },
        onDismiss = { showStatsDialog = false }
      )
    }

    // About & Privacy Policy Dialog
    if (showAboutDialog) {
      AboutPrivacyDialog(
        onDismiss = { showAboutDialog = false }
      )
    }
  }
}

@Composable
private fun MindfulMilestonesDialog(
  uiState: BreathingUiState,
  onAddTestMinutes: () -> Unit,
  onOpenPrivacy: () -> Unit,
  onDismiss: () -> Unit
) {
  val hasReached100 = uiState.hasReached100Minutes

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.SelfImprovement,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Mindfulness & Milestones", style = MaterialTheme.typography.titleLarge)
      }
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
      ) {
        // 1. Overall Stats Card
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${uiState.todayTotalBreaths}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Total Breaths",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              modifier = Modifier
                .height(32.dp)
                .size(width = 1.dp, height = 32.dp),
              color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            ) {}

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = String.format("%.1f", uiState.lifetimeTotalMinutes),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = "Mindful Minutes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // 2. The 100-Minute Milestone Feature Card
        Card(
          colors = CardDefaults.cardColors(
            containerColor = if (hasReached100) {
              MaterialTheme.colorScheme.primaryContainer
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
          ),
          border = if (hasReached100) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
          } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          },
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier.fillMaxWidth().testTag("milestone_100m_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(text = "🏅", fontSize = 24.sp)
                Column {
                  Text(
                    text = "Century of Calm",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (hasReached100) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "100 Total Minutes Milestone",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasReached100) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Surface(
                color = if (hasReached100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = if (hasReached100) "UNLOCKED" else "${(uiState.milestone100mProgress * 100).toInt()}%",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (hasReached100) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            if (!hasReached100) {
              LinearProgressIndicator(
                progress = { uiState.milestone100mProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
              )
            }

            // Milestone Summary Text
            Text(
              text = uiState.milestone100mSummary,
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (hasReached100) FontWeight.SemiBold else FontWeight.Normal,
                lineHeight = 18.sp
              ),
              color = if (hasReached100) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.testTag("milestone_summary_text")
            )
          }
        }

        // 3. Milestone Journey Tiers
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Milestone Journey",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          MilestonePresets.allMilestones.forEach { milestone ->
            val isUnlocked = uiState.lifetimeTotalMinutes >= milestone.targetMinutes

            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isUnlocked) 0.5f else 0.25f),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(text = milestone.badgeIcon, fontSize = 16.sp)
                  Column {
                    Text(
                      text = "${milestone.title} (${milestone.targetMinutes}m)",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = milestone.description,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                if (isUnlocked) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Milestone achieved",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                } else {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Milestone locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }

        // Quick simulation / testing button to try unlocking milestone
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          OutlinedButton(
            onClick = onAddTestMinutes,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("test_add_minutes_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (hasReached100) "+5 Mins More" else "+5 Mins (Unlock Test)",
              style = MaterialTheme.typography.labelSmall
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("dismiss_stats_dialog")
      ) {
        Text("Done")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onOpenPrivacy,
        modifier = Modifier.testTag("dialog_privacy_button")
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Privacy & Policy",
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  )
}
