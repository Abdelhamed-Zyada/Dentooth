# Dentooth — Project Specification

## 1. Vision & Goals

**Dentooth** is a full-featured dental clinic management system built with Kotlin Multiplatform (KMP) and Compose Multiplatform, targeting Android and iOS from a single codebase. Inspired by Dentolize, it aims to cover the complete clinical workflow — from patient intake to treatment charting, billing, and clinic analytics.

### Core Goals
- **All-in-one**: Replace paper, spreadsheets, and fragmented tools with a single app
- **Cross-platform**: Android + iOS from one Kotlin codebase
- **Offline-first**: Works without internet; syncs when connected
- **Role-based**: Different views for Admin, Doctor, Receptionist, Accountant
- **Multi-branch**: One account manages multiple clinic locations
- **Localized**: Arabic + English from day one (RTL-ready)

---

## 2. Tech Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| **Language** | Kotlin | 2.4.21 | Shared business logic + UI |
| **UI Framework** | Compose Multiplatform | 1.12.2 | Declarative cross-platform UI |
| **Build** | Gradle (KTS) + AGP | 9.1.1 | Build system |
| **Navigation** | Voyager | 1.1.0-beta03 | Type-safe multiplatform navigation |
| **DI** | Koin | 4.1.0-Beta5 | Dependency injection |
| **Local DB** | SQLDelight | 2.0.2 | Offline-first SQL database |
| **Networking** | Ktor Client | 3.1.3 | HTTP client for API calls |
| **Serialization** | kotlinx.serialization | 1.8.1 | JSON parsing |
| **Date/Time** | kotlinx-datetime | 0.6.2 | Cross-platform date handling |
| **Image Loading** | Coil 3 | 3.1.0 | Async image loading |
| **Backend** | Supabase | 3.1.4 (KMP SDK) | Auth, DB, Storage, Realtime |
| **Backend DB** | PostgreSQL (via Supabase) | — | Relational data store |
| **Cloud Functions** | Supabase Edge Functions | Deno | SMS, WhatsApp, automation |
| **Cloud Storage** | Supabase Storage | — | X-rays, photos, PDFs |

---

## 3. Architecture

### 3.1 Pattern: Clean Architecture + MVI

```
┌──────────────────────────────────────────────┐
│  UI Layer (Compose Screens + ViewModels)      │
│  - Screens observe StateFlow from ViewModels  │
│  - User actions → Intent → ViewModel          │
├──────────────────────────────────────────────┤
│  Domain Layer (Use Cases + Models)            │
│  - Business rules, validation, transformations│
│  - Pure Kotlin, no framework dependencies     │
├──────────────────────────────────────────────┤
│  Data Layer (Repositories + Data Sources)     │
│  - Repository pattern with offline-first      │
│  - Local: SQLDelight, Remote: Supabase/Ktor   │
└──────────────────────────────────────────────┘
```

### 3.2 Module Structure

```
Dentooth/
├── androidApp/          # Android entry point (MainActivity)
├── iosApp/              # iOS entry point (SwiftUI wrapper)
├── shared/              # KMP shared module (ALL business logic + UI)
│   └── src/
│       ├── commonMain/  # Shared code (95%+ of the app)
│       ├── androidMain/ # Android-specific (platform implementations)
│       └── iosMain/     # iOS-specific (platform implementations)
```

### 3.3 Package Layout (commonMain)

```
com.example.dentooth/
├── core/
│   ├── auth/           # AuthRepository, AuthViewModel, LoginScreen, RegisterScreen
│   ├── di/             # Koin module definitions
│   ├── navigation/     # AppNavigator, NavGraph, BottomNavBar
│   ├── network/        # SupabaseClient setup, ApiService, interceptors
│   ├── database/       # SQLDelight driver factory, DatabaseHelper
│   ├── designsystem/   # Theme, Colors, Typography, Spacing, shared components
│   └── util/           # Extensions, validators, formatters, constants
├── feature/
│   ├── dashboard/      # Home screen with KPIs, quick actions
│   ├── patient/        # Patient CRUD, medical history, groups
│   ├── dentalchart/    # Interactive tooth chart, conditions, per-surface charting
│   ├── treatment/      # Treatment plans, procedures, plan lifecycle
│   ├── calendar/       # Appointment scheduling, day/week/month views
│   ├── billing/        # Invoices, payments, expense tracking
│   ├── inventory/      # Stock management, reorder alerts
│   ├── insurance/      # Insurance companies, policies, claims
│   ├── staff/          # Staff profiles, attendance, permissions
│   ├── imaging/        # X-ray/photo capture, gallery, viewer
│   ├── lab/            # Lab orders, tracking
│   ├── automation/     # Birthday greetings, reminders configuration
│   └── settings/       # Clinic settings, branches, notification prefs, profile
```

### 3.4 Conventions

| Convention | Rule |
|-----------|------|
| **Screen naming** | `{Feature}Screen.kt` (e.g., `PatientListScreen.kt`) |
| **ViewModel naming** | `{Feature}ViewModel.kt` |
| **State class** | `{Feature}UiState` as data class inside ViewModel file |
| **Intent/Event** | `{Feature}Event` as sealed interface |
| **Repository** | Interface in `domain/`, impl in `data/` |
| **Model** | Domain models in `domain/model/`, DTOs in `data/model/` |
| **SQLDelight** | `.sq` files in `shared/src/commonMain/sqldelight/` |
| **String resources** | Compose Multiplatform resources in `composeResources/` |
| **Coroutines** | ViewModels use `viewModelScope`, Repos use `withContext(Dispatchers.IO)` |

---

## 4. Non-Functional Requirements

| Requirement | Target |
|------------|--------|
| **Offline support** | Full CRUD offline, background sync when online |
| **Startup time** | < 2 seconds cold start |
| **Min Android** | API 24 (Android 7.0) |
| **Min iOS** | iOS 16 |
| **Languages** | Arabic (RTL) + English |
| **Accessibility** | Content descriptions, minimum touch targets 48dp |
| **Data security** | Encrypted local DB, HTTPS only, token-based auth |
| **Image size** | Max 5MB per upload, compressed before upload |
| **Sync strategy** | Optimistic local-first, conflict resolution: last-write-wins |

---

## 5. Feature Priority Matrix

| Priority | Features |
|----------|----------|
| 🔴 **P0 — Must Have** | Auth, Patient CRUD, Dental Chart, Treatment Plans, Calendar, Basic Billing, Dashboard |
| 🟡 **P1 — Should Have** | Medical History QR, Imaging, SMS Reminders, Insurance, Inventory, Staff Management, Multi-Branch, Expense Tracking |
| 🟢 **P2 — Nice to Have** | WhatsApp Bot, Online Payments, Birthday Automation, Lab Integration, Advanced Analytics, Offline Sync |

---

## 6. Deployment Strategy

| Platform | Distribution |
|----------|-------------|
| **Android** | Google Play Store (or direct APK for testing) |
| **iOS** | Apple App Store (via TestFlight for testing) |
| **Backend** | Supabase Cloud (managed) or self-hosted |

---

## 7. Referenced Documentation

| Document | Path | Contents |
|----------|------|----------|
| Data Models | [02-DATA-MODELS.md](./02-DATA-MODELS.md) | Complete entity definitions, ER diagram, SQL schemas |
| Feature Specs | [03-FEATURE-SPECS.md](./03-FEATURE-SPECS.md) | Detailed per-feature specifications |
| UI & Design System | [04-UI-DESIGN-SYSTEM.md](./04-UI-DESIGN-SYSTEM.md) | Theme, components, screen specs, navigation |
| API Contracts | [05-API-CONTRACTS.md](./05-API-CONTRACTS.md) | Supabase tables, RLS, edge functions |
| Implementation Guide | [06-IMPLEMENTATION-GUIDE.md](./06-IMPLEMENTATION-GUIDE.md) | Phase-by-phase tasks, dependencies, testing |
