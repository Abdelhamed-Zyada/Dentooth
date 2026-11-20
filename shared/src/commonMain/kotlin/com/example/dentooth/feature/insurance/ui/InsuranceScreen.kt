package com.example.dentooth.feature.insurance.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import com.example.dentooth.feature.insurance.domain.model.InsuranceClaim
import com.example.dentooth.feature.insurance.domain.model.InsuranceClaimStatus
import com.example.dentooth.feature.insurance.domain.model.InsuranceCompany

class InsuranceScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<InsuranceViewModel>()
        val state by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = "Insurance & Claims",
                    onBackClick = { navigator.pop() },
                    actions = {
                        IconButton(onClick = { viewModel.openCompaniesDirectory() }) {
                            Icon(Icons.Default.Info, contentDescription = "Insurance Providers")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openCreateClaim() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "File Insurance Claim")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.claims.isLoading) {
                    DentoothLoadingState(message = "Loading insurance claims...")
                } else {
                    val claimsList = state.claims.dataOrNull() ?: emptyList()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search bar
                        item(key = "claim-search") {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Search by patient, claim #, or insurer...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Status filter chips
                        item(key = "status-filter-chips") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = state.selectedStatusFilter == null,
                                    onClick = { viewModel.setStatusFilter(null) },
                                    label = { Text("All Claims (${state.allClaims.size})") }
                                )

                                InsuranceClaimStatus.entries.forEach { status ->
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

                        if (claimsList.isEmpty()) {
                            item(key = "empty-claims") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No insurance claims found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(claimsList, key = { it.id }) { claim ->
                                InsuranceClaimCard(
                                    claim = claim,
                                    onUpdateStatus = { newStatus, code -> viewModel.updateClaimStatus(claim.id, newStatus, code) }
                                )
                            }
                        }
                    }
                }
            }

            // Create Claim Dialog
            if (state.isCreateClaimOpen && state.companies.isNotEmpty()) {
                CreateInsuranceClaimDialog(
                    companies = state.companies,
                    onDismiss = { viewModel.closeCreateClaim() },
                    onCreate = { patientId, patientName, invNumber, company, policy, total, cov, notes ->
                        viewModel.createClaim(patientId, patientName, invNumber, company, policy, total, cov, notes)
                    }
                )
            }

            // Companies Directory Dialog
            if (state.isCompaniesDirectoryOpen) {
                InsuranceCompaniesDirectoryDialog(
                    companies = state.companies,
                    onDismiss = { viewModel.closeCompaniesDirectory() }
                )
            }
        }
    }
}

@Composable
private fun InsuranceClaimCard(
    claim: InsuranceClaim,
    onUpdateStatus: (InsuranceClaimStatus, String?) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Claim Number + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = claim.claimNumber,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Submitted: ${claim.dateSubmitted} • Ref Inv: ${claim.invoiceNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = claim.status.composeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = claim.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = claim.status.composeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Patient & Policy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = claim.patientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${claim.companyName} • Pol #${claim.policyNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Financial split: Total, Covered (80%), Copay (20%)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Claim Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("${claim.totalAmount.toInt()} EGP", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    Column {
                        Text("Insurer Share", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("${claim.coveredAmount.toInt()} EGP", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }

                    Column {
                        Text("Patient Copay", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("${claim.patientCopay.toInt()} EGP", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (!claim.approvalCode.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Approval Auth Code: ${claim.approvalCode}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            // Quick Status Action Buttons
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (claim.status) {
                    InsuranceClaimStatus.SUBMITTED -> {
                        FilledTonalButton(
                            onClick = { onUpdateStatus(InsuranceClaimStatus.UNDER_REVIEW, null) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Mark Under Review", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    InsuranceClaimStatus.UNDER_REVIEW -> {
                        FilledTonalButton(
                            onClick = { onUpdateStatus(InsuranceClaimStatus.APPROVED, "AUTH-EGY-${(100000..999999).random()}") },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Approve Claim", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    InsuranceClaimStatus.APPROVED -> {
                        FilledTonalButton(
                            onClick = { onUpdateStatus(InsuranceClaimStatus.PAID_OUT, null) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Record Disbursal", style = MaterialTheme.typography.labelSmall)
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
private fun CreateInsuranceClaimDialog(
    companies: List<InsuranceCompany>,
    onDismiss: () -> Unit,
    onCreate: (
        patientId: String,
        patientName: String,
        invoiceNumber: String,
        company: InsuranceCompany,
        policyNumber: String,
        totalAmount: Double,
        coveragePercent: Int,
        notes: String?
    ) -> Unit
) {
    var patientName by remember { mutableStateOf("Ahmed Ali") }
    var patientId by remember { mutableStateOf("1") }
    var invoiceNumber by remember { mutableStateOf("INV-2026-0001") }
    var selectedCompany by remember { mutableStateOf(companies[0]) }
    var companyExpanded by remember { mutableStateOf(false) }
    var policyNumber by remember { mutableStateOf("MET-98124-EGY") }
    var totalAmountText by remember { mutableStateOf("7125") }
    var coveragePercent by remember { mutableIntStateOf(selectedCompany.defaultCoveragePercent) }
    var notes by remember { mutableStateOf("") }

    val totalAmount = totalAmountText.toDoubleOrNull() ?: 0.0

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
                        text = "File Insurance Claim",
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

                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    label = { Text("Invoice Reference #") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Company Dropdown
                ExposedDropdownMenuBox(
                    expanded = companyExpanded,
                    onExpandedChange = { companyExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCompany.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Insurance Company") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = companyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = companyExpanded,
                        onDismissRequest = { companyExpanded = false }
                    ) {
                        companies.forEach { comp ->
                            DropdownMenuItem(
                                text = { Text("${comp.name} (${comp.defaultCoveragePercent}%)") },
                                onClick = {
                                    selectedCompany = comp
                                    coveragePercent = comp.defaultCoveragePercent
                                    companyExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = policyNumber,
                    onValueChange = { policyNumber = it },
                    label = { Text("Policy / Member Card #") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = totalAmountText,
                        onValueChange = { totalAmountText = it },
                        label = { Text("Total Bill (EGP)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = "$coveragePercent%",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Coverage") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Diagnosis / Pre-authorization Notes") },
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
                            onCreate(
                                patientId,
                                patientName,
                                invoiceNumber,
                                selectedCompany,
                                policyNumber,
                                totalAmount,
                                coveragePercent,
                                notes.ifBlank { null }
                            )
                        },
                        enabled = patientName.isNotBlank() && totalAmount > 0
                    ) {
                        Text("Submit Claim")
                    }
                }
            }
        }
    }
}

@Composable
private fun InsuranceCompaniesDirectoryDialog(
    companies: List<InsuranceCompany>,
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
                        text = "Approved Insurers Network",
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
                    items(companies, key = { it.id }) { company ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = company.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Hotline: ${company.phone} • Email: ${company.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Portal: ${company.portalUrl} • Default Coverage: ${company.defaultCoveragePercent}%",
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
