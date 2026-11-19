package com.example.dentooth.feature.imaging.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.components.DentoothEmptyState
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.feature.imaging.domain.model.ImageType
import com.example.dentooth.feature.imaging.domain.model.PatientImage

@Composable
fun PatientImagesTab(
    viewModel: ImagingViewModel,
    initialToothNumber: Int? = null,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var selectedImageForViewing by remember { mutableStateOf<PatientImage?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddImageDialog(
            initialToothNumber = initialToothNumber,
            onDismiss = { showAddDialog = false },
            onConfirm = { type, title, toothNum, notes ->
                showAddDialog = false
                viewModel.addImage(type, title, toothNum, notes)
            }
        )
    }

    if (selectedImageForViewing != null) {
        ImageViewerDialog(
            image = selectedImageForViewing!!,
            onDismiss = { selectedImageForViewing = null },
            onDelete = {
                val imgId = selectedImageForViewing!!.id
                selectedImageForViewing = null
                viewModel.deleteImage(imgId)
            }
        )
    }

    if (state.isLoading) {
        DentoothLoadingState(message = "Loading radiographs & clinical images...")
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with "+ Attach Image"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Clinical Imaging & X-Rays (${state.allImages.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Periapical, Panoramic OPG & intraoral photographs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Attach")
                }
            }

            // Image Type Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedFilter == null,
                    onClick = { viewModel.setFilter(null) },
                    label = { Text("All (${state.allImages.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                ImageType.entries.forEach { type ->
                    val count = state.allImages.count { it.type == type }
                    if (count > 0) {
                        FilterChip(
                            selected = state.selectedFilter == type,
                            onClick = { viewModel.setFilter(type) },
                            label = { Text("${type.displayName.split(" ").first()} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }

            // Images Grid or Empty State
            if (state.filteredImages.isEmpty()) {
                DentoothEmptyState(
                    title = "No Radiographs Found",
                    message = "No dental X-rays or photos have been uploaded for this patient yet.",
                    actionLabel = "Attach First X-Ray",
                    onAction = { showAddDialog = true }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.filteredImages, key = { it.id }) { image ->
                        ImageThumbnailCard(
                            image = image,
                            onClick = { selectedImageForViewing = image }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnailCard(
    image: PatientImage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Radiograph Simulated Thumbnail Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFF0F2027)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (image.type) {
                            ImageType.OPG_PANORAMIC -> "🦷 ━━━ 🦷"
                            ImageType.PERIAPICAL_XRAY -> "🦷"
                            ImageType.BITEWING_XRAY -> "🦷 🦷"
                            else -> "📷"
                        },
                        fontSize = 28.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = image.type.displayName.split(" ").first(),
                        fontSize = 10.sp,
                        color = Color(0xFF64DFDF),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (image.toothNumber != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        DentoothStatusBadge(
                            text = "#${image.toothNumber}",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Info Section
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = image.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = image.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
