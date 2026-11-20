package com.example.dentooth.feature.automation.data

import com.example.dentooth.feature.automation.domain.model.AutomationChannel
import com.example.dentooth.feature.automation.domain.model.AutomationLog
import com.example.dentooth.feature.automation.domain.model.AutomationRule
import kotlinx.coroutines.flow.Flow

interface AutomationRepository {
    fun getRules(): Flow<List<AutomationRule>>
    fun getLogs(): Flow<List<AutomationLog>>
    suspend fun toggleRule(ruleId: String, isEnabled: Boolean): Result<Unit>
    suspend fun updateRuleTemplates(ruleId: String, englishTemplate: String, arabicTemplate: String): Result<Unit>
    suspend fun triggerManualTest(ruleId: String, patientPhone: String, patientName: String, channel: AutomationChannel): Result<AutomationLog>
    suspend fun clearLogs(): Result<Unit>
}
