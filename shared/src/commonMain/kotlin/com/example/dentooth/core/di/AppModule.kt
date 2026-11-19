package com.example.dentooth.core.di

import com.example.dentooth.core.auth.data.AuthRepository
import com.example.dentooth.core.auth.data.AuthRepositoryImpl
import com.example.dentooth.core.auth.ui.LoginViewModel
import com.example.dentooth.feature.calendar.data.CalendarRepository
import com.example.dentooth.feature.calendar.data.CalendarRepositoryImpl
import com.example.dentooth.feature.calendar.ui.CalendarViewModel
import com.example.dentooth.feature.dashboard.ui.DashboardViewModel
import com.example.dentooth.feature.dentalchart.data.DentalChartRepository
import com.example.dentooth.feature.dentalchart.data.DentalChartRepositoryImpl
import com.example.dentooth.feature.dentalchart.ui.DentalChartViewModel
import com.example.dentooth.feature.imaging.data.ImagingRepository
import com.example.dentooth.feature.imaging.data.ImagingRepositoryImpl
import com.example.dentooth.feature.imaging.ui.ImagingViewModel
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.data.PatientRepositoryImpl
import com.example.dentooth.feature.patient.ui.detail.PatientDetailViewModel
import com.example.dentooth.feature.patient.ui.form.PatientFormViewModel
import com.example.dentooth.feature.patient.ui.list.PatientListViewModel
import com.example.dentooth.feature.treatment.data.TreatmentRepository
import com.example.dentooth.feature.treatment.data.TreatmentRepositoryImpl
import com.example.dentooth.feature.treatment.ui.TreatmentViewModel
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<PatientRepository> { PatientRepositoryImpl() }
    single<DentalChartRepository> { DentalChartRepositoryImpl() }
    single<TreatmentRepository> { TreatmentRepositoryImpl() }
    single<ImagingRepository> { ImagingRepositoryImpl() }
    single<CalendarRepository> { CalendarRepositoryImpl() }
}

val viewModelModule = module {
    factory { LoginViewModel(get()) }
    factory { DashboardViewModel(get(), get()) }
    factory { PatientListViewModel(get()) }
    factory { (patientId: String?) -> PatientFormViewModel(patientId, get()) }
    factory { (patientId: String) -> PatientDetailViewModel(patientId, get()) }
    factory { (patientId: String) -> DentalChartViewModel(patientId, get()) }
    factory { (patientId: String) -> TreatmentViewModel(patientId, get()) }
    factory { (patientId: String) -> ImagingViewModel(patientId, get()) }
    factory { CalendarViewModel(get()) }
}

val appModule = listOf(dataModule, viewModelModule)
