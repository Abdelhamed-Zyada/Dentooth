package com.example.dentooth.feature.admin.branch.data

import com.example.dentooth.feature.admin.branch.domain.model.Branch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BranchRepository {
    fun getBranches(): Flow<List<Branch>>
    fun getActiveBranch(): StateFlow<Branch>
    suspend fun setActiveBranch(branchId: String): Result<Unit>
    suspend fun addBranch(branch: Branch): Result<Branch>
    suspend fun updateBranch(branch: Branch): Result<Branch>
}
