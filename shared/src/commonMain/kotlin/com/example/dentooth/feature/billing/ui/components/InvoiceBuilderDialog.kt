package com.example.dentooth.feature.billing.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dentooth.feature.billing.domain.model.InvoiceItem

private data class EditableInvoiceItem(
    val id: String,
    var description: String,
    var toothNumberText: String,
    var quantityText: String,
    var unitPriceText: String
) {
    val quantity: Int
        get() = quantityText.toIntOrNull() ?: 1

    val unitPrice: Double
        get() = unitPriceText.toDoubleOrNull() ?: 0.0

    val totalPrice: Double
        get() = quantity * unitPrice
}

@Composable
fun InvoiceBuilderDialog(
    onDismiss: () -> Unit,
    onCreate: (
        patientId: String,
        patientName: String,
        patientPhone: String,
        doctorName: String,
        dueDate: String,
        items: List<InvoiceItem>,
        discountPercent: Double,
        notes: String?
    ) -> Unit
) {
    var patientName by remember { mutableStateOf("Ahmed Ali") }
    var patientPhone by remember { mutableStateOf("+201012345678") }
    var patientId by remember { mutableStateOf("1") }
    var doctorName by remember { mutableStateOf("Dr. Ahmed Mostafa") }
    var dueDate by remember { mutableStateOf("2026-11-30") }
    var discountPercentText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val items = remember {
        mutableStateListOf(
            EditableInvoiceItem(
                id = "item-1",
                description = "Zirconia Crown Placement",
                toothNumberText = "16",
                quantityText = "1",
                unitPriceText = "3500"
            )
        )
    }

    val subtotal = items.sumOf { it.totalPrice }
    val discountPercent = discountPercentText.toDoubleOrNull() ?: 0.0
    val discountAmount = subtotal * (discountPercent / 100.0)
    val grandTotal = (subtotal - discountAmount).coerceAtLeast(0.0)

    val canSubmit = patientName.isNotBlank() && items.isNotEmpty() && items.all { it.description.isNotBlank() && it.unitPrice > 0 }

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
                        text = "Create New Invoice",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Patient Information
                Text("Patient & Treatment Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = patientPhone,
                        onValueChange = { patientPhone = it },
                        label = { Text("Patient Phone") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Attending Doctor") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Item Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Procedures & Line Items",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = {
                            items.add(
                                EditableInvoiceItem(
                                    id = "item-${items.size + 1}",
                                    description = "",
                                    toothNumberText = "",
                                    quantityText = "1",
                                    unitPriceText = ""
                                )
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.padding(end = 4.dp))
                        Text("Add Item")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                items.forEachIndexed { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Item #${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                if (items.size > 1) {
                                    IconButton(
                                        onClick = { items.removeAt(index) },
                                        modifier = Modifier.padding(0.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = item.description,
                                onValueChange = { item.description = it; items[index] = item.copy(description = it) },
                                label = { Text("Procedure Description") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = item.toothNumberText,
                                    onValueChange = { item.toothNumberText = it; items[index] = item.copy(toothNumberText = it) },
                                    label = { Text("Tooth #") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = item.quantityText,
                                    onValueChange = { item.quantityText = it; items[index] = item.copy(quantityText = it) },
                                    label = { Text("Qty") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = item.unitPriceText,
                                    onValueChange = { item.unitPriceText = it; items[index] = item.copy(unitPriceText = it) },
                                    label = { Text("Price (EGP)") },
                                    modifier = Modifier.weight(1.5f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Discount & Totals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = discountPercentText,
                        onValueChange = { discountPercentText = it },
                        label = { Text("Discount %") },
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(2f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Grand Total Amount:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${formatNumber(grandTotal)} EGP",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Payment Terms / Notes (Optional)") },
                    minLines = 2,
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
                            val finalItems = items.map { item ->
                                InvoiceItem(
                                    id = item.id,
                                    description = item.description,
                                    toothNumber = item.toothNumberText.toIntOrNull(),
                                    quantity = item.quantity,
                                    unitPrice = item.unitPrice
                                )
                            }
                            onCreate(
                                patientId,
                                patientName,
                                patientPhone,
                                doctorName,
                                dueDate,
                                finalItems,
                                discountPercent,
                                notes.ifBlank { null }
                            )
                        },
                        enabled = canSubmit
                    ) {
                        Text("Save & Issue Invoice")
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
