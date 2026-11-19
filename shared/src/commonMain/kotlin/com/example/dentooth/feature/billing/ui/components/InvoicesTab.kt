package com.example.dentooth.feature.billing.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.dentooth.feature.billing.domain.model.Invoice
import com.example.dentooth.feature.billing.domain.model.InvoiceStatus

@Composable
fun InvoicesTab(
    invoices: List<Invoice>,
    summary: FinancialSummary?,
    selectedStatus: InvoiceStatus?,
    searchQuery: String,
    onStatusSelect: (InvoiceStatus?) -> Unit,
    onSearchChange: (String) -> Unit,
    onInvoiceClick: (Invoice) -> Unit,
    onRecordPaymentClick: (Invoice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Cards Row
        item(key = "kpi-cards") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = "Total Billed",
                    amount = summary?.totalBilled ?: 0.0,
                    color = DentoothColors.Primary,
                    modifier = Modifier.weight(1f)
                )

                KpiMetricCard(
                    title = "Collected",
                    amount = summary?.totalCollected ?: 0.0,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )

                KpiMetricCard(
                    title = "Outstanding",
                    amount = summary?.outstandingBalance ?: 0.0,
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Input
        item(key = "search-bar") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by patient, invoice #, procedure...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Status Filter Chips
        item(key = "filter-chips") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { onStatusSelect(null) },
                    label = { Text("All Invoices (${invoices.size})") }
                )

                InvoiceStatus.entries.forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusSelect(if (selectedStatus == status) null else status) },
                        label = { Text(status.label) }
                    )
                }
            }
        }

        if (invoices.isEmpty()) {
            item(key = "empty-state") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No invoices found matching criteria",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(invoices, key = { it.id }) { invoice ->
                InvoiceCard(
                    invoice = invoice,
                    onClick = { onInvoiceClick(invoice) },
                    onRecordPayment = { onRecordPaymentClick(invoice) }
                )
            }
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formatMoney(amount)} EGP",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun InvoiceCard(
    invoice: Invoice,
    onClick: () -> Unit,
    onRecordPayment: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Number + Date + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Issued: ${invoice.issueDate} • Due: ${invoice.dueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = invoice.status.composeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = invoice.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = invoice.status.composeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Patient Info & Items preview
            Text(
                text = invoice.patientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val itemsSummary = invoice.items.joinToString(", ") { it.description }
            Text(
                text = itemsSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Financial amounts & Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total: ${formatMoney(invoice.totalAmount)} EGP",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (invoice.balance > 0) {
                        Text(
                            text = "Remaining: ${formatMoney(invoice.balance)} EGP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Fully Settled",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                if (invoice.balance > 0) {
                    FilledTonalButton(
                        onClick = onRecordPayment,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Record Payment", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

private fun formatMoney(amount: Double): String {
    val rounded = (amount * 100).toLong() / 100.0
    val parts = rounded.toString().split(".")
    val intPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    return intPart
}
