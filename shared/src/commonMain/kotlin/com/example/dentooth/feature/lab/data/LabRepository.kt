package com.example.dentooth.feature.lab.data

import com.example.dentooth.feature.lab.domain.model.DentalLab
import com.example.dentooth.feature.lab.domain.model.LabOrder
import com.example.dentooth.feature.lab.domain.model.LabOrderStatus
import kotlinx.coroutines.flow.Flow

interface LabRepository {
    fun getLabs(): Flow<List<DentalLab>>
    fun getOrders(): Flow<List<LabOrder>>
    fun getOrdersForPatient(patientId: String): Flow<List<LabOrder>>
    suspend fun createOrder(order: LabOrder): Result<LabOrder>
    suspend fun updateOrderStatus(orderId: String, status: LabOrderStatus): Result<Unit>
    suspend fun addLab(lab: DentalLab): Result<DentalLab>
    suspend fun deleteOrder(orderId: String): Result<Unit>
}
