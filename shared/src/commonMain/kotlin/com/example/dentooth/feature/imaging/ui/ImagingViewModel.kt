package com.example.dentooth.feature.imaging.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.feature.imaging.data.ImagingRepository
import com.example.dentooth.feature.imaging.domain.model.ImageType
import com.example.dentooth.feature.imaging.domain.model.PatientImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ImagingUiState(
    val allImages: List<PatientImage> = emptyList(),
    val filteredImages: List<PatientImage> = emptyList(),
    val selectedFilter: ImageType? = null,
    val isLoading: Boolean = false
)

class ImagingViewModel(
    private val patientId: String,
    private val imagingRepository: ImagingRepository
) : ScreenModel {

    private val _selectedFilter = MutableStateFlow<ImageType?>(null)

    val state: StateFlow<ImagingUiState> = combine(
        imagingRepository.getImagesForPatient(patientId),
        _selectedFilter
    ) { images, filter ->
        val filtered = if (filter == null) images else images.filter { it.type == filter }
        ImagingUiState(
            allImages = images,
            filteredImages = filtered,
            selectedFilter = filter,
            isLoading = false
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImagingUiState(isLoading = true)
    )

    fun setFilter(filter: ImageType?) {
        _selectedFilter.value = if (_selectedFilter.value == filter) null else filter
    }

    fun addImage(type: ImageType, title: String, toothNumber: Int?, notes: String?) {
        screenModelScope.launch {
            val image = PatientImage(
                id = "",
                patientId = patientId,
                toothNumber = toothNumber,
                type = type,
                title = title.trim(),
                date = "",
                notes = notes?.trim()?.ifEmpty { null }
            )
            imagingRepository.addImage(image)
        }
    }

    fun deleteImage(imageId: String) {
        screenModelScope.launch {
            imagingRepository.deleteImage(imageId)
        }
    }
}
