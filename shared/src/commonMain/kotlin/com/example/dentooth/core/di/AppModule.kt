package com.example.dentooth.core.di

import com.example.dentooth.core.auth.data.AuthRepository
import com.example.dentooth.core.auth.data.AuthRepositoryImpl
import com.example.dentooth.core.auth.data.SupabaseAuthRepositoryImpl
import com.example.dentooth.core.auth.ui.LoginViewModel
import com.example.dentooth.feature.admin.analytics.data.AnalyticsRepository
import com.example.dentooth.feature.admin.analytics.data.AnalyticsRepositoryImpl
import com.example.dentooth.feature.admin.analytics.ui.AnalyticsViewModel
import com.example.dentooth.feature.admin.branch.data.BranchRepository
import com.example.dentooth.feature.admin.branch.data.BranchRepositoryImpl
import com.example.dentooth.feature.admin.inventory.data.InventoryRepository
import com.example.dentooth.feature.admin.inventory.data.InventoryRepositoryImpl
import com.example.dentooth.feature.admin.inventory.ui.InventoryViewModel
import com.example.dentooth.feature.admin.settings.data.SettingsRepository
import com.example.dentooth.feature.admin.settings.data.SettingsRepositoryImpl
import com.example.dentooth.feature.admin.staff.data.StaffRepository
import com.example.dentooth.feature.admin.staff.data.StaffRepositoryImpl
import com.example.dentooth.feature.admin.staff.ui.StaffViewModel
import com.example.dentooth.feature.automation.data.AutomationRepository
import com.example.dentooth.feature.automation.data.AutomationRepositoryImpl
import com.example.dentooth.feature.automation.ui.AutomationViewModel
import com.example.dentooth.feature.billing.data.BillingRepository
import com.example.dentooth.feature.billing.data.BillingRepositoryImpl
import com.example.dentooth.feature.billing.ui.BillingViewModel
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
import com.example.dentooth.feature.insurance.data.InsuranceRepository
import com.example.dentooth.feature.insurance.data.InsuranceRepositoryImpl
import com.example.dentooth.feature.insurance.ui.InsuranceViewModel
import com.example.dentooth.feature.lab.data.LabRepository
import com.example.dentooth.feature.lab.data.LabRepositoryImpl
import com.example.dentooth.feature.lab.ui.LabViewModel
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.data.PatientRepositoryImpl
import com.example.dentooth.feature.patient.data.SupabasePatientRepositoryImpl
import com.example.dentooth.feature.patient.ui.detail.PatientDetailViewModel
import com.example.dentooth.feature.patient.ui.form.PatientFormViewModel
import com.example.dentooth.feature.patient.ui.list.PatientListViewModel
import com.example.dentooth.feature.treatment.data.TreatmentRepository
import com.example.dentooth.feature.treatment.data.TreatmentRepositoryImpl
import com.example.dentooth.feature.treatment.ui.TreatmentViewModel
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { SupabaseAuthRepositoryImpl() }
    single<PatientRepository> { SupabasePatientRepositoryImpl() }
    single<DentalChartRepository> { DentalChartRepositoryImpl() }
    single<TreatmentRepository> { TreatmentRepositoryImpl() }
    single<ImagingRepository> { ImagingRepositoryImpl() }
    single<CalendarRepository> { CalendarRepositoryImpl() }
    single<BillingRepository> { BillingRepositoryImpl() }
    single<InventoryRepository> { InventoryRepositoryImpl() }
    single<StaffRepository> { StaffRepositoryImpl() }
    single<BranchRepository> { BranchRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<AnalyticsRepository> { AnalyticsRepositoryImpl() }
    single<LabRepository> { LabRepositoryImpl() }
    single<InsuranceRepository> { InsuranceRepositoryImpl() }
    single<AutomationRepository> { AutomationRepositoryImpl() }
}

val viewModelModule = module {
    factory { LoginViewModel(get()) }
    factory { DashboardViewModel(get(), get(), get(), get(), get(), get()) }
    factory { PatientListViewModel(get()) }
    factory { (patientId: String?) -> PatientFormViewModel(patientId, get()) }
    factory { (patientId: String) -> PatientDetailViewModel(patientId, get()) }
    factory { (patientId: String) -> DentalChartViewModel(patientId, get()) }
    factory { (patientId: String) -> TreatmentViewModel(patientId, get()) }
    factory { (patientId: String) -> ImagingViewModel(patientId, get()) }
    factory { CalendarViewModel(get()) }
    factory { BillingViewModel(get()) }
    factory { InventoryViewModel(get()) }
    factory { StaffViewModel(get()) }
    factory { AnalyticsViewModel(get()) }
    factory { LabViewModel(get()) }
    factory { InsuranceViewModel(get()) }
    factory { AutomationViewModel(get()) }
}

val appModule = listOf(dataModule, viewModelModule)
