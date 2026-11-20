package com.example.dentooth.feature.automation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.automation.domain.model.AutomationChannel
import com.example.dentooth.feature.automation.domain.model.AutomationLog
import com.example.dentooth.feature.automation.domain.model.AutomationRule

class AutomationScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<AutomationViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(uiState.actionMessage) {
            uiState.actionMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.dismissMessage()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Patient Automations & Engagement",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "SMS Gateway & WhatsApp Cloud Business Engine",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header Metric Summary
                AutomationMetricsHeader(
                    activeRulesCount = uiState.allRules.count { it.isEnabled },
                    totalDispatchedToday = uiState.totalMessagesToday,
                    totalCostTodayEgp = uiState.totalCostTodayEgp
                )

                // Tabs: Rules vs Activity Logs
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.setSelectedTab(0) },
                        text = {
                            Text("Automation Rules (${uiState.allRules.size})")
                        }
                    )
                    Tab(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.setSelectedTab(1) },
                        text = {
                            Text("Dispatched Logs (${uiState.allLogs.size})")
                        }
                    )
                }

                // Search & Channel Filter Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search automations or logs...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (uiState.selectedTab == 0) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = uiState.channelFilter == null,
                                onClick = { viewModel.setChannelFilter(null) },
                                label = { Text("All Channels") }
                            )
                            AutomationChannel.entries.forEach { channel ->
                                FilterChip(
                                    selected = uiState.channelFilter == channel,
                                    onClick = {
                                        viewModel.setChannelFilter(
                                            if (uiState.channelFilter == channel) null else channel
                                        )
                                    },
                                    label = { Text("${channel.icon} ${channel.label}") }
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.clearLogs() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Clear")
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Tab Content
                when (uiState.selectedTab) {
                    0 -> RulesTabContent(
                        rulesState = uiState.rules,
                        onToggle = { id, enabled -> viewModel.toggleRule(id, enabled) },
                        onEdit = { viewModel.openEditRule(it) },
                        onTest = { viewModel.openTestRule(it) }
                    )
                    1 -> LogsTabContent(logsState = uiState.logs)
                }
            }
        }

        // Edit Rule Dialog
        uiState.editingRule?.let { rule ->
            EditRuleTemplatesDialog(
                rule = rule,
                onDismiss = { viewModel.closeEditRule() },
                onSave = { english, arabic ->
                    viewModel.saveRuleTemplates(rule.id, english, arabic)
                }
            )
        }

        // Test Trigger Dialog
        uiState.testingRule?.let { rule ->
            TestTriggerDialog(
                rule = rule,
                onDismiss = { viewModel.closeTestRule() },
                onSend = { phone, name, channel ->
                    viewModel.sendManualTest(rule.id, phone, name, channel)
                }
            )
        }
    }
}

@Composable
private fun AutomationMetricsHeader(
    activeRulesCount: Int,
    totalDispatchedToday: Int,
    totalCostTodayEgp: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            title = "Active Rules",
            value = "$activeRulesCount",
            subtitle = "Always running 24/7",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primaryContainer
        )
        MetricCard(
            title = "Dispatched Today",
            value = "$totalDispatchedToday",
            subtitle = "SMS & WhatsApp",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondaryContainer
        )
        MetricCard(
            title = "Today's Cost",
            value = "${(totalCostTodayEgp * 100).toInt() / 100.0} EGP",
            subtitle = "Gateway balance safe",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.tertiaryContainer
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun RulesTabContent(
    rulesState: UiState<List<AutomationRule>>,
    onToggle: (String, Boolean) -> Unit,
    onEdit: (AutomationRule) -> Unit,
    onTest: (AutomationRule) -> Unit
) {
    when (rulesState) {
        is UiState.Idle, is UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading automation pipeline...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${rulesState.message}", color = MaterialTheme.colorScheme.error)
            }
        }
        is UiState.Success -> {
            val rules = rulesState.data
            if (rules.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No automations matching filter", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(rules, key = { it.id }) { rule ->
                        AutomationRuleCard(
                            rule = rule,
                            onToggle = { onToggle(rule.id, it) },
                            onEdit = { onEdit(rule) },
                            onTest = { onTest(rule) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AutomationRuleCard(
    rule: AutomationRule,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rule.isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Channel icon badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (rule.channel == AutomationChannel.WHATSAPP) Color(0xFF25D366).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(rule.channel.icon, fontSize = 20.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            rule.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        "${rule.channel.label} • Sent ${rule.totalSentCount} times",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = onToggle
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                rule.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            // Bilingual preview box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .padding(10.dp)
            ) {
                Text(
                    "🇬🇧 English: \"${rule.templateEnglish}\"",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "🇪🇬 العربية: \"${rule.templateArabic}\"",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    rule.lastTriggeredAt?.let { "Last fired: $it" } ?: "Not fired yet",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.outline
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onTest,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Test Trigger")
                    }

                    Button(
                        onClick = onEdit,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Customize")
                    }
                }
            }
        }
    }
}

@Composable
private fun LogsTabContent(
    logsState: UiState<List<AutomationLog>>
) {
    when (logsState) {
        is UiState.Idle, is UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading dispatch history...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${logsState.message}", color = MaterialTheme.colorScheme.error)
            }
        }
        is UiState.Success -> {
            val logs = logsState.data
            if (logs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No automated messages sent yet", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(logs, key = { it.id }) { log ->
                        AutomationLogItem(log = log)
                    }
                }
            }
        }
    }
}

@Composable
private fun AutomationLogItem(log: AutomationLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(log.channel.icon, fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        log.patientName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        log.patientPhone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badge
                Surface(
                    color = log.status.composeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        log.status.label,
                        color = log.status.composeColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                "Trigger: ${log.ruleTitle}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    log.messageContent,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    log.timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "Cost: ${log.costEstimateEgp} EGP",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditRuleTemplatesDialog(
    rule: AutomationRule,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var english by remember(rule) { mutableStateOf(rule.templateEnglish) }
    var arabic by remember(rule) { mutableStateOf(rule.templateArabic) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Edit Templates",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    rule.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                // Merge Tags Help Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Supported Merge Variables:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "{patient_name}, {doctor_name}, {branch_name}, {time}, {amount}, {receipt_url}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text("🇬🇧 English Template", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = english,
                    onValueChange = { english = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(14.dp))

                Text("🇪🇬 Arabic Template (العربية)", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = arabic,
                    onValueChange = { arabic = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(english, arabic) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Templates")
                    }
                }
            }
        }
    }
}

@Composable
private fun TestTriggerDialog(
    rule: AutomationRule,
    onDismiss: () -> Unit,
    onSend: (String, String, AutomationChannel) -> Unit
) {
    var testPhone by remember { mutableStateOf("+201012345678") }
    var testName by remember { mutableStateOf("Dr. Test Lead") }
    var selectedChannel by remember { mutableStateOf(rule.channel) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Test Trigger Automation",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    "Simulate live delivery of \"${rule.title}\" through gateway.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                Text("Recipient Phone Number", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = testPhone,
                    onValueChange = { testPhone = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(10.dp))

                Text("Recipient Name", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = testName,
                    onValueChange = { testName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(14.dp))

                Text("Gateway Channel", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AutomationChannel.entries.filter { it != AutomationChannel.INTERNAL_ALERT }.forEach { channel ->
                        FilterChip(
                            selected = selectedChannel == channel,
                            onClick = { selectedChannel = channel },
                            label = { Text("${channel.icon} ${channel.label}") }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSend(testPhone, testName, selectedChannel) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send Test Now")
                    }
                }
            }
        }
    }
}
