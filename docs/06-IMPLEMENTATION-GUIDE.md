# Dentooth — Implementation Guide & Roadmap

## 1. Phase 1 — Foundation & Core (Weeks 1–3)

### 1.1 Project Setup (Week 1, Days 1–2)

| # | Task | Files to Create/Modify | Dependencies |
|---|------|----------------------|--------------|
| 1.1.1 | Add all dependencies to `libs.versions.toml` | `gradle/libs.versions.toml` | — |
| 1.1.2 | Update `shared/build.gradle.kts` with new deps | `shared/build.gradle.kts` | 1.1.1 |
| 1.1.3 | Add SQLDelight plugin, kotlinx.serialization plugin | `build.gradle.kts`, `shared/build.gradle.kts` | 1.1.1 |
| 1.1.4 | Create Koin DI module structure | `core/di/AppModule.kt`, `core/di/DataModule.kt`, `core/di/DomainModule.kt` | 1.1.2 |
| 1.1.5 | Setup Supabase client singleton | `core/network/SupabaseProvider.kt` | 1.1.2 |
| 1.1.6 | Setup SQLDelight driver (expect/actual) | `core/database/DriverFactory.kt` (common, android, ios) | 1.1.3 |
| 1.1.7 | Create base classes | `core/util/BaseViewModel.kt`, `core/util/UiState.kt`, `core/util/Result.kt` | 1.1.2 |
| 1.1.8 | Initialize Koin in app entry points | `androidApp/.../MainActivity.kt`, `iosApp/.../iOSApp.swift` | 1.1.4 |

### 1.2 Design System (Week 1, Days 3–5)

| # | Task | Files to Create | Dependencies |
|---|------|----------------|--------------|
| 1.2.1 | Define color scheme (light + dark) | `core/designsystem/Color.kt` | — |
| 1.2.2 | Define typography | `core/designsystem/Typography.kt` | — |
| 1.2.3 | Define spacing & shapes | `core/designsystem/Spacing.kt`, `core/designsystem/Shape.kt` | — |
| 1.2.4 | Create DentoothTheme composable | `core/designsystem/Theme.kt` | 1.2.1–1.2.3 |
| 1.2.5 | Build shared UI components | `core/designsystem/components/TopBar.kt`, `BottomBar.kt`, `SearchBar.kt`, `Avatar.kt`, `StatusBadge.kt`, `EmptyState.kt`, `LoadingState.kt`, `ErrorState.kt`, `FormField.kt`, `InfoRow.kt`, `StatCard.kt`, `ConfirmDialog.kt` | 1.2.4 |

### 1.3 Auth & Navigation (Week 2, Days 1–3)

| # | Task | Files to Create | Dependencies |
|---|------|----------------|--------------|
| 1.3.1 | SQLDelight schema: `UserAccount.sq` | `sqldelight/.../UserAccount.sq` | 1.1.6 |
| 1.3.2 | Auth data layer | `core/auth/data/AuthRepository.kt`, `core/auth/data/AuthRepositoryImpl.kt` | 1.1.5 |
| 1.3.3 | Auth domain | `core/auth/domain/LoginUseCase.kt`, `core/auth/domain/UserSession.kt` | 1.3.2 |
| 1.3.4 | Login screen + ViewModel | `core/auth/ui/LoginScreen.kt`, `core/auth/ui/LoginViewModel.kt` | 1.3.3, 1.2.5 |
| 1.3.5 | Forgot password screen | `core/auth/ui/ForgotPasswordScreen.kt` | 1.3.2, 1.2.5 |
| 1.3.6 | Navigation setup: Voyager tabs + navigator | `core/navigation/MainScreen.kt`, `core/navigation/MainTab.kt` | 1.2.5 |
| 1.3.7 | Auth flow: splash → login → main | `core/navigation/AppNavigator.kt`, `App.kt` (refactor) | 1.3.4, 1.3.6 |
| 1.3.8 | Role-based tab visibility | `core/navigation/MainScreen.kt` | 1.3.3 |

### 1.4 Patient Module (Week 2, Day 4 – Week 3)

| # | Task | Files to Create | Dependencies |
|---|------|----------------|--------------|
| 1.4.1 | SQLDelight schema: `Patient.sq`, `MedicalHistory.sq`, `PatientGroup.sq` | `sqldelight/.../*.sq` | 1.1.6 |
| 1.4.2 | Patient domain models | `feature/patient/domain/model/Patient.kt`, `MedicalHistory.kt`, `PatientGroup.kt` | — |
| 1.4.3 | Patient data layer | `feature/patient/data/PatientRepository.kt`, `PatientRepositoryImpl.kt`, `PatientDto.kt` | 1.4.1, 1.1.5 |
| 1.4.4 | Patient list screen + ViewModel | `feature/patient/ui/list/PatientListScreen.kt`, `PatientListViewModel.kt` | 1.4.3, 1.2.5 |
| 1.4.5 | Patient search & filter | `feature/patient/ui/list/PatientListScreen.kt` | 1.4.4 |
| 1.4.6 | Patient create/edit screen | `feature/patient/ui/form/PatientFormScreen.kt`, `PatientFormViewModel.kt` | 1.4.3, 1.2.5 |
| 1.4.7 | Patient detail screen (tabs) | `feature/patient/ui/detail/PatientDetailScreen.kt`, `PatientDetailViewModel.kt` | 1.4.3, 1.2.5 |
| 1.4.8 | Medical history form | `feature/patient/ui/detail/MedicalHistoryTab.kt` | 1.4.3, 1.2.5 |
| 1.4.9 | Patient notes | `feature/patient/ui/detail/PatientNotesTab.kt` | 1.4.3 |
| 1.4.10 | Patient groups CRUD | `feature/patient/ui/groups/PatientGroupScreen.kt` | 1.4.3 |

### 1.5 Dashboard Skeleton (Week 3, last 2 days)

| # | Task | Files to Create | Dependencies |
|---|------|----------------|--------------|
| 1.5.1 | Dashboard screen (static/mock data) | `feature/dashboard/ui/DashboardScreen.kt`, `DashboardViewModel.kt` | 1.2.5 |
| 1.5.2 | Quick action buttons | `feature/dashboard/ui/DashboardScreen.kt` | 1.5.1 |

---

## 2. Phase 2 — Clinical Core (Weeks 4–6)

| # | Task | Priority |
|---|------|----------|
| 2.1 | SQLDelight: `DentalChart.sq`, `ToothRecord.sq`, `ToothCondition.sq`, `ToothProcedure.sq` | P0 |
| 2.2 | Dental chart domain models | P0 |
| 2.3 | Dental chart repository | P0 |
| 2.4 | Tooth shape Canvas composables (molar, premolar, canine, incisor) | P0 |
| 2.5 | Surface-level rendering (5 surfaces per tooth, color-coded) | P0 |
| 2.6 | Full arch layout (upper + lower, FDI numbering) | P0 |
| 2.7 | Tooth tap → Bottom sheet | P0 |
| 2.8 | Add condition flow (condition picker + surface selector) | P0 |
| 2.9 | Tooth status update | P0 |
| 2.10 | Adult/Pediatric toggle | P0 |
| 2.11 | SQLDelight: `TreatmentPlan.sq`, `PlanProcedure.sq`, `ProcedureCatalog.sq` | P0 |
| 2.12 | Treatment plan CRUD | P0 |
| 2.13 | Procedure catalog (pre-seeded) | P0 |
| 2.14 | Add procedure to plan from tooth detail | P0 |
| 2.15 | Plan status lifecycle | P0 |
| 2.16 | Basic imaging: Camera/gallery → attach to patient/tooth | P1 |

---

## 3. Phase 3 — Scheduling (Weeks 7–8)

| # | Task | Priority |
|---|------|----------|
| 3.1 | SQLDelight: `Appointment.sq`, `Chair.sq` | P0 |
| 3.2 | Appointment domain & repository | P0 |
| 3.3 | Day view: Timeline with appointment blocks | P0 |
| 3.4 | Week view | P0 |
| 3.5 | Month view (dot indicators) | P0 |
| 3.6 | Appointment booking form | P0 |
| 3.7 | Conflict detection (doctor + chair) | P0 |
| 3.8 | Appointment status transitions | P0 |
| 3.9 | Link appointment to treatment plan procedure | P1 |
| 3.10 | Column modes: by chair / by doctor | P1 |

---

## 4. Phase 4 — Finance (Weeks 9–11)

| # | Task | Priority |
|---|------|----------|
| 4.1 | SQLDelight: `Invoice.sq`, `InvoiceItem.sq`, `Payment.sq`, `Expense.sq` | P0 |
| 4.2 | Invoice list with filters and status | P0 |
| 4.3 | Invoice builder (manual + auto from plan) | P0 |
| 4.4 | Payment recording | P0 |
| 4.5 | PDF generation (expect/actual) | P1 |
| 4.6 | Expense CRUD | P1 |
| 4.7 | Financial summary cards | P1 |

---

## 5. Phase 5 — Administration (Weeks 12–14)

| # | Task | Priority |
|---|------|----------|
| 5.1 | Dashboard → live data (appointments, revenue, alerts) | P0 |
| 5.2 | SQLDelight: `StaffProfile.sq`, `AttendanceLog.sq`, `Branch.sq` | P1 |
| 5.3 | Staff CRUD + attendance | P1 |
| 5.4 | Inventory CRUD + reorder alerts | P1 |
| 5.5 | Multi-branch: branch selector, branch-scoped data | P1 |
| 5.6 | Clinic settings screen | P1 |
| 5.7 | Analytics charts (revenue trend, procedure distribution) | P1 |

---

## 6. Phase 6 — Engagement & Automation (Weeks 15–17)

| # | Task | Priority |
|---|------|----------|
| 6.1 | SMS reminders edge function + UI config | P1 |
| 6.2 | QR medical history form (edge function + web form) | P1 |
| 6.3 | Patient grouping UI (bulk assign, filter) | P1 |
| 6.4 | Insurance module: companies, policies, claims | P1 |
| 6.5 | Lab module: labs, orders, tracking | P1 |
| 6.6 | Birthday automation edge function | P2 |

---

## 7. Phase 7 — Advanced (Weeks 18–20)

| # | Task | Priority |
|---|------|----------|
| 7.1 | WhatsApp Business API integration | P2 |
| 7.2 | Online payment gateway (Stripe/Paymob) | P2 |
| 7.3 | Offline sync engine (sync queue, background sync) | P2 |
| 7.4 | Advanced permissions (feature-level ACL) | P2 |
| 7.5 | Performance optimization (pagination, lazy loading, image caching) | P2 |
| 7.6 | Arabic localization (all strings, RTL layout testing) | P1 |
| 7.7 | Tablet / adaptive layout (medium/expanded breakpoints) | P2 |

---

## 8. Testing Strategy

### 8.1 Unit Tests
- **Domain**: Use cases, validators, formatters — pure Kotlin, `commonTest`
- **ViewModels**: State changes on events — using test coroutine dispatchers
- **Repository**: Mock Supabase responses — using interfaces + fakes

### 8.2 UI Tests
- **Compose UI tests**: Component rendering, interaction (click, type, scroll)
- **Screen tests**: Navigation flows, form validation feedback
- Target: Android instrumented tests via `androidDeviceTest`

### 8.3 Integration Tests
- **Supabase integration**: Against test project (optional, CI only)
- **SQLDelight**: Schema migration tests

### 8.4 Test File Convention
```
commonTest/kotlin/com/example/dentooth/
├── feature/
│   ├── patient/
│   │   ├── domain/PatientValidatorTest.kt
│   │   └── ui/PatientListViewModelTest.kt
│   ├── dentalchart/...
│   └── billing/...
├── core/
│   └── auth/LoginViewModelTest.kt
```

---

## 9. File Creation Order (Phase 1)

This is the exact sequence to create files in Phase 1:

```
Step 1: gradle/libs.versions.toml (update)
Step 2: build.gradle.kts (add serialization plugin)
Step 3: shared/build.gradle.kts (add all deps + sqldelight config)
Step 4: core/util/Result.kt
Step 5: core/util/UiState.kt
Step 6: core/designsystem/Color.kt
Step 7: core/designsystem/Typography.kt
Step 8: core/designsystem/Spacing.kt
Step 9: core/designsystem/Shape.kt
Step 10: core/designsystem/Theme.kt
Step 11: core/designsystem/components/*.kt (all shared components)
Step 12: core/database/DriverFactory.kt (expect/actual)
Step 13: core/network/SupabaseProvider.kt
Step 14: core/di/AppModule.kt
Step 15: sqldelight/UserAccount.sq
Step 16: core/auth/data/AuthRepository.kt
Step 17: core/auth/data/AuthRepositoryImpl.kt
Step 18: core/auth/domain/UserSession.kt
Step 19: core/auth/domain/LoginUseCase.kt
Step 20: core/auth/ui/LoginViewModel.kt
Step 21: core/auth/ui/LoginScreen.kt
Step 22: core/auth/ui/ForgotPasswordScreen.kt
Step 23: core/navigation/MainTab.kt
Step 24: core/navigation/MainScreen.kt
Step 25: core/navigation/AppNavigator.kt
Step 26: App.kt (refactor to navigation root)
Step 27: sqldelight/Patient.sq
Step 28: sqldelight/MedicalHistory.sq
Step 29: feature/patient/domain/model/Patient.kt
Step 30: feature/patient/domain/model/MedicalHistory.kt
Step 31: feature/patient/data/dto/PatientDto.kt
Step 32: feature/patient/data/PatientRepository.kt
Step 33: feature/patient/data/PatientRepositoryImpl.kt
Step 34: feature/patient/ui/list/PatientListViewModel.kt
Step 35: feature/patient/ui/list/PatientListScreen.kt
Step 36: feature/patient/ui/form/PatientFormViewModel.kt
Step 37: feature/patient/ui/form/PatientFormScreen.kt
Step 38: feature/patient/ui/detail/PatientDetailViewModel.kt
Step 39: feature/patient/ui/detail/PatientDetailScreen.kt
Step 40: feature/patient/ui/detail/MedicalHistoryTab.kt
Step 41: feature/patient/ui/detail/PatientNotesTab.kt
Step 42: feature/dashboard/ui/DashboardViewModel.kt
Step 43: feature/dashboard/ui/DashboardScreen.kt
Step 44: core/di/DataModule.kt (register all repos)
Step 45: core/di/DomainModule.kt (register all use cases)
Step 46: core/di/UiModule.kt (register all viewmodels)
Step 47: androidApp update: init Koin, apply theme
Step 48: iosApp update: init Koin
```

---

## 10. CI/CD (Future)

| Stage | Tool | Action |
|-------|------|--------|
| Lint | ktlint | Code style check |
| Build | Gradle | Compile Android + shared |
| Unit Test | Gradle | Run `commonTest` |
| UI Test | Gradle | Run `androidDeviceTest` on emulator |
| Distribute | Firebase App Distribution / TestFlight | Beta testing |
| Release | Google Play Console / App Store Connect | Production |
