package com.example.dentooth.feature.billing.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.billing.data.BillingRepository
import com.example.dentooth.feature.billing.domain.model.Expense
import com.example.dentooth.feature.billing.domain.model.ExpenseCategory
import com.example.dentooth.feature.billing.domain.model.FinancialSummary
import com.example.dentooth.feature.billing.domain.model.Invoice
import com.example.dentooth.feature.billing.domain.model.InvoiceItem
import com.example.dentooth.feature.billing.domain.model.InvoiceStatus
import com.example.dentooth.feature.billing.domain.model.Payment
import com.example.dentooth.feature.billing.domain.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

enum class BillingTabMode(val title: String) {
    INVOICES("Invoices"),
    EXPENSES("Expenses"),
    OVERVIEW("Financial Summary")
}

data class BillingUiState(
    val activeTab: BillingTabMode = BillingTabMode.INVOICES,
    val invoices: UiState<List<Invoice>> = UiState.Loading,
    val allInvoices: List<Invoice> = emptyList(),
    val expenses: UiState<List<Expense>> = UiState.Loading,
    val allExpenses: List<Expense> = emptyList(),
    val summary: UiState<FinancialSummary> = UiState.Loading,
    val statusFilter: InvoiceStatus? = null,
    val searchQuery: String = "",
    val selectedInvoiceForDetail: Invoice? = null,
    val invoicePayments: List<Payment> = emptyList(),
    val invoiceToRecordPayment: Invoice? = null,
    val isInvoiceBuilderOpen: Boolean = false,
    val isAddExpenseOpen: Boolean = false,
    val errorMessage: String? = null
)

class BillingViewModel(
    private val billingRepository: BillingRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            combine(
                billingRepository.getInvoices(),
                billingRepository.getExpenses(),
                billingRepository.getFinancialSummary()
            ) { invoices, expenses, summary ->
                Triple(invoices, expenses, summary)
            }.collect { (invoices, expenses, summary) ->
                _uiState.update { current ->
                    current.copy(
                        allInvoices = invoices,
                        invoices = UiState.Success(filterInvoices(invoices, current.statusFilter, current.searchQuery)),
                        allExpenses = expenses,
                        expenses = UiState.Success(expenses),
                        summary = UiState.Success(summary)
                    )
                }
            }
        }
    }

    private fun filterInvoices(
        list: List<Invoice>,
        status: InvoiceStatus?,
        query: String
    ): List<Invoice> {
        val q = query.trim().lowercase()
        return list.filter { inv ->
            val matchStatus = status == null || inv.status == status
            val matchQuery = q.isEmpty() ||
                    inv.patientName.lowercase().contains(q) ||
                    inv.invoiceNumber.lowercase().contains(q) ||
                    inv.items.any { it.description.lowercase().contains(q) }
            matchStatus && matchQuery
        }.sortedByDescending { it.issueDate }
    }

    fun setActiveTab(tab: BillingTabMode) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setStatusFilter(status: InvoiceStatus?) {
        _uiState.update { current ->
            val next = current.copy(statusFilter = status)
            next.copy(invoices = UiState.Success(filterInvoices(next.allInvoices, next.statusFilter, next.searchQuery)))
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            val next = current.copy(searchQuery = query)
            next.copy(invoices = UiState.Success(filterInvoices(next.allInvoices, next.statusFilter, next.searchQuery)))
        }
    }

    fun selectInvoiceForDetail(invoice: Invoice) {
        _uiState.update { it.copy(selectedInvoiceForDetail = invoice) }
        screenModelScope.launch {
            billingRepository.getPaymentsForInvoice(invoice.id).collect { payments ->
                _uiState.update { it.copy(invoicePayments = payments) }
            }
        }
    }

    fun closeInvoiceDetail() {
        _uiState.update { it.copy(selectedInvoiceForDetail = null, invoicePayments = emptyList()) }
    }

    fun openRecordPayment(invoice: Invoice) {
        _uiState.update { it.copy(invoiceToRecordPayment = invoice) }
    }

    fun closeRecordPayment() {
        _uiState.update { it.copy(invoiceToRecordPayment = null) }
    }

    fun recordPayment(
        invoice: Invoice,
        amount: Double,
        method: PaymentMethod,
        ref: String?,
        notes: String?
    ) {
        screenModelScope.launch {
            val paymentId = "pay-${DateTimeUtil.currentInstant().toEpochMilliseconds()}"
            val receiptNum = "REC-${DateTimeUtil.today().year}-${(100..999).random()}"
            val payment = Payment(
                id = paymentId,
                invoiceId = invoice.id,
                invoiceNumber = invoice.invoiceNumber,
                patientId = invoice.patientId,
                patientName = invoice.patientName,
                amount = amount,
                paymentMethod = method,
                transactionRef = ref,
                paymentDate = DateTimeUtil.today().toString(),
                receiptNumber = receiptNum,
                notes = notes
            )

            billingRepository.recordPayment(payment)
            closeRecordPayment()

            // Refresh selected detail if currently open
            if (_uiState.value.selectedInvoiceForDetail?.id == invoice.id) {
                val updatedInvoice = invoice.copy(
                    paidAmount = invoice.paidAmount + amount,
                    status = if (invoice.paidAmount + amount >= invoice.totalAmount - 0.01) InvoiceStatus.PAID else InvoiceStatus.PARTIALLY_PAID
                )
                _uiState.update { it.copy(selectedInvoiceForDetail = updatedInvoice) }
            }
        }
    }

    fun openInvoiceBuilder() {
        _uiState.update { it.copy(isInvoiceBuilderOpen = true) }
    }

    fun closeInvoiceBuilder() {
        _uiState.update { it.copy(isInvoiceBuilderOpen = false) }
    }

    fun createInvoice(
        patientId: String,
        patientName: String,
        patientPhone: String,
        doctorName: String,
        dueDate: String,
        items: List<InvoiceItem>,
        discountPercent: Double,
        notes: String?
    ) {
        screenModelScope.launch {
            val subtotal = items.sumOf { it.totalPrice }
            val discountAmount = subtotal * (discountPercent / 100.0)
            val totalAmount = (subtotal - discountAmount).coerceAtLeast(0.0)

            val nextInvNumber = "INV-${DateTimeUtil.today().year}-${((_uiState.value.allInvoices.size + 1).toString().padStart(4, '0'))}"
            val newInvoice = Invoice(
                id = "inv-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                invoiceNumber = nextInvNumber,
                patientId = patientId,
                patientName = patientName,
                patientPhone = patientPhone,
                doctorName = doctorName,
                issueDate = DateTimeUtil.today().toString(),
                dueDate = dueDate,
                items = items,
                subtotal = subtotal,
                discountPercent = discountPercent,
                discountAmount = discountAmount,
                taxPercent = 0.0,
                taxAmount = 0.0,
                totalAmount = totalAmount,
                paidAmount = 0.0,
                status = InvoiceStatus.SENT,
                notes = notes
            )

            billingRepository.createInvoice(newInvoice)
            closeInvoiceBuilder()
        }
    }

    fun openAddExpense() {
        _uiState.update { it.copy(isAddExpenseOpen = true) }
    }

    fun closeAddExpense() {
        _uiState.update { it.copy(isAddExpenseOpen = false) }
    }

    fun createExpense(
        category: ExpenseCategory,
        amount: Double,
        description: String,
        paidTo: String,
        date: String,
        notes: String?
    ) {
        screenModelScope.launch {
            val expense = Expense(
                id = "exp-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                category = category,
                amount = amount,
                description = description,
                expenseDate = date,
                paidTo = paidTo,
                notes = notes
            )
            billingRepository.createExpense(expense)
            closeAddExpense()
        }
    }

    fun deleteExpense(id: String) {
        screenModelScope.launch {
            billingRepository.deleteExpense(id)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
