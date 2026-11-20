package com.example.dentooth.feature.insurance.data

import com.example.dentooth.feature.insurance.domain.model.InsuranceClaim
import com.example.dentooth.feature.insurance.domain.model.InsuranceClaimStatus
import com.example.dentooth.feature.insurance.domain.model.InsuranceCompany
import com.example.dentooth.feature.insurance.domain.model.PatientInsurancePolicy
import kotlinx.coroutines.flow.Flow

interface InsuranceRepository {
    fun getCompanies(): Flow<List<InsuranceCompany>>
    fun getClaims(): Flow<List<InsuranceClaim>>
    fun getClaimsForPatient(patientId: String): Flow<List<InsuranceClaim>>
    fun getPolicyForPatient(patientId: String): Flow<PatientInsurancePolicy?>
    suspend fun createClaim(claim: InsuranceClaim): Result<InsuranceClaim>
    suspend fun updateClaimStatus(claimId: String, status: InsuranceClaimStatus, approvalCode: String? = null): Result<Unit>
    suspend fun savePolicy(policy: PatientInsurancePolicy): Result<PatientInsurancePolicy>
    suspend fun addCompany(company: InsuranceCompany): Result<InsuranceCompany>
}
