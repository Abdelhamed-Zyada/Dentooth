package com.example.dentooth.feature.insurance.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.insurance.data.InsuranceRepository
import com.example.dentooth.feature.insurance.domain.model.InsuranceClaim
import com.example.dentooth.feature.insurance.domain.model.InsuranceClaimStatus
import com.example.dentooth.feature.insurance.domain.model.InsuranceCompany
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InsuranceUiState(
    val claims: UiState<List<InsuranceClaim>> = UiState.Loading,
    val allClaims: List<InsuranceClaim> = emptyList(),
    val companies: List<InsuranceCompany> = emptyList(),
    val selectedStatusFilter: InsuranceClaimStatus? = null,
    val searchQuery: String = "",
    val isCreateClaimOpen: Boolean = false,
    val isCompaniesDirectoryOpen: Boolean = false
)

class InsuranceViewModel(
    private val insuranceRepository: InsuranceRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(InsuranceUiState())
    val uiState: StateFlow<InsuranceUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            combine(
                insuranceRepository.getClaims(),
                insuranceRepository.getCompanies()
            ) { claims, companies ->
                Pair(claims, companies)
            }.collect { (claims, companies) ->
                _uiState.update { current ->
                    current.copy(
                        allClaims = claims,
                        companies = companies,
                        claims = UiState.Success(filterClaims(claims, current.selectedStatusFilter, current.searchQuery))
                    )
                }
            }
        }
    }

    private fun filterClaims(list: List<InsuranceClaim>, status: InsuranceClaimStatus?, query: String): List<InsuranceClaim> {
        val q = query.trim().lowercase()
        return list.filter { claim ->
            val matchStatus = status == null || claim.status == status
            val matchQuery = q.isEmpty() ||
                    claim.patientName.lowercase().contains(q) ||
                    claim.claimNumber.lowercase().contains(q) ||
                    claim.companyName.lowercase().contains(q) ||
                    claim.policyNumber.lowercase().contains(q)
            matchStatus && matchQuery
        }.sortedByDescending { it.dateSubmitted }
    }

    fun setStatusFilter(status: InsuranceClaimStatus?) {
        _uiState.update { current ->
            val next = current.copy(selectedStatusFilter = status)
            next.copy(claims = UiState.Success(filterClaims(next.allClaims, next.selectedStatusFilter, next.searchQuery)))
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            val next = current.copy(searchQuery = query)
            next.copy(claims = UiState.Success(filterClaims(next.allClaims, next.selectedStatusFilter, next.searchQuery)))
        }
    }

    fun openCreateClaim() {
        _uiState.update { it.copy(isCreateClaimOpen = true) }
    }

    fun closeCreateClaim() {
        _uiState.update { it.copy(isCreateClaimOpen = false) }
    }

    fun openCompaniesDirectory() {
        _uiState.update { it.copy(isCompaniesDirectoryOpen = true) }
    }

    fun closeCompaniesDirectory() {
        _uiState.update { it.copy(isCompaniesDirectoryOpen = false) }
    }

    fun updateClaimStatus(claimId: String, status: InsuranceClaimStatus, code: String? = null) {
        screenModelScope.launch {
            insuranceRepository.updateClaimStatus(claimId, status, code)
        }
    }

    fun createClaim(
        patientId: String,
        patientName: String,
        invoiceNumber: String,
        company: InsuranceCompany,
        policyNumber: String,
        totalAmount: Double,
        coveragePercent: Int,
        notes: String?
    ) {
        screenModelScope.launch {
            val covered = totalAmount * (coveragePercent / 100.0)
            val copay = (totalAmount - covered).coerceAtLeast(0.0)
            val claimNum = "CLM-${DateTimeUtil.today().year}-${(1000..9999).random()}"

            val newClaim = InsuranceClaim(
                id = "clm-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                claimNumber = claimNum,
                patientId = patientId,
                patientName = patientName,
                invoiceNumber = invoiceNumber,
                companyId = company.id,
                companyName = company.name,
                policyNumber = policyNumber,
                totalAmount = totalAmount,
                coveredAmount = covered,
                patientCopay = copay,
                status = InsuranceClaimStatus.SUBMITTED,
                dateSubmitted = DateTimeUtil.today().toString(),
                notes = notes
            )

            insuranceRepository.createClaim(newClaim)
            closeCreateClaim()
        }
    }
}
