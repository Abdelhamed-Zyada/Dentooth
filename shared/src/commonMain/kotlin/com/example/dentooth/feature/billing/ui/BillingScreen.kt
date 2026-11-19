package com.example.dentooth.feature.billing.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.feature.billing.ui.components.AddExpenseDialog
import com.example.dentooth.feature.billing.ui.components.ExpensesTab
import com.example.dentooth.feature.billing.ui.components.FinancialOverviewTab
import com.example.dentooth.feature.billing.ui.components.InvoiceBuilderDialog
import com.example.dentooth.feature.billing.ui.components.InvoiceDetailDialog
import com.example.dentooth.feature.billing.ui.components.InvoicesTab
import com.example.dentooth.feature.billing.ui.components.RecordPaymentDialog

class BillingScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BillingViewModel>()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.clearError()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    DentoothTopBar(
                        title = "Billing & Financials"
                    )

                    PrimaryTabRow(
                        selectedTabIndex = state.activeTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        BillingTabMode.entries.forEach { tab ->
                            Tab(
                                selected = state.activeTab == tab,
                                onClick = { viewModel.setActiveTab(tab) },
                                text = { Text(tab.title, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                when (state.activeTab) {
                    BillingTabMode.INVOICES -> {
                        FloatingActionButton(
                            onClick = { viewModel.openInvoiceBuilder() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Invoice")
                        }
                    }
                    BillingTabMode.EXPENSES -> {
                        FloatingActionButton(
                            onClick = { viewModel.openAddExpense() },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Expense")
                        }
                    }
                    BillingTabMode.OVERVIEW -> {}
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (state.activeTab) {
                    BillingTabMode.INVOICES -> {
                        if (state.invoices.isLoading) {
                            DentoothLoadingState(message = "Loading invoices...")
                        } else {
                            InvoicesTab(
                                invoices = state.invoices.dataOrNull() ?: emptyList(),
                                summary = state.summary.dataOrNull(),
                                selectedStatus = state.statusFilter,
                                searchQuery = state.searchQuery,
                                onStatusSelect = { viewModel.setStatusFilter(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onInvoiceClick = { viewModel.selectInvoiceForDetail(it) },
                                onRecordPaymentClick = { viewModel.openRecordPayment(it) }
                            )
                        }
                    }
                    BillingTabMode.EXPENSES -> {
                        if (state.expenses.isLoading) {
                            DentoothLoadingState(message = "Loading expenses...")
                        } else {
                            ExpensesTab(
                                expenses = state.expenses.dataOrNull() ?: emptyList(),
                                onDeleteExpense = { viewModel.deleteExpense(it) }
                            )
                        }
                    }
                    BillingTabMode.OVERVIEW -> {
                        if (state.summary.isLoading) {
                            DentoothLoadingState(message = "Loading financial report...")
                        } else {
                            FinancialOverviewTab(
                                summary = state.summary.dataOrNull()
                            )
                        }
                    }
                }
            }

            // Invoice Detail Dialog
            state.selectedInvoiceForDetail?.let { invoice ->
                InvoiceDetailDialog(
                    invoice = invoice,
                    payments = state.invoicePayments,
                    onDismiss = { viewModel.closeInvoiceDetail() },
                    onRecordPayment = {
                        viewModel.openRecordPayment(invoice)
                    }
                )
            }

            // Record Payment Dialog
            state.invoiceToRecordPayment?.let { invoice ->
                RecordPaymentDialog(
                    invoice = invoice,
                    onDismiss = { viewModel.closeRecordPayment() },
                    onRecord = { amount, method, ref, notes ->
                        viewModel.recordPayment(invoice, amount, method, ref, notes)
                    }
                )
            }

            // Invoice Builder Dialog
            if (state.isInvoiceBuilderOpen) {
                InvoiceBuilderDialog(
                    onDismiss = { viewModel.closeInvoiceBuilder() },
                    onCreate = { patientId, patientName, patientPhone, doctorName, dueDate, items, discount, notes ->
                        viewModel.createInvoice(
                            patientId = patientId,
                            patientName = patientName,
                            patientPhone = patientPhone,
                            doctorName = doctorName,
                            dueDate = dueDate,
                            items = items,
                            discountPercent = discount,
                            notes = notes
                        )
                    }
                )
            }

            // Add Expense Dialog
            if (state.isAddExpenseOpen) {
                AddExpenseDialog(
                    onDismiss = { viewModel.closeAddExpense() },
                    onAddExpense = { category, amount, description, paidTo, date, notes ->
                        viewModel.createExpense(
                            category = category,
                            amount = amount,
                            description = description,
                            paidTo = paidTo,
                            date = date,
                            notes = notes
                        )
                    }
                )
            }
        }
    }
}
