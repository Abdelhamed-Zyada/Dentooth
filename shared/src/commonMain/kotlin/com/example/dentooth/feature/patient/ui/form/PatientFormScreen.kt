package com.example.dentooth.feature.patient.ui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.feature.patient.domain.model.Gender
import org.koin.core.parameter.parametersOf

data class PatientFormScreen(val patientId: String? = null) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<PatientFormViewModel> { parametersOf(patientId) }
        val formState by viewModel.formState.collectAsState()

        LaunchedEffect(formState.saveState) {
            if (formState.saveState.isSuccess) {
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = if (patientId == null) "Register Patient" else "Edit Patient",
                    onBackClick = { navigator.pop() },
                    actions = {
                        TextButton(
                            onClick = { viewModel.save() },
                            enabled = !formState.saveState.isLoading
                        ) {
                            if (formState.saveState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    text = "Save",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // First & Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DentoothFormField(
                        value = formState.firstName,
                        onValueChange = { viewModel.onFirstNameChange(it) },
                        label = "First Name",
                        isRequired = true,
                        error = formState.firstNameError,
                        modifier = Modifier.weight(1f)
                    )
                    DentoothFormField(
                        value = formState.lastName,
                        onValueChange = { viewModel.onLastNameChange(it) },
                        label = "Last Name",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Phone
                DentoothFormField(
                    value = formState.phone,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    label = "Phone Number",
                    placeholder = "+20 100 000 0000",
                    isRequired = true,
                    keyboardType = KeyboardType.Phone,
                    error = formState.phoneError
                )

                // Email
                DentoothFormField(
                    value = formState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = "Email Address",
                    placeholder = "patient@example.com",
                    keyboardType = KeyboardType.Email
                )

                // Gender Selection
                Column {
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(Gender.MALE, Gender.FEMALE).forEach { gender ->
                            FilterChip(
                                selected = formState.gender == gender,
                                onClick = { viewModel.onGenderChange(gender) },
                                label = { Text(gender.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }

                // Blood Type
                DentoothFormField(
                    value = formState.bloodType,
                    onValueChange = { viewModel.onBloodTypeChange(it) },
                    label = "Blood Type",
                    placeholder = "e.g. A+, O+, B-"
                )

                // Address
                DentoothFormField(
                    value = formState.address,
                    onValueChange = { viewModel.onAddressChange(it) },
                    label = "Residential Address",
                    placeholder = "City, Street address"
                )

                // Initial Notes
                DentoothFormField(
                    value = formState.notes,
                    onValueChange = { viewModel.onNotesChange(it) },
                    label = "Clinical / Reception Notes",
                    placeholder = "Chief complaint or initial observations",
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.save() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = if (patientId == null) "Complete Registration" else "Save Changes",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
