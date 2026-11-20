package com.example.dentooth.feature.lab.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.lab.data.LabRepository
import com.example.dentooth.feature.lab.domain.model.DentalLab
import com.example.dentooth.feature.lab.domain.model.LabOrder
import com.example.dentooth.feature.lab.domain.model.LabOrderStatus
import com.example.dentooth.feature.lab.domain.model.RestorationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class LabUiState(
    val orders: UiState<List<LabOrder>> = UiState.Loading,
    val allOrders: List<LabOrder> = emptyList(),
    val labs: List<DentalLab> = emptyList(),
    val selectedStatusFilter: LabOrderStatus? = null,
    val searchQuery: String = "",
    val isCreateOrderOpen: Boolean = false,
    val isLabsDirectoryOpen: Boolean = false,
    val selectedOrderForDetail: LabOrder? = null
)

class LabViewModel(
    private val labRepository: LabRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(LabUiState())
    val uiState: StateFlow<LabUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            combine(
                labRepository.getOrders(),
                labRepository.getLabs()
            ) { orders, labs ->
                Pair(orders, labs)
            }.collect { (orders, labs) ->
                _uiState.update { current ->
                    current.copy(
                        allOrders = orders,
                        labs = labs,
                        orders = UiState.Success(filterOrders(orders, current.selectedStatusFilter, current.searchQuery))
                    )
                }
            }
        }
    }

    private fun filterOrders(list: List<LabOrder>, status: LabOrderStatus?, query: String): List<LabOrder> {
        val q = query.trim().lowercase()
        return list.filter { order ->
            val matchStatus = status == null || order.status == status
            val matchQuery = q.isEmpty() ||
                    order.patientName.lowercase().contains(q) ||
                    order.orderNumber.lowercase().contains(q) ||
                    order.restorationType.label.lowercase().contains(q) ||
                    order.labName.lowercase().contains(q)
            matchStatus && matchQuery
        }.sortedByDescending { it.orderDate }
    }

    fun setStatusFilter(status: LabOrderStatus?) {
        _uiState.update { current ->
            val next = current.copy(selectedStatusFilter = status)
            next.copy(orders = UiState.Success(filterOrders(next.allOrders, next.selectedStatusFilter, next.searchQuery)))
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            val next = current.copy(searchQuery = query)
            next.copy(orders = UiState.Success(filterOrders(next.allOrders, next.selectedStatusFilter, next.searchQuery)))
        }
    }

    fun openCreateOrder() {
        _uiState.update { it.copy(isCreateOrderOpen = true) }
    }

    fun closeCreateOrder() {
        _uiState.update { it.copy(isCreateOrderOpen = false) }
    }

    fun openLabsDirectory() {
        _uiState.update { it.copy(isLabsDirectoryOpen = true) }
    }

    fun closeLabsDirectory() {
        _uiState.update { it.copy(isLabsDirectoryOpen = false) }
    }

    fun updateOrderStatus(orderId: String, status: LabOrderStatus) {
        screenModelScope.launch {
            labRepository.updateOrderStatus(orderId, status)
        }
    }

    fun createOrder(
        patientId: String,
        patientName: String,
        doctorName: String,
        lab: DentalLab,
        type: RestorationType,
        toothNumber: Int?,
        shade: String,
        deliveryDays: Int,
        cost: Double,
        notes: String?
    ) {
        screenModelScope.launch {
            val today = DateTimeUtil.today()
            val dueDate = LocalDate.fromEpochDays(today.toEpochDays() + deliveryDays).toString()
            val orderNum = "LAB-${today.year}-${(1000..9999).random()}"

            val newOrder = LabOrder(
                id = "order-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                orderNumber = orderNum,
                patientId = patientId,
                patientName = patientName,
                doctorName = doctorName,
                labId = lab.id,
                labName = lab.name,
                restorationType = type,
                toothNumber = toothNumber,
                shade = shade,
                orderDate = today.toString(),
                expectedDeliveryDate = dueDate,
                cost = cost,
                status = LabOrderStatus.SENT_TO_LAB,
                notes = notes
            )

            labRepository.createOrder(newOrder)
            closeCreateOrder()
        }
    }

    fun deleteOrder(orderId: String) {
        screenModelScope.launch {
            labRepository.deleteOrder(orderId)
        }
    }
}
