package com.example.dentooth.feature.billing.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.dentooth.feature.billing.domain.model.FinancialSummary

@Composable
fun FinancialOverviewTab(
    summary: FinancialSummary?,
    modifier: Modifier = Modifier
) {
    val totalBilled = summary?.totalBilled ?: 0.0
    val totalCollected = summary?.totalCollected ?: 0.0
    val totalExpenses = summary?.totalExpenses ?: 0.0
    val netProfit = summary?.netIncome ?: 0.0
    val rate = summary?.collectionRatePercent ?: 0.0
    val cash = summary?.cashCollected ?: 0.0
    val electronic = summary?.electronicCollected ?: 0.0
    val insurance = summary?.insuranceCollected ?: 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Net Income Card
        item(key = "net-profit-card") {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (netProfit >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Net Operating Income",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (netProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${formatNumber(netProfit)} EGP",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (netProfit >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculated as Total Collected (${formatNumber(totalCollected)} EGP) − Operating Expenses (${formatNumber(totalExpenses)} EGP)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Collection Rate Progress Card
        item(key = "collection-efficiency") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Collection Efficiency Rate",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${((rate * 10).toLong() / 10.0)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (rate / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Collected: ${formatNumber(totalCollected)} EGP",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "Outstanding: ${formatNumber(summary?.outstandingBalance ?: 0.0)} EGP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Collection Channel Split
        item(key = "channels-split") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Revenue Collection Channels",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ChannelRow(
                        label = "Cash Payments",
                        amount = cash,
                        total = totalCollected,
                        color = Color(0xFF43A047)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ChannelRow(
                        label = "Electronic (InstaPay / Visa / POS)",
                        amount = electronic,
                        total = totalCollected,
                        color = DentoothColors.Primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ChannelRow(
                        label = "Insurance Direct Billing",
                        amount = insurance,
                        total = totalCollected,
                        color = Color(0xFFFB8C00)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelRow(
    label: String,
    amount: Double,
    total: Double,
    color: Color
) {
    val percent = if (total > 0) (amount / total * 100).toInt() else 0

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "${formatNumber(amount)} EGP ($percent%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

private fun formatNumber(amount: Double): String {
    val rounded = (amount * 100).toLong() / 100.0
    val parts = rounded.toString().split(".")
    val intPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    return intPart
}
