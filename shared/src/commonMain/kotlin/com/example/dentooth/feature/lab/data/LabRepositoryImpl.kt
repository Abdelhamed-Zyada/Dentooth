package com.example.dentooth.feature.lab.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.lab.domain.model.DentalLab
import com.example.dentooth.feature.lab.domain.model.LabOrder
import com.example.dentooth.feature.lab.domain.model.LabOrderStatus
import com.example.dentooth.feature.lab.domain.model.RestorationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class LabRepositoryImpl : LabRepository {

    private val sampleLabs = listOf(
        DentalLab(
            id = "lab-1",
            name = "Elite Dental CAD/CAM Center",
            phone = "+201091827364",
            email = "orders@elitedentallab.eg",
            address = "Nasr City, Makram Ebeid, Cairo",
            averageTurnaroundDays = 3,
            rating = 4.9
        ),
        DentalLab(
            id = "lab-2",
            name = "BioDent Aesthetic Ceramics",
            phone = "+201128374659",
            email = "info@biodentegypt.com",
            address = "Dokki, Mesaha Square, Giza",
            averageTurnaroundDays = 4,
            rating = 4.8
        ),
        DentalLab(
            id = "lab-3",
            name = "Master Ortho & Appliance Lab",
            phone = "+201209384756",
            email = "lab@masterortho.com",
            address = "Heliopolis, Korba, Cairo",
            averageTurnaroundDays = 2,
            rating = 4.7
        )
    )

    private val _labs = MutableStateFlow(sampleLabs)
    private val _orders = MutableStateFlow<List<LabOrder>>(emptyList())

    init {
        seedInitialOrders()
    }

    private fun seedInitialOrders() {
        val sampleOrders = listOf(
            LabOrder(
                id = "order-1",
                orderNumber = "LAB-2026-0081",
                patientId = "1",
                patientName = "Ahmed Ali",
                doctorName = "Dr. Ahmed Mostafa",
                labId = "lab-1",
                labName = "Elite Dental CAD/CAM Center",
                restorationType = RestorationType.ZIRCONIA_CROWN,
                toothNumber = 16,
                shade = "A2",
                orderDate = "2026-11-14",
                expectedDeliveryDate = "2026-11-18",
                actualDeliveryDate = "2026-11-18",
                cost = 1400.0,
                status = LabOrderStatus.DELIVERED,
                notes = "High translucency multilayer zirconia crown for tooth #16.",
                trackingReference = "ELT-91824"
            ),
            LabOrder(
                id = "order-2",
                orderNumber = "LAB-2026-0082",
                patientId = "3",
                patientName = "Mahmoud Ibrahim",
                doctorName = "Dr. Omar Tarek",
                labId = "lab-1",
                labName = "Elite Dental CAD/CAM Center",
                restorationType = RestorationType.IMPLANT_CUSTOM_ABUTMENT,
                toothNumber = 36,
                shade = "A3",
                orderDate = "2026-11-16",
                expectedDeliveryDate = "2026-11-22",
                cost = 2200.0,
                status = LabOrderStatus.IN_FABRICATION,
                notes = "Screw-retained zirconia crown on customized titanium base.",
                trackingReference = "ELT-91902"
            ),
            LabOrder(
                id = "order-3",
                orderNumber = "LAB-2026-0083",
                patientId = "2",
                patientName = "Fatma Hassan",
                doctorName = "Dr. Sarah Khalil",
                labId = "lab-3",
                labName = "Master Ortho & Appliance Lab",
                restorationType = RestorationType.CLEAR_ALIGNER_TRAY,
                toothNumber = null,
                shade = "Clear",
                orderDate = "2026-11-18",
                expectedDeliveryDate = "2026-11-21",
                cost = 650.0,
                status = LabOrderStatus.SENT_TO_LAB,
                notes = "Upper & lower Hawley retainer with labial bow.",
                trackingReference = "MST-4019"
            )
        )

        _orders.value = sampleOrders
    }

    override fun getLabs(): Flow<List<DentalLab>> = _labs.asStateFlow()

    override fun getOrders(): Flow<List<LabOrder>> = _orders.asStateFlow()

    override fun getOrdersForPatient(patientId: String): Flow<List<LabOrder>> {
        return _orders.map { list -> list.filter { it.patientId == patientId } }
    }

    override suspend fun createOrder(order: LabOrder): Result<LabOrder> {
        _orders.value = listOf(order) + _orders.value
        return Result.success(order)
    }

    override suspend fun updateOrderStatus(orderId: String, status: LabOrderStatus): Result<Unit> {
        val current = _orders.value
        val index = current.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val updated = current.toMutableList()
            val existing = updated[index]
            val actualDate = if (status == LabOrderStatus.DELIVERED && existing.actualDeliveryDate == null) {
                DateTimeUtil.today().toString()
            } else existing.actualDeliveryDate

            updated[index] = existing.copy(status = status, actualDeliveryDate = actualDate)
            _orders.value = updated
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Lab order not found"))
    }

    override suspend fun addLab(lab: DentalLab): Result<DentalLab> {
        _labs.value = _labs.value + lab
        return Result.success(lab)
    }

    override suspend fun deleteOrder(orderId: String): Result<Unit> {
        _orders.value = _orders.value.filter { it.id != orderId }
        return Result.success(Unit)
    }
}
