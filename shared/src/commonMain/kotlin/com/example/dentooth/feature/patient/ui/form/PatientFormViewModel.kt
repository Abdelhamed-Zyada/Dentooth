package com.example.dentooth.feature.patient.ui.form

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.domain.model.Gender
import com.example.dentooth.feature.patient.domain.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PatientFormState(
    val id: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val gender: Gender = Gender.MALE,
    val address: String = "",
    val bloodType: String = "",
    val notes: String = "",
    val firstNameError: String? = null,
    val phoneError: String? = null,
    val saveState: UiState<Patient> = UiState.Idle
)

class PatientFormViewModel(
    private val patientId: String?,
    private val patientRepository: PatientRepository
) : ScreenModel {

    private val _formState = MutableStateFlow(PatientFormState(id = patientId))
    val formState: StateFlow<PatientFormState> = _formState.asStateFlow()

    init {
        if (patientId != null) {
            loadPatient(patientId)
        }
    }

    private fun loadPatient(id: String) {
        screenModelScope.launch {
            patientRepository.getPatientById(id).onSuccess { p ->
                _formState.value = _formState.value.copy(
                    firstName = p.firstName,
                    lastName = p.lastName,
                    phone = p.phone,
                    email = p.email ?: "",
                    gender = p.gender,
                    address = p.address ?: "",
                    bloodType = p.bloodType ?: "",
                    notes = p.notes ?: ""
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _formState.value = _formState.value.copy(firstName = value, firstNameError = null)
    }

    fun onLastNameChange(value: String) {
        _formState.value = _formState.value.copy(lastName = value)
    }

    fun onPhoneChange(value: String) {
        _formState.value = _formState.value.copy(phone = value, phoneError = null)
    }

    fun onEmailChange(value: String) {
        _formState.value = _formState.value.copy(email = value)
    }

    fun onGenderChange(value: Gender) {
        _formState.value = _formState.value.copy(gender = value)
    }

    fun onAddressChange(value: String) {
        _formState.value = _formState.value.copy(address = value)
    }

    fun onBloodTypeChange(value: String) {
        _formState.value = _formState.value.copy(bloodType = value)
    }

    fun onNotesChange(value: String) {
        _formState.value = _formState.value.copy(notes = value)
    }

    fun save() {
        val current = _formState.value
        var hasError = false
        var firstNameErr: String? = null
        var phoneErr: String? = null

        if (current.firstName.isBlank()) {
            firstNameErr = "First name is required"
            hasError = true
        }

        if (current.phone.isBlank()) {
            phoneErr = "Phone number is required"
            hasError = true
        }

        if (hasError) {
            _formState.value = current.copy(
                firstNameError = firstNameErr,
                phoneError = phoneErr
            )
            return
        }

        screenModelScope.launch {
            _formState.value = current.copy(saveState = UiState.Loading)
            val patient = Patient(
                id = current.id ?: "",
                clinicId = "cln-001",
                patientNumber = "",
                firstName = current.firstName.trim(),
                lastName = current.lastName.trim(),
                phone = current.phone.trim(),
                email = current.email.trim().ifEmpty { null },
                gender = current.gender,
                address = current.address.trim().ifEmpty { null },
                bloodType = current.bloodType.trim().ifEmpty { null },
                notes = current.notes.trim().ifEmpty { null }
            )

            val result = patientRepository.savePatient(patient)
            result.onSuccess { saved ->
                _formState.value = _formState.value.copy(saveState = UiState.Success(saved))
            }.onError { msg, _ ->
                _formState.value = _formState.value.copy(saveState = UiState.Error(msg))
            }
        }
    }
}
