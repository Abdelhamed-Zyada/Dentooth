package com.example.dentooth.feature.billing.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dentooth.feature.billing.domain.model.Invoice
import com.example.dentooth.feature.billing.domain.model.PaymentMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentDialog(
    invoice: Invoice,
    onDismiss: () -> Unit,
    onRecord: (amount: Double, method: PaymentMethod, ref: String?, notes: String?) -> Unit
) {
    var amountText by remember { mutableStateOf(invoice.balance.toInt().toString()) }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var methodExpanded by remember { mutableStateOf(false) }
    var transactionRef by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val amountDouble = amountText.toDoubleOrNull() ?: 0.0
    val isValid = amountDouble > 0 && amountDouble <= invoice.balance + 0.01

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Record Payment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Invoice Summary Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${invoice.invoiceNumber} • ${invoice.patientName}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Invoice: ${formatNumber(invoice.totalAmount)} EGP",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Balance: ${formatNumber(invoice.balance)} EGP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Payment Amount (EGP)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilterChip(
                        selected = amountDouble == invoice.balance,
                        onClick = { amountText = invoice.balance.toInt().toString() },
                        label = { Text("Full Balance (${formatNumber(invoice.balance)} EGP)") }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method
                ExposedDropdownMenuBox(
                    expanded = methodExpanded,
                    onExpandedChange = { methodExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMethod.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = methodExpanded,
                        onDismissRequest = { methodExpanded = false }
                    ) {
                        PaymentMethod.entries.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.label) },
                                onClick = {
                                    selectedMethod = method
                                    methodExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Transaction Reference
                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = { transactionRef = it },
                    label = { Text("Transaction Reference / Receipt Code (Optional)") },
                    placeholder = { Text("e.g. INSTA-98124 or POS-8472") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onRecord(
                                amountDouble,
                                selectedMethod,
                                transactionRef.ifBlank { null },
                                notes.ifBlank { null }
                            )
                        },
                        enabled = isValid
                    ) {
                        Text("Confirm & Record")
                    }
                }
            }
        }
    }
}

private fun formatNumber(amount: Double): String {
    val rounded = (amount * 100).toLong() / 100.0
    val parts = rounded.toString().split(".")
    val intPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    return intPart
}
