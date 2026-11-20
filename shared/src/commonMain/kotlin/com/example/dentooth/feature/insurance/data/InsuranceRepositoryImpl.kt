package com.example.dentooth.feature.insurance.data

import com.example.dentooth.feature.insurance.domain.model.InsuranceClaim
import com.example.dentooth.feature.insurance.domain.model.InsuranceClaimStatus
import com.example.dentooth.feature.insurance.domain.model.InsuranceCompany
import com.example.dentooth.feature.insurance.domain.model.PatientInsurancePolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class InsuranceRepositoryImpl : InsuranceRepository {

    private val sampleCompanies = listOf(
        InsuranceCompany(
            id = "ins-1",
            name = "MetLife Egypt Dental Network",
            phone = "+20219798",
            email = "dental.claims@metlife.eg",
            portalUrl = "https://metlife.eg/providers",
            defaultCoveragePercent = 80
        ),
        InsuranceCompany(
            id = "ins-2",
            name = "AXA OneHealth Medical Insurance",
            phone = "+20216636",
            email = "approval@axa-egypt.com",
            portalUrl = "https://axa.eg/portal",
            defaultCoveragePercent = 85
        ),
        InsuranceCompany(
            id = "ins-3",
            name = "Bupa Global Egypt",
            phone = "+20224003900",
            email = "claims@bupa.com.eg",
            portalUrl = "https://bupaglobal.com",
            defaultCoveragePercent = 90
        ),
        InsuranceCompany(
            id = "ins-4",
            name = "Allianz Egypt Health Shield",
            phone = "+20219909",
            email = "health.claims@allianz.com.eg",
            portalUrl = "https://allianz.com.eg",
            defaultCoveragePercent = 75
        )
    )

    private val samplePolicies = listOf(
        PatientInsurancePolicy(
            id = "pol-1",
            patientId = "1",
            companyId = "ins-1",
            companyName = "MetLife Egypt Dental Network",
            policyNumber = "MET-98124-EGY",
            groupNumber = "GRP-VODAFONE-EG",
            coveragePercent = 80,
            annualMaxLimit = 30000.0,
            remainingBalance = 22875.0,
            validUntil = "2026-12-31"
        )
    )

    private val sampleClaims = listOf(
        InsuranceClaim(
            id = "clm-1",
            claimNumber = "CLM-2026-0041",
            patientId = "1",
            patientName = "Ahmed Ali",
            invoiceId = "inv-001",
            invoiceNumber = "INV-2026-0001",
            companyId = "ins-1",
            companyName = "MetLife Egypt Dental Network",
            policyNumber = "MET-98124-EGY",
            totalAmount = 7125.0,
            coveredAmount = 5700.0, // 80%
            patientCopay = 1425.0,  // 20%
            status = InsuranceClaimStatus.APPROVED,
            dateSubmitted = "2026-11-12",
            approvalCode = "AUTH-MET-884210",
            notes = "Pre-authorization approved for RCT and Zirconia crown #16."
        ),
        InsuranceClaim(
            id = "clm-2",
            claimNumber = "CLM-2026-0042",
            patientId = "2",
            patientName = "Fatma Hassan",
            invoiceId = "inv-002",
            invoiceNumber = "INV-2026-0002",
            companyId = "ins-2",
            companyName = "AXA OneHealth Medical Insurance",
            policyNumber = "AXA-481920-A",
            totalAmount = 3915.0,
            coveredAmount = 3132.0,
            patientCopay = 783.0,
            status = InsuranceClaimStatus.SUBMITTED,
            dateSubmitted = "2026-11-15",
            notes = "Ultrasonic scaling and periodontal examination submitted."
        )
    )

    private val _companies = MutableStateFlow(sampleCompanies)
    private val _policies = MutableStateFlow(samplePolicies)
    private val _claims = MutableStateFlow(sampleClaims)

    override fun getCompanies(): Flow<List<InsuranceCompany>> = _companies.asStateFlow()

    override fun getClaims(): Flow<List<InsuranceClaim>> = _claims.asStateFlow()

    override fun getClaimsForPatient(patientId: String): Flow<List<InsuranceClaim>> {
        return _claims.map { list -> list.filter { it.patientId == patientId } }
    }

    override fun getPolicyForPatient(patientId: String): Flow<PatientInsurancePolicy?> {
        return _policies.map { list -> list.find { it.patientId == patientId } }
    }

    override suspend fun createClaim(claim: InsuranceClaim): Result<InsuranceClaim> {
        _claims.value = listOf(claim) + _claims.value
        return Result.success(claim)
    }

    override suspend fun updateClaimStatus(
        claimId: String,
        status: InsuranceClaimStatus,
        approvalCode: String?
    ): Result<Unit> {
        val current = _claims.value
        val index = current.indexOfFirst { it.id == claimId }
        if (index != -1) {
            val updated = current.toMutableList()
            val existing = updated[index]
            updated[index] = existing.copy(
                status = status,
                approvalCode = approvalCode ?: existing.approvalCode
            )
            _claims.value = updated
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Claim not found"))
    }

    override suspend fun savePolicy(policy: PatientInsurancePolicy): Result<PatientInsurancePolicy> {
        val current = _policies.value
        val index = current.indexOfFirst { it.patientId == policy.patientId }
        val updated = current.toMutableList()
        if (index != -1) {
            updated[index] = policy
        } else {
            updated.add(policy)
        }
        _policies.value = updated
        return Result.success(policy)
    }

    override suspend fun addCompany(company: InsuranceCompany): Result<InsuranceCompany> {
        _companies.value = _companies.value + company
        return Result.success(company)
    }
}
