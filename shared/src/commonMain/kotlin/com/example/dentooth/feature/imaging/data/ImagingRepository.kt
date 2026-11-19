package com.example.dentooth.feature.imaging.data

import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.imaging.domain.model.PatientImage
import kotlinx.coroutines.flow.Flow

interface ImagingRepository {
    fun getImagesForPatient(patientId: String): Flow<List<PatientImage>>
    suspend fun addImage(image: PatientImage): Result<PatientImage>
    suspend fun deleteImage(imageId: String): Result<Unit>
}
