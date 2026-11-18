package com.example.dentooth.feature.dentalchart.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.feature.dentalchart.domain.model.DentalChart
import com.example.dentooth.feature.dentalchart.domain.model.ToothRecord

@Composable
fun DentalArchView(
    dentalChart: DentalChart,
    selectedToothNumber: Int?,
    onToothSelected: (ToothRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val upperAdultRight = listOf(18, 17, 16, 15, 14, 13, 12, 11)
    val upperAdultLeft = listOf(21, 22, 23, 24, 25, 26, 27, 28)
    val lowerAdultRight = listOf(48, 47, 46, 45, 44, 43, 42, 41)
    val lowerAdultLeft = listOf(31, 32, 33, 34, 35, 36, 37, 38)

    val upperPediatricRight = listOf(55, 54, 53, 52, 51)
    val upperPediatricLeft = listOf(61, 62, 63, 64, 65)
    val lowerPediatricRight = listOf(85, 84, 83, 82, 81)
    val lowerPediatricLeft = listOf(71, 72, 73, 74, 75)

    val upperRight = if (dentalChart.isPediatric) upperPediatricRight else upperAdultRight
    val upperLeft = if (dentalChart.isPediatric) upperPediatricLeft else upperAdultLeft
    val lowerRight = if (dentalChart.isPediatric) lowerPediatricRight else lowerAdultRight
    val lowerLeft = if (dentalChart.isPediatric) lowerPediatricLeft else lowerAdultLeft

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Scrollable Arch Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // UPPER ARCH (MAXILLA)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Quadrant 1 / 5 (Right)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        upperRight.forEach { num ->
                            val tooth = dentalChart.getTooth(num)
                            ToothCanvas(
                                tooth = tooth,
                                isSelected = selectedToothNumber == num,
                                onClick = { onToothSelected(tooth) },
                                isUpperArch = true
                            )
                        }
                    }

                    // Midline separator
                    Spacer(modifier = Modifier.width(16.dp))

                    // Quadrant 2 / 6 (Left)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        upperLeft.forEach { num ->
                            val tooth = dentalChart.getTooth(num)
                            ToothCanvas(
                                tooth = tooth,
                                isSelected = selectedToothNumber == num,
                                onClick = { onToothSelected(tooth) },
                                isUpperArch = true
                            )
                        }
                    }
                }

                // Arch Midline & Occlusal Plane
                Row(
                    modifier = Modifier.width(if (dentalChart.isPediatric) 520.dp else 780.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = "OCCLUSAL PLANE",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // LOWER ARCH (MANDIBLE)
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Quadrant 4 / 8 (Right)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        lowerRight.forEach { num ->
                            val tooth = dentalChart.getTooth(num)
                            ToothCanvas(
                                tooth = tooth,
                                isSelected = selectedToothNumber == num,
                                onClick = { onToothSelected(tooth) },
                                isUpperArch = false
                            )
                        }
                    }

                    // Midline separator
                    Spacer(modifier = Modifier.width(16.dp))

                    // Quadrant 3 / 7 (Left)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        lowerLeft.forEach { num ->
                            val tooth = dentalChart.getTooth(num)
                            ToothCanvas(
                                tooth = tooth,
                                isSelected = selectedToothNumber == num,
                                onClick = { onToothSelected(tooth) },
                                isUpperArch = false
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clinical Legend Bar
        ChartLegend()
    }
}

@Composable
fun ChartLegend() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("Healthy", DentoothColors.ToothHealthy, borderColor = DentoothColors.ToothHealthyBorder)
            LegendItem("Caries", DentoothColors.ToothCaries)
            LegendItem("Restored", DentoothColors.ToothFilled)
            LegendItem("Crown", DentoothColors.ToothCrown)
            LegendItem("Root Canal", DentoothColors.ToothRootCanal)
            LegendItem("Missing", DentoothColors.ToothMissing, isCrossed = true)
            LegendItem("Implant", DentoothColors.ToothImplant)
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color,
    borderColor: Color? = null,
    isCrossed: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (isCrossed) {
                Text("✕", fontSize = 9.sp, color = DentoothColors.Error, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
