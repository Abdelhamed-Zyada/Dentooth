package com.example.dentooth.feature.lab.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.feature.lab.domain.model.DentalLab
import com.example.dentooth.feature.lab.domain.model.LabOrder
import com.example.dentooth.feature.lab.domain.model.LabOrderStatus
import com.example.dentooth.feature.lab.domain.model.RestorationType

class LabScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<LabViewModel>()
        val state by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = "Dental Lab Management",
                    onBackClick = { navigator.pop() },
                    actions = {
                        IconButton(onClick = { viewModel.openLabsDirectory() }) {
                            Icon(Icons.Default.Info, contentDescription = "Partner Labs")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openCreateOrder() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Lab Order")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.orders.isLoading) {
                    DentoothLoadingState(message = "Loading lab orders...")
                } else {
                    val ordersList = state.orders.dataOrNull() ?: emptyList()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search
                        item(key = "lab-search") {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Search by patient, order #, or lab...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Status Filter Chips
                        item(key = "status-chips") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = state.selectedStatusFilter == null,
                                    onClick = { viewModel.setStatusFilter(null) },
                                    label = { Text("All Orders (${state.allOrders.size})") }
                                )

                                LabOrderStatus.entries.forEach { status ->
                                    FilterChip(
                                        selected = state.selectedStatusFilter == status,
                                        onClick = {
                                            viewModel.setStatusFilter(if (state.selectedStatusFilter == status) null else status)
                                        },
                                        label = { Text(status.label) }
                                    )
                                }
                            }
                        }

                        if (ordersList.isEmpty()) {
                            item(key = "empty-orders") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No dental lab orders found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(ordersList, key = { it.id }) { order ->
                                LabOrderCard(
                                    order = order,
                                    onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.id, newStatus) }
                                )
                            }
                        }
                    }
                }
            }

            // Create Order Dialog
            if (state.isCreateOrderOpen && state.labs.isNotEmpty()) {
                CreateLabOrderDialog(
                    labs = state.labs,
                    onDismiss = { viewModel.closeCreateOrder() },
                    onCreate = { patientId, patientName, doctor, lab, type, tooth, shade, days, cost, notes ->
                        viewModel.createOrder(patientId, patientName, doctor, lab, type, tooth, shade, days, cost, notes)
                    }
                )
            }

            // Labs Directory Dialog
            if (state.isLabsDirectoryOpen) {
                DentalLabsDirectoryDialog(
                    labs = state.labs,
                    onDismiss = { viewModel.closeLabsDirectory() }
                )
            }
        }
    }
}

@Composable
private fun LabOrderCard(
    order: LabOrder,
    onUpdateStatus: (LabOrderStatus) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Order Number + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ordered: ${order.orderDate} • Due: ${order.expectedDeliveryDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = order.status.composeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = order.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = order.status.composeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Restoration & Patient Info
            Text(
                text = order.restorationType.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Patient: ${order.patientName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    if (order.toothNumber != null) {
                        Text(
                            text = "Tooth #${order.toothNumber} • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Shade: ${order.shade}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lab: ${order.labName} • Fee: ${order.cost.toInt()} EGP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!order.notes.isNullOrBlank()) {
                Text(
                    text = "Note: ${order.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1
                )
            }

            // Quick Status Actions
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (order.status) {
                    LabOrderStatus.SENT_TO_LAB -> {
                        FilledTonalButton(
                            onClick = { onUpdateStatus(LabOrderStatus.IN_FABRICATION) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Mark in Fabrication", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    LabOrderStatus.IN_FABRICATION -> {
                        FilledTonalButton(
                            onClick = { onUpdateStatus(LabOrderStatus.DELIVERED) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Received in Clinic", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    LabOrderStatus.DELIVERED -> {
                        FilledTonalButton(
                            onClick = { onUpdateStatus(LabOrderStatus.FITTED) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Fitted on Patient", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateLabOrderDialog(
    labs: List<DentalLab>,
    onDismiss: () -> Unit,
    onCreate: (
        patientId: String,
        patientName: String,
        doctor: String,
        lab: DentalLab,
        type: RestorationType,
        tooth: Int?,
        shade: String,
        deliveryDays: Int,
        cost: Double,
        notes: String?
    ) -> Unit
) {
    var patientName by remember { mutableStateOf("Ahmed Ali") }
    var patientId by remember { mutableStateOf("1") }
    var doctorName by remember { mutableStateOf("Dr. Ahmed Mostafa") }
    var selectedLab by remember { mutableStateOf(labs[0]) }
    var labExpanded by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf(RestorationType.ZIRCONIA_CROWN) }
    var typeExpanded by remember { mutableStateOf(false) }

    var toothNumberText by remember { mutableStateOf("16") }
    var shade by remember { mutableStateOf("A2") }
    var deliveryDays by remember { mutableIntStateOf(selectedType.typicalDays) }
    var costText by remember { mutableStateOf("1400") }
    var notes by remember { mutableStateOf("") }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Dental Lab Order",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Lab Dropdown
                ExposedDropdownMenuBox(
                    expanded = labExpanded,
                    onExpandedChange = { labExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLab.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Partner Dental Lab") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = labExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = labExpanded,
                        onDismissRequest = { labExpanded = false }
                    ) {
                        labs.forEach { lab ->
                            DropdownMenuItem(
                                text = { Text("${lab.name} (~${lab.averageTurnaroundDays} days)") },
                                onClick = {
                                    selectedLab = lab
                                    labExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Restoration Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Restoration / Appliance Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        RestorationType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label) },
                                onClick = {
                                    selectedType = type
                                    deliveryDays = type.typicalDays
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = toothNumberText,
                        onValueChange = { toothNumberText = it },
                        label = { Text("Tooth #") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = shade,
                        onValueChange = { shade = it },
                        label = { Text("Shade (e.g. A2, BL1)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = deliveryDays.toString(),
                        onValueChange = { deliveryDays = it.toIntOrNull() ?: 3 },
                        label = { Text("Turnaround (Days)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        label = { Text("Estimated Fee (EGP)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Laboratory Instructions / Morphology Notes") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                            val tooth = toothNumberText.toIntOrNull()
                            val cost = costText.toDoubleOrNull() ?: 0.0
                            onCreate(
                                patientId,
                                patientName,
                                doctorName,
                                selectedLab,
                                selectedType,
                                tooth,
                                shade,
                                deliveryDays,
                                cost,
                                notes.ifBlank { null }
                            )
                        },
                        enabled = patientName.isNotBlank()
                    ) {
                        Text("Dispatch Order")
                    }
                }
            }
        }
    }
}

@Composable
private fun DentalLabsDirectoryDialog(
    labs: List<DentalLab>,
    onDismiss: () -> Unit
) {
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
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Partner Dental Laboratories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(labs, key = { it.id }) { lab ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lab.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "★ ${lab.rating}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Phone: ${lab.phone} • Email: ${lab.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "${lab.address} • Average Turnaround: ${lab.averageTurnaroundDays} days",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
