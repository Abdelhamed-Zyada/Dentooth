package com.example.dentooth.feature.admin.branch.data

import com.example.dentooth.feature.admin.branch.domain.model.Branch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BranchRepositoryImpl : BranchRepository {

    private val sampleBranches = listOf(
        Branch(
            id = "branch-1",
            name = "Dentooth New Cairo (HQ)",
            code = "NC-01",
            address = "South 90th Street, Medical Park Premier, 3rd Floor",
            city = "New Cairo",
            phone = "+20228192031",
            chairCount = 5,
            operatingHours = "09:00 AM - 10:00 PM",
            isMainBranch = true,
            isActive = true
        ),
        Branch(
            id = "branch-2",
            name = "Dentooth Maadi Specialized Center",
            code = "MD-02",
            address = "Road 9, Degla Medical Building, 2nd Floor",
            city = "Maadi",
            phone = "+20223847291",
            chairCount = 3,
            operatingHours = "10:00 AM - 09:00 PM",
            isMainBranch = false,
            isActive = true
        ),
        Branch(
            id = "branch-3",
            name = "Dentooth Sheikh Zayed (West Cairo)",
            code = "SZ-03",
            address = "Arkan Plaza, Phase 2, Clinic 402",
            city = "Sheikh Zayed",
            phone = "+20238592014",
            chairCount = 4,
            operatingHours = "09:00 AM - 09:00 PM",
            isMainBranch = false,
            isActive = true
        )
    )

    private val _branches = MutableStateFlow(sampleBranches)
    private val _activeBranch = MutableStateFlow(sampleBranches[0])

    override fun getBranches(): Flow<List<Branch>> = _branches.asStateFlow()

    override fun getActiveBranch(): StateFlow<Branch> = _activeBranch.asStateFlow()

    override suspend fun setActiveBranch(branchId: String): Result<Unit> {
        val branch = _branches.value.find { it.id == branchId }
        if (branch != null) {
            _activeBranch.value = branch
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Branch not found"))
    }

    override suspend fun addBranch(branch: Branch): Result<Branch> {
        _branches.value = _branches.value + branch
        return Result.success(branch)
    }

    override suspend fun updateBranch(branch: Branch): Result<Branch> {
        val current = _branches.value
        val index = current.indexOfFirst { it.id == branch.id }
        if (index != -1) {
            val updated = current.toMutableList()
            updated[index] = branch
            _branches.value = updated
            if (_activeBranch.value.id == branch.id) {
                _activeBranch.value = branch
            }
            return Result.success(branch)
        }
        return Result.failure(IllegalArgumentException("Branch not found"))
    }
}
