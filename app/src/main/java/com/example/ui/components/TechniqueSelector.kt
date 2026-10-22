package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BreathingPresets
import com.example.model.BreathingTechnique

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniqueSelector(
  selectedTechnique: BreathingTechnique,
  onTechniqueSelected: (BreathingTechnique) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCatalogSheet by remember { mutableStateOf(false) }
  var techniqueInfoDialog by remember { mutableStateOf<BreathingTechnique?>(null) }
  val scrollState = rememberScrollState()

  Column(modifier = modifier.fillMaxWidth()) {
    // Header Bar: Section Title + "All Patterns" Catalog Button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.LocalHospital,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "Medical Patterns",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // "Browse All" button
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { showCatalogSheet = true }
          .testTag("browse_patterns_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
            contentDescription = "Browse all breathing techniques",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Browse All (${BreathingPresets.allTechniques.size})",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    // Horizontal Scrolling Technique Selector Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      BreathingPresets.allTechniques.forEach { technique ->
        val isSelected = technique.id == selectedTechnique.id
        val icon = getTechniqueIcon(technique.id)

        TechniqueChip(
          technique = technique,
          icon = icon,
          isSelected = isSelected,
          onClick = { onTechniqueSelected(technique) },
          onInfoClick = { techniqueInfoDialog = technique },
          modifier = Modifier.testTag("technique_chip_${technique.id}")
        )
      }
    }
  }

  // Comprehensive Medical Breathing Patterns Catalog Sheet
  if (showCatalogSheet) {
    MedicalBreathingCatalogSheet(
      currentTechnique = selectedTechnique,
      onSelect = {
        onTechniqueSelected(it)
        showCatalogSheet = false
      },
      onDismiss = { showCatalogSheet = false }
    )
  }

  // Quick Info Dialog for a specific technique
  techniqueInfoDialog?.let { tech ->
    AlertDialog(
      onDismissRequest = { techniqueInfoDialog = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = getTechniqueIcon(tech.id),
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(text = tech.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
              text = tech.formula,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Cycle: ${tech.steps.joinToString(" → ") { "${it.durationSeconds}s ${it.label}" }}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }

          Text(
            text = tech.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )

          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Clinical Origin: ${tech.medicalOrigin}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.secondary
            )
            Text(
              text = "Primary Benefits: ${tech.benefits}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            onTechniqueSelected(tech)
            techniqueInfoDialog = null
          },
          modifier = Modifier.testTag("apply_technique_dialog_button")
        ) {
          Text("Select Pattern")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { techniqueInfoDialog = null },
          modifier = Modifier.testTag("dismiss_info_dialog")
        ) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
private fun TechniqueChip(
  technique: BreathingTechnique,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  onInfoClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val containerColor = if (isSelected) {
    MaterialTheme.colorScheme.primaryContainer
  } else {
    MaterialTheme.colorScheme.surface
  }

  val contentColor = if (isSelected) {
    MaterialTheme.colorScheme.onPrimaryContainer
  } else {
    MaterialTheme.colorScheme.onSurface
  }

  val border = if (isSelected) {
    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
  } else {
    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
  }

  Surface(
    color = containerColor,
    shape = RoundedCornerShape(16.dp),
    border = border,
    shadowElevation = if (isSelected) 2.dp else 0.dp,
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = technique.name,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = technique.formula,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
        }

        Text(
          text = technique.category,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = contentColor.copy(alpha = 0.75f)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicalBreathingCatalogSheet(
  currentTechnique: BreathingTechnique,
  onSelect: (BreathingTechnique) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = Modifier.testTag("medical_catalog_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp, top = 4.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = Icons.Default.LocalHospital,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Medically Recognized Patterns",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Text(
            text = "Select an evidence-based pattern to guide autonomic regulation",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Catalog List of Techniques
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BreathingPresets.allTechniques.forEach { technique ->
          val isSelected = technique.id == currentTechnique.id

          Card(
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
              } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
              }
            ),
            border = if (isSelected) {
              BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            } else {
              BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            },
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .clickable { onSelect(technique) }
              .testTag("catalog_card_${technique.id}")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Top line: Icon, Name, Formula, Selected Badge
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                  ) {
                    Icon(
                      imageVector = getTechniqueIcon(technique.id),
                      contentDescription = null,
                      tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                    )
                  }

                  Column {
                    Text(
                      text = technique.name,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = technique.category,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                // Formula tag & selection badge
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = technique.formula,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }

                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Currently active",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }

              // Timing sequence
              Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Timing: ${technique.steps.joinToString("  •  ") { "${it.label} ${it.durationSeconds}s" }}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              // Description
              Text(
                text = technique.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )

              // Clinical Origin & Target
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Origin: ${technique.medicalOrigin}",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.primary
                )

                Button(
                  onClick = { onSelect(technique) },
                  colors = if (isSelected) {
                    ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primary,
                      contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                  } else {
                    ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.surfaceVariant,
                      contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.height(34.dp)
                ) {
                  Text(
                    text = if (isSelected) "Active" else "Select",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

private fun getTechniqueIcon(id: String): ImageVector {
  return when (id) {
    "box" -> Icons.Default.Spa
    "478" -> Icons.Default.NightsStay
    "equal" -> Icons.Default.SelfImprovement
    "coherent" -> Icons.Default.Favorite
    "sigh" -> Icons.Default.Air
    "calm711" -> Icons.Default.Shield
    "vitality" -> Icons.Default.Bolt
    else -> Icons.Default.Spa
  }
}
