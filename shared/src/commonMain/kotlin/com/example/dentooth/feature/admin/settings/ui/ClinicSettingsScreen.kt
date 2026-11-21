package com.example.dentooth.feature.admin.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.localization.AppLanguage
import com.example.dentooth.core.localization.LocalAppLanguage
import com.example.dentooth.core.localization.LocalizationManager
import com.example.dentooth.core.localization.appStrings
import com.example.dentooth.core.localization.ui.LanguageToggleButton
import com.example.dentooth.feature.admin.settings.data.SettingsRepository
import com.example.dentooth.feature.admin.settings.domain.model.ClinicSettings
import com.example.dentooth.feature.admin.settings.domain.model.ToothNotationSystem
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClinicSettingsScreen : Screen, KoinComponent {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val settingsRepo by inject<SettingsRepository>()
        val initialSettings by settingsRepo.getSettings().collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val s = appStrings
        val currentLang = LocalAppLanguage.current

        var clinicName by remember(initialSettings) { mutableStateOf(initialSettings.clinicName) }
        var phone by remember(initialSettings) { mutableStateOf(initialSettings.phone) }
        var email by remember(initialSettings) { mutableStateOf(initialSettings.email) }
        var address by remember(initialSettings) { mutableStateOf(initialSettings.address) }
        var taxNumber by remember(initialSettings) { mutableStateOf(initialSettings.taxRegistrationNumber) }
        var notation by remember(initialSettings) { mutableStateOf(initialSettings.toothNotation) }
        var duration by remember(initialSettings) { mutableStateOf(initialSettings.defaultAppointmentMinutes) }
        var smsEnabled by remember(initialSettings) { mutableStateOf(initialSettings.smsAutomationEnabled) }
        var whatsappEnabled by remember(initialSettings) { mutableStateOf(initialSettings.whatsappAutomationEnabled) }
        var labAutoSend by remember(initialSettings) { mutableStateOf(initialSettings.autoSendLabOnTreatment) }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                DentoothTopBar(
                    title = s.tileSettings,
                    onBackClick = { navigator.pop() },
                    actions = {
                        LanguageToggleButton()
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clinic Information
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Clinic Identity & Legal Info",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = clinicName,
                            onValueChange = { clinicName = it },
                            label = { Text("Clinic Display Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Main Phone") },
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = taxNumber,
                                onValueChange = { taxNumber = it },
                                label = { Text("Tax Registration Number") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Official Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Headquarters Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Clinical & Charting Settings
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Clinical & Dental Charting",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Tooth Notation System:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = notation == ToothNotationSystem.FDI,
                                onClick = { notation = ToothNotationSystem.FDI },
                                label = { Text("FDI (11-48)") }
                            )

                            FilterChip(
                                selected = notation == ToothNotationSystem.UNIVERSAL,
                                onClick = { notation = ToothNotationSystem.UNIVERSAL },
                                label = { Text("Universal (1-32)") }
                            )
                        }

                        Text(
                            text = notation.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Default Chair Appointment Duration:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 30, 45, 60).forEach { mins ->
                                FilterChip(
                                    selected = duration == mins,
                                    onClick = { duration = mins },
                                    label = { Text("${mins} min") }
                                )
                            }
                        }
                    }
                }

                // Automation & Patient Engagement
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Automation & Patient Messaging",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Automated SMS Reminders", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Send SMS confirmation 24h before appointment", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(checked = smsEnabled, onCheckedChange = { smsEnabled = it })
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("WhatsApp Reservation Bot", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Allow patients to confirm or reschedule via WhatsApp", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(checked = whatsappEnabled, onCheckedChange = { whatsappEnabled = it })
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Automatic Lab Work Order", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Auto-dispatch prosthetics to dental lab once procedure is planned", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(checked = labAutoSend, onCheckedChange = { labAutoSend = it })
                        }
                    }
                }

                // Application Language & Localization Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = s.languageSetting,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = currentLang == AppLanguage.ENGLISH,
                                onClick = { LocalizationManager.setLanguage(AppLanguage.ENGLISH) },
                                label = { Text(s.languageEnglish) }
                            )

                            FilterChip(
                                selected = currentLang == AppLanguage.ARABIC,
                                onClick = { LocalizationManager.setLanguage(AppLanguage.ARABIC) },
                                label = { Text(s.languageArabic) }
                            )
                        }
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        scope.launch {
                            val updated = initialSettings.copy(
                                clinicName = clinicName,
                                phone = phone,
                                email = email,
                                address = address,
                                taxRegistrationNumber = taxNumber,
                                toothNotation = notation,
                                defaultAppointmentMinutes = duration,
                                smsAutomationEnabled = smsEnabled,
                                whatsappAutomationEnabled = whatsappEnabled,
                                autoSendLabOnTreatment = labAutoSend
                            )
                            settingsRepo.updateSettings(updated)
                            snackbarHostState.showSnackbar(s.success)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = s.save, modifier = Modifier.padding(end = 6.dp))
                    Text(s.save)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
