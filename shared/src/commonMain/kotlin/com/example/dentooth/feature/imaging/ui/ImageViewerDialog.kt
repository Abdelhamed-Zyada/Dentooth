package com.example.dentooth.feature.imaging.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.feature.imaging.domain.model.ImageType
import com.example.dentooth.feature.imaging.domain.model.PatientImage

@Composable
fun ImageViewerDialog(
    image: PatientImage,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = image.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${image.type.displayName} • ${image.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Radiographic Viewport Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D1B2A)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (image.type) {
                                ImageType.OPG_PANORAMIC -> "🦷 ━━━━━━━ 🦷"
                                ImageType.PERIAPICAL_XRAY -> "🦷"
                                ImageType.BITEWING_XRAY -> "🦷 🦷 🦷"
                                else -> "📷"
                            },
                            fontSize = 36.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Digital Radiograph (${image.type.displayName})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64DFDF)
                        )
                        if (image.toothNumber != null) {
                            Text(
                                text = "Focus: Tooth #${image.toothNumber}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Badges Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DentoothStatusBadge(
                        text = image.type.displayName,
                        backgroundColor = image.type.badgeColor
                    )
                    if (image.toothNumber != null) {
                        DentoothStatusBadge(
                            text = "Tooth #${image.toothNumber}",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Clinical Observations
                if (!image.notes.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Radiological Findings:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = image.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }
        }
    )
}

@Composable
fun AddImageDialog(
    initialToothNumber: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (ImageType, String, Int?, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(ImageType.PERIAPICAL_XRAY) }
    var title by remember { mutableStateOf("") }
    var toothText by remember { mutableStateOf(initialToothNumber?.toString() ?: "") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Attach X-Ray / Clinical Photo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Image Type:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(ImageType.PERIAPICAL_XRAY, ImageType.OPG_PANORAMIC, ImageType.INTRAORAL_PHOTO).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.displayName.split(" ").first(), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                DentoothFormField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Image Title / Description",
                    placeholder = "e.g. Pre-op PA #16, Post-obturation, Smile photo",
                    isRequired = true
                )

                DentoothFormField(
                    value = toothText,
                    onValueChange = { toothText = it },
                    label = "Tooth Number (Optional)",
                    placeholder = "e.g. 16, 24 (Leave blank if full arch)",
                    keyboardType = KeyboardType.Number
                )

                DentoothFormField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Radiographic / Diagnostic Notes",
                    placeholder = "Periapical status, bone crest, canal visibility",
                    singleLine = false,
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val toothNum = toothText.toIntOrNull()
                    val actualTitle = title.trim().ifEmpty { "${selectedType.displayName} ${toothText.ifEmpty { "" }}".trim() }
                    onConfirm(selectedType, actualTitle, toothNum, notes)
                },
                enabled = title.isNotBlank() || toothText.isNotBlank()
            ) {
                Text("Attach Image", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
