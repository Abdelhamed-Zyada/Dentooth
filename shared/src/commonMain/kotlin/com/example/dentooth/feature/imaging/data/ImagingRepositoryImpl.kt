package com.example.dentooth.feature.imaging.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.imaging.domain.model.ImageType
import com.example.dentooth.feature.imaging.domain.model.PatientImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ImagingRepositoryImpl : ImagingRepository {

    private val seedImages = mutableListOf(
        PatientImage(
            id = "img-001",
            patientId = "pat-001",
            type = ImageType.OPG_PANORAMIC,
            title = "Initial Full Mouth Panoramic OPG",
            date = "Nov 05, 2026",
            notes = "General bone levels acceptable. Periapical radiolucency noted on mesiobuccal root of #16. Impacted #38."
        ),
        PatientImage(
            id = "img-002",
            patientId = "pat-001",
            toothNumber = 16,
            type = ImageType.PERIAPICAL_XRAY,
            title = "Periapical Pre-op RVG - Tooth #16",
            date = "Nov 10, 2026",
            notes = "Deep coronal radiolucency approximating pulp horn. Canal anatomy 3 canals visible."
        ),
        PatientImage(
            id = "img-003",
            patientId = "pat-001",
            toothNumber = 16,
            type = ImageType.INTRAORAL_PHOTO,
            title = "Intraoral Pre-op Clinical View - #16",
            date = "Nov 10, 2026",
            notes = "Extensive occlusal caries with undermined enamel."
        ),
        PatientImage(
            id = "img-004",
            patientId = "pat-001",
            toothNumber = 24,
            type = ImageType.BITEWING_XRAY,
            title = "Right Premolar Bitewing",
            date = "Nov 10, 2026",
            notes = "Post-restoration check on #24 MO composite."
        ),
        PatientImage(
            id = "img-005",
            patientId = "pat-003",
            type = ImageType.OPG_PANORAMIC,
            title = "Full Mouth Survey OPG",
            date = "Nov 05, 2026",
            notes = "Pre-prosthodontic evaluation for zirconia crowns."
        )
    )

    private val imagesFlow = MutableStateFlow(seedImages)

    override fun getImagesForPatient(patientId: String): Flow<List<PatientImage>> {
        return imagesFlow.map { list -> list.filter { it.patientId == patientId } }
    }

    override suspend fun addImage(image: PatientImage): Result<PatientImage> {
        val current = imagesFlow.value.toMutableList()
        val newImg = image.copy(
            id = if (image.id.isBlank()) "img-${DateTimeUtil.currentInstant().toEpochMilliseconds()}" else image.id,
            date = if (image.date.isBlank()) DateTimeUtil.formatDate(DateTimeUtil.today()) else image.date
        )
        current.add(0, newImg)
        imagesFlow.value = current
        return Result.Success(newImg)
    }

    override suspend fun deleteImage(imageId: String): Result<Unit> {
        val current = imagesFlow.value.toMutableList()
        current.removeAll { it.id == imageId }
        imagesFlow.value = current
        return Result.Success(Unit)
    }
}
