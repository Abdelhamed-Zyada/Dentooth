package com.example.dentooth.feature.billing.data

import com.example.dentooth.feature.billing.domain.model.Expense
import com.example.dentooth.feature.billing.domain.model.FinancialSummary
import com.example.dentooth.feature.billing.domain.model.Invoice
import com.example.dentooth.feature.billing.domain.model.InvoiceStatus
import com.example.dentooth.feature.billing.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    fun getInvoices(): Flow<List<Invoice>>
    fun getInvoicesForPatient(patientId: String): Flow<List<Invoice>>
    fun getInvoiceById(id: String): Flow<Invoice?>
    suspend fun createInvoice(invoice: Invoice): Result<Invoice>
    suspend fun updateInvoiceStatus(id: String, status: InvoiceStatus): Result<Unit>
    suspend fun recordPayment(payment: Payment): Result<Payment>
    fun getPaymentsForInvoice(invoiceId: String): Flow<List<Payment>>
    fun getAllPayments(): Flow<List<Payment>>
    fun getExpenses(): Flow<List<Expense>>
    suspend fun createExpense(expense: Expense): Result<Expense>
    suspend fun deleteExpense(id: String): Result<Unit>
    fun getFinancialSummary(): Flow<FinancialSummary>
}
