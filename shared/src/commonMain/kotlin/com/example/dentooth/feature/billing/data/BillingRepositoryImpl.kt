package com.example.dentooth.feature.billing.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.billing.domain.model.Expense
import com.example.dentooth.feature.billing.domain.model.ExpenseCategory
import com.example.dentooth.feature.billing.domain.model.FinancialSummary
import com.example.dentooth.feature.billing.domain.model.Invoice
import com.example.dentooth.feature.billing.domain.model.InvoiceItem
import com.example.dentooth.feature.billing.domain.model.InvoiceStatus
import com.example.dentooth.feature.billing.domain.model.Payment
import com.example.dentooth.feature.billing.domain.model.PaymentMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class BillingRepositoryImpl : BillingRepository {

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())

    init {
        seedInitialFinancialData()
    }

    private fun seedInitialFinancialData() {
        val todayStr = DateTimeUtil.today().toString()

        val sampleInvoices = listOf(
            Invoice(
                id = "inv-001",
                invoiceNumber = "INV-2026-0001",
                patientId = "1",
                patientName = "Ahmed Ali",
                patientPhone = "+201012345678",
                doctorName = "Dr. Ahmed Mostafa",
                issueDate = "2026-11-10",
                dueDate = "2026-11-25",
                items = listOf(
                    InvoiceItem(id = "item-1", description = "Root Canal Treatment - Molar #16", quantity = 1, unitPrice = 2800.0, toothNumber = 16, procedureCode = "D3330"),
                    InvoiceItem(id = "item-2", description = "Core Buildup & Fiber Post #16", quantity = 1, unitPrice = 1200.0, toothNumber = 16, procedureCode = "D2950"),
                    InvoiceItem(id = "item-3", description = "Zirconia Aesthetic Crown #16", quantity = 1, unitPrice = 3500.0, toothNumber = 16, procedureCode = "D2740")
                ),
                subtotal = 7500.0,
                discountPercent = 5.0,
                discountAmount = 375.0,
                taxPercent = 0.0,
                taxAmount = 0.0,
                totalAmount = 7125.0,
                paidAmount = 5000.0,
                status = InvoiceStatus.PARTIALLY_PAID,
                notes = "Deposit paid upon RCT start. Remaining 2,125 EGP upon crown cementation."
            ),
            Invoice(
                id = "inv-002",
                invoiceNumber = "INV-2026-0002",
                patientId = "2",
                patientName = "Fatma Hassan",
                patientPhone = "+201123456789",
                doctorName = "Dr. Sarah Khalil",
                issueDate = "2026-11-12",
                dueDate = "2026-11-12",
                items = listOf(
                    InvoiceItem(id = "item-4", description = "Full Mouth Ultrasonic Scaling & Polishing", quantity = 1, unitPrice = 850.0, procedureCode = "D1110"),
                    InvoiceItem(id = "item-5", description = "In-Office Laser Teeth Whitening", quantity = 1, unitPrice = 3500.0, procedureCode = "D9972")
                ),
                subtotal = 4350.0,
                discountPercent = 10.0,
                discountAmount = 435.0,
                taxPercent = 0.0,
                taxAmount = 0.0,
                totalAmount = 3915.0,
                paidAmount = 3915.0,
                status = InvoiceStatus.PAID,
                notes = "Paid in full via Visa."
            ),
            Invoice(
                id = "inv-003",
                invoiceNumber = "INV-2026-0003",
                patientId = "3",
                patientName = "Mahmoud Ibrahim",
                patientPhone = "+201234567890",
                doctorName = "Dr. Omar Tarek",
                issueDate = "2026-11-15",
                dueDate = "2026-11-30",
                items = listOf(
                    InvoiceItem(id = "item-6", description = "Titanium Dental Implant Fixture #36", quantity = 1, unitPrice = 9000.0, toothNumber = 36, procedureCode = "D6010"),
                    InvoiceItem(id = "item-7", description = "Bone Graft & Resorbable Membrane", quantity = 1, unitPrice = 3200.0, toothNumber = 36, procedureCode = "D7953")
                ),
                subtotal = 12200.0,
                discountPercent = 0.0,
                discountAmount = 0.0,
                taxPercent = 0.0,
                taxAmount = 0.0,
                totalAmount = 12200.0,
                paidAmount = 0.0,
                status = InvoiceStatus.SENT,
                notes = "Implant surgery scheduled for next week."
            ),
            Invoice(
                id = "inv-004",
                invoiceNumber = "INV-2026-0004",
                patientId = "2",
                patientName = "Fatma Hassan",
                patientPhone = "+201123456789",
                doctorName = "Dr. Sarah Khalil",
                issueDate = "2026-10-20",
                dueDate = "2026-11-05",
                items = listOf(
                    InvoiceItem(id = "item-8", description = "Comprehensive Orthodontic Consultation & Ceph Analysis", quantity = 1, unitPrice = 600.0, procedureCode = "D0150")
                ),
                subtotal = 600.0,
                discountPercent = 0.0,
                discountAmount = 0.0,
                taxPercent = 0.0,
                taxAmount = 0.0,
                totalAmount = 600.0,
                paidAmount = 0.0,
                status = InvoiceStatus.OVERDUE,
                notes = "Follow up with patient regarding payment."
            )
        )

        val samplePayments = listOf(
            Payment(
                id = "pay-001",
                invoiceId = "inv-001",
                invoiceNumber = "INV-2026-0001",
                patientId = "1",
                patientName = "Ahmed Ali",
                amount = 5000.0,
                paymentMethod = PaymentMethod.INSTAPAY,
                transactionRef = "INSTA-89410321",
                paymentDate = "2026-11-10",
                receiptNumber = "REC-2026-001",
                notes = "Advance deposit for RCT and Crown"
            ),
            Payment(
                id = "pay-002",
                invoiceId = "inv-002",
                invoiceNumber = "INV-2026-0002",
                patientId = "2",
                patientName = "Fatma Hassan",
                amount = 3915.0,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                transactionRef = "AUTH-481920",
                paymentDate = "2026-11-12",
                receiptNumber = "REC-2026-002",
                notes = "Card terminal transaction POS #1"
            )
        )

        val sampleExpenses = listOf(
            Expense(
                id = "exp-001",
                category = ExpenseCategory.MATERIALS_SUPPLIES,
                amount = 4500.0,
                description = "Composite shades, bonding agents, and disposable impression trays",
                expenseDate = "2026-11-08",
                paidTo = "Dental Supplies Egypt Co.",
                notes = "Invoice #DSE-4910"
            ),
            Expense(
                id = "exp-002",
                category = ExpenseCategory.LAB_FEES,
                amount = 2800.0,
                description = "Zirconia Crown CAD/CAM milling for patient Ahmed Ali",
                expenseDate = "2026-11-14",
                paidTo = "Elite Dental Laboratory Cairo",
                notes = "High translucency zirconia"
            ),
            Expense(
                id = "exp-003",
                category = ExpenseCategory.UTILITIES,
                amount = 1650.0,
                description = "High-speed clinic internet & South Cairo electricity bill",
                expenseDate = "2026-11-05",
                paidTo = "Telecom Egypt & Electricity Holding Co.",
                notes = "Monthly bill"
            ),
            Expense(
                id = "exp-004",
                category = ExpenseCategory.EQUIPMENT_MAINTENANCE,
                amount = 950.0,
                description = "Quarterly autoclave calibration and compressor filter replacement",
                expenseDate = "2026-11-02",
                paidTo = "MediEquip Dental Services",
                notes = "All autoclaves certified for class B sterilization"
            )
        )

        _invoices.value = sampleInvoices
        _payments.value = samplePayments
        _expenses.value = sampleExpenses
    }

    override fun getInvoices(): Flow<List<Invoice>> = _invoices.asStateFlow()

    override fun getInvoicesForPatient(patientId: String): Flow<List<Invoice>> {
        return _invoices.map { list ->
            list.filter { it.patientId == patientId }.sortedByDescending { it.issueDate }
        }
    }

    override fun getInvoiceById(id: String): Flow<Invoice?> {
        return _invoices.map { list -> list.find { it.id == id } }
    }

    override suspend fun createInvoice(invoice: Invoice): Result<Invoice> {
        _invoices.value = listOf(invoice) + _invoices.value
        return Result.success(invoice)
    }

    override suspend fun updateInvoiceStatus(id: String, status: InvoiceStatus): Result<Unit> {
        val current = _invoices.value
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = current.toMutableList()
            updated[index] = updated[index].copy(status = status)
            _invoices.value = updated
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Invoice not found"))
    }

    override suspend fun recordPayment(payment: Payment): Result<Payment> {
        _payments.value = listOf(payment) + _payments.value

        // Update the invoice paid amount & status
        val currentInvoices = _invoices.value
        val index = currentInvoices.indexOfFirst { it.id == payment.invoiceId }
        if (index != -1) {
            val inv = currentInvoices[index]
            val newPaid = inv.paidAmount + payment.amount
            val newStatus = if (newPaid >= inv.totalAmount - 0.01) {
                InvoiceStatus.PAID
            } else {
                InvoiceStatus.PARTIALLY_PAID
            }
            val updated = currentInvoices.toMutableList()
            updated[index] = inv.copy(paidAmount = newPaid, status = newStatus)
            _invoices.value = updated
        }

        return Result.success(payment)
    }

    override fun getPaymentsForInvoice(invoiceId: String): Flow<List<Payment>> {
        return _payments.map { list ->
            list.filter { it.invoiceId == invoiceId }.sortedByDescending { it.paymentDate }
        }
    }

    override fun getAllPayments(): Flow<List<Payment>> = _payments.asStateFlow()

    override fun getExpenses(): Flow<List<Expense>> = _expenses.asStateFlow()

    override suspend fun createExpense(expense: Expense): Result<Expense> {
        _expenses.value = listOf(expense) + _expenses.value
        return Result.success(expense)
    }

    override suspend fun deleteExpense(id: String): Result<Unit> {
        _expenses.value = _expenses.value.filter { it.id != id }
        return Result.success(Unit)
    }

    override fun getFinancialSummary(): Flow<FinancialSummary> {
        return combine(_invoices, _payments, _expenses) { invoices, payments, expenses ->
            val totalBilled = invoices.sumOf { it.totalAmount }
            val totalCollected = payments.sumOf { it.amount }
            val outstanding = invoices.sumOf { it.balance }
            val totalExp = expenses.sumOf { it.amount }
            val net = totalCollected - totalExp
            val rate = if (totalBilled > 0) (totalCollected / totalBilled) * 100 else 0.0

            val cash = payments.filter { it.paymentMethod == PaymentMethod.CASH }.sumOf { it.amount }
            val electronic = payments.filter {
                it.paymentMethod in listOf(PaymentMethod.CREDIT_CARD, PaymentMethod.INSTAPAY, PaymentMethod.VODAFONE_CASH, PaymentMethod.BANK_TRANSFER)
            }.sumOf { it.amount }
            val insurance = payments.filter { it.paymentMethod == PaymentMethod.INSURANCE }.sumOf { it.amount }

            FinancialSummary(
                totalBilled = totalBilled,
                totalCollected = totalCollected,
                outstandingBalance = outstanding,
                totalExpenses = totalExp,
                netIncome = net,
                collectionRatePercent = rate,
                cashCollected = cash,
                electronicCollected = electronic,
                insuranceCollected = insurance
            )
        }
    }
}
