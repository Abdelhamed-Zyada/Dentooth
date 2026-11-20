package com.example.dentooth.feature.automation.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.automation.data.AutomationRepository
import com.example.dentooth.feature.automation.domain.model.AutomationChannel
import com.example.dentooth.feature.automation.domain.model.AutomationLog
import com.example.dentooth.feature.automation.domain.model.AutomationRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AutomationUiState(
    val rules: UiState<List<AutomationRule>> = UiState.Loading,
    val logs: UiState<List<AutomationLog>> = UiState.Loading,
    val allRules: List<AutomationRule> = emptyList(),
    val allLogs: List<AutomationLog> = emptyList(),
    val selectedTab: Int = 0, // 0 = Automation Rules, 1 = Delivery Logs
    val channelFilter: AutomationChannel? = null,
    val searchQuery: String = "",
    val editingRule: AutomationRule? = null,
    val testingRule: AutomationRule? = null,
    val actionMessage: String? = null,
    val totalCostTodayEgp: Double = 0.0,
    val totalMessagesToday: Int = 0
)

class AutomationViewModel(
    private val automationRepository: AutomationRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            combine(
                automationRepository.getRules(),
                automationRepository.getLogs()
            ) { rules, logs ->
                Pair(rules, logs)
            }.collect { (rules, logs) ->
                val totalCost = logs.sumOf { it.costEstimateEgp }
                _uiState.update { current ->
                    current.copy(
                        allRules = rules,
                        allLogs = logs,
                        rules = UiState.Success(filterRules(rules, current.channelFilter, current.searchQuery)),
                        logs = UiState.Success(filterLogs(logs, current.searchQuery)),
                        totalCostTodayEgp = totalCost,
                        totalMessagesToday = logs.size
                    )
                }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setChannelFilter(channel: AutomationChannel?) {
        _uiState.update { current ->
            current.copy(
                channelFilter = channel,
                rules = UiState.Success(filterRules(current.allRules, channel, current.searchQuery))
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                rules = UiState.Success(filterRules(current.allRules, current.channelFilter, query)),
                logs = UiState.Success(filterLogs(current.allLogs, query))
            )
        }
    }

    fun toggleRule(ruleId: String, isEnabled: Boolean) {
        screenModelScope.launch {
            automationRepository.toggleRule(ruleId, isEnabled)
        }
    }

    fun openEditRule(rule: AutomationRule) {
        _uiState.update { it.copy(editingRule = rule) }
    }

    fun closeEditRule() {
        _uiState.update { it.copy(editingRule = null) }
    }

    fun saveRuleTemplates(ruleId: String, englishTemplate: String, arabicTemplate: String) {
        screenModelScope.launch {
            automationRepository.updateRuleTemplates(ruleId, englishTemplate, arabicTemplate)
            _uiState.update { it.copy(editingRule = null, actionMessage = "Templates updated successfully") }
        }
    }

    fun openTestRule(rule: AutomationRule) {
        _uiState.update { it.copy(testingRule = rule) }
    }

    fun closeTestRule() {
        _uiState.update { it.copy(testingRule = null) }
    }

    fun sendManualTest(ruleId: String, phone: String, name: String, channel: AutomationChannel) {
        screenModelScope.launch {
            val result = automationRepository.triggerManualTest(ruleId, phone, name, channel)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        testingRule = null,
                        actionMessage = "Test message dispatched successfully to $phone"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(actionMessage = "Failed: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    fun clearLogs() {
        screenModelScope.launch {
            automationRepository.clearLogs()
            _uiState.update { it.copy(actionMessage = "Delivery logs cleared") }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    private fun filterRules(
        rules: List<AutomationRule>,
        channel: AutomationChannel?,
        query: String
    ): List<AutomationRule> {
        return rules.filter { rule ->
            val matchesChannel = channel == null || rule.channel == channel
            val matchesQuery = query.isBlank() ||
                rule.title.contains(query, ignoreCase = true) ||
                rule.description.contains(query, ignoreCase = true)
            matchesChannel && matchesQuery
        }
    }

    private fun filterLogs(
        logs: List<AutomationLog>,
        query: String
    ): List<AutomationLog> {
        if (query.isBlank()) return logs
        return logs.filter { log ->
            log.patientName.contains(query, ignoreCase = true) ||
            log.patientPhone.contains(query, ignoreCase = true) ||
            log.ruleTitle.contains(query, ignoreCase = true) ||
            log.messageContent.contains(query, ignoreCase = true)
        }
    }
}
