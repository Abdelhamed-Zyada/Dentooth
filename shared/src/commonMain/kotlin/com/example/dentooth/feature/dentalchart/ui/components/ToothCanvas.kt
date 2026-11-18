package com.example.dentooth.feature.dentalchart.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.feature.dentalchart.domain.model.ToothRecord
import com.example.dentooth.feature.dentalchart.domain.model.ToothStatus
import com.example.dentooth.feature.dentalchart.domain.model.ToothSurface

@Composable
fun ToothCanvas(
    tooth: ToothRecord,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showNumber: Boolean = true,
    isUpperArch: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (showNumber && isUpperArch) {
            Text(
                text = "${tooth.toothNumber}",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val pad = w * 0.28f

                val isMissing = tooth.status == ToothStatus.MISSING
                val isCrown = tooth.status == ToothStatus.CROWN
                val isRootCanal = tooth.status == ToothStatus.ROOT_CANAL
                val isImplant = tooth.status == ToothStatus.IMPLANT

                // 1. Center / Occlusal surface
                val occlusalPath = Path().apply {
                    moveTo(pad, pad)
                    lineTo(w - pad, pad)
                    lineTo(w - pad, h - pad)
                    lineTo(pad, h - pad)
                    close()
                }
                drawPath(
                    path = occlusalPath,
                    color = tooth.getSurfaceColor(ToothSurface.OCCLUSAL),
                    style = Fill
                )
                drawPath(
                    path = occlusalPath,
                    color = DentoothColors.ToothHealthyBorder,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 2. Buccal surface (Top trapezoid)
                val buccalPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w - pad, pad)
                    lineTo(pad, pad)
                    close()
                }
                drawPath(
                    path = buccalPath,
                    color = tooth.getSurfaceColor(ToothSurface.BUCCAL),
                    style = Fill
                )
                drawPath(
                    path = buccalPath,
                    color = DentoothColors.ToothHealthyBorder,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 3. Lingual surface (Bottom trapezoid)
                val lingualPath = Path().apply {
                    moveTo(pad, h - pad)
                    lineTo(w - pad, h - pad)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = lingualPath,
                    color = tooth.getSurfaceColor(ToothSurface.LINGUAL),
                    style = Fill
                )
                drawPath(
                    path = lingualPath,
                    color = DentoothColors.ToothHealthyBorder,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 4. Mesial surface (Left trapezoid)
                val mesialPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(pad, pad)
                    lineTo(pad, h - pad)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = mesialPath,
                    color = tooth.getSurfaceColor(ToothSurface.MESIAL),
                    style = Fill
                )
                drawPath(
                    path = mesialPath,
                    color = DentoothColors.ToothHealthyBorder,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 5. Distal surface (Right trapezoid)
                val distalPath = Path().apply {
                    moveTo(w - pad, pad)
                    lineTo(w, 0f)
                    lineTo(w, h)
                    lineTo(w - pad, h - pad)
                    close()
                }
                drawPath(
                    path = distalPath,
                    color = tooth.getSurfaceColor(ToothSurface.DISTAL),
                    style = Fill
                )
                drawPath(
                    path = distalPath,
                    color = DentoothColors.ToothHealthyBorder,
                    style = Stroke(width = 1.dp.toPx())
                )

                // Outer border
                drawRect(
                    color = if (isSelected) DentoothColors.Primary else DentoothColors.ToothHealthyBorder,
                    size = Size(w, h),
                    style = Stroke(width = if (isSelected) 2.5.dp.toPx() else 1.dp.toPx())
                )

                // Clinical Status Overlays
                if (isMissing) {
                    // Draw red diagonal cross
                    drawLine(
                        color = DentoothColors.Error,
                        start = Offset(0f, 0f),
                        end = Offset(w, h),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = DentoothColors.Error,
                        start = Offset(w, 0f),
                        end = Offset(0f, h),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                if (isCrown) {
                    // Gold border highlight
                    drawRect(
                        color = DentoothColors.ToothCrown,
                        size = Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
                        topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }

                if (isRootCanal) {
                    // Root canal obturation indicator dot in center
                    drawCircle(
                        color = DentoothColors.ToothRootCanal,
                        radius = w * 0.12f,
                        center = Offset(w / 2f, h / 2f)
                    )
                }

                if (isImplant) {
                    // Implant indicator
                    drawCircle(
                        color = DentoothColors.ToothImplant,
                        radius = w * 0.18f,
                        center = Offset(w / 2f, h / 2f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        if (showNumber && !isUpperArch) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${tooth.toothNumber}",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
