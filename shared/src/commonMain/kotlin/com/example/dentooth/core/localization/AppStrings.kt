package com.example.dentooth.core.localization

/**
 * Universal application strings contract.
 * Ensures zero hardcoded strings and strict compile-time localization.
 */
interface AppStrings {
    // Brand & App Attributes
    val appName: String
    val tagline: String
    val currency: String

    // Common Actions & States
    val save: String
    val cancel: String
    val delete: String
    val edit: String
    val create: String
    val add: String
    val back: String
    val close: String
    val search: String
    val filter: String
    val all: String
    val status: String
    val actions: String
    val clear: String
    val refresh: String
    val loading: String
    val error: String
    val success: String
    val retry: String
    val confirm: String
    val proceed: String
    val dismiss: String
    val noData: String
    val required: String
    val optional: String
    val date: String
    val time: String
    val amount: String
    val cost: String
    val total: String
    val notes: String
    val phone: String
    val email: String
    val address: String
    val doctor: String
    val mainClinic: String
    val lowStock: String
    val items: String
    val yearsUnit: String
    val today: String
    val previous: String
    val next: String

    // Navigation Tabs
    val navDashboard: String
    val navPatients: String
    val navCalendar: String
    val navBilling: String
    val navInventory: String
    val navStaff: String
    val navAnalytics: String
    val navSettings: String
    val navLabs: String
    val navInsurance: String
    val navAutomations: String

    // Auth & Account
    val loginTitle: String
    val loginSubtitle: String
    val emailLabel: String
    val passwordLabel: String
    val signInButton: String
    val forgotPassword: String
    val sendResetLink: String
    val rememberMe: String
    val logout: String
    val invalidCredentialsError: String
    val resetLinkSentSuccess: String

    // Dashboard
    val goodMorning: String
    val dashboardTitle: String
    val branchSelector: String
    val todayAppointmentsCount: String
    val todayRevenue: String
    val totalPatients: String
    val pendingInvoicesCount: String
    val recentAppointments: String
    val quickActionsTitle: String
    val btnNewPatient: String
    val btnAddIntake: String
    val clinicAdminSection: String
    val operationsEngagementSection: String
    val tileInventory: String
    val tileStaff: String
    val tileAnalytics: String
    val tileSettings: String
    val tileLabs: String
    val tileInsurance: String
    val tileAutomations: String
    val tileInventorySubtitle: String
    val tileStaffSubtitle: String
    val tileAnalyticsSubtitle: String
    val tileSettingsSubtitle: String
    val tileLabsSubtitle: String
    val tileInsuranceSubtitle: String
    val tileAutomationsSubtitle: String
    val inventoryAlertTitle: String
    val inventoryAlertSubtitle: String
    val todaySchedule: String
    val noAppointmentsToday: String
    val generalChair: String

    // Patients & Cohorts
    val patientsHeader: String
    val searchPatientsPlaceholder: String
    val registerPatientButton: String
    val noPatientsFound: String
    val manageGroupsButton: String
    val patientGroupsTitle: String
    val createNewGroup: String
    val groupNameLabel: String
    val groupDescriptionLabel: String
    val groupColorLabel: String
    val patientNumber: String
    val patientFullName: String
    val patientDateOfBirth: String
    val patientGender: String
    val genderMale: String
    val genderFemale: String
    val genderOther: String
    val patientBloodType: String
    val patientBalanceDue: String
    val patientTotalBilled: String
    val patientTotalPaid: String
    val patientLastVisit: String

    // Patient Details & Tabs
    val tabOverview: String
    val tabDentalChart: String
    val tabTreatmentPlans: String
    val tabXrays: String
    val tabMedicalHistory: String
    val tabNotes: String
    val qrIntakeButton: String
    val qrIntakeTitle: String
    val qrIntakeSubtitle: String
    val qrScanInstruction: String
    val qrCopyLink: String
    val qrLinkCopied: String
    val qrSimulateFormButton: String

    // Medical History Questionnaire
    val medHistoryAllergies: String
    val medHistoryMedications: String
    val medHistoryConditions: String
    val condDiabetes: String
    val condHypertension: String
    val condHeartDisease: String
    val condBleedingDisorder: String
    val condAllergies: String
    val condPregnancy: String
    val signAndSubmit: String

    // Calendar & Operatory Management
    val calendarHeader: String
    val btnNewAppointment: String
    val viewDay: String
    val viewWeek: String
    val viewMonth: String
    val viewChairs: String
    val byChair: String
    val byDoctor: String
    val allDoctors: String
    val allStatuses: String
    val weekOf: String
    val scheduleAgenda: String
    val appointmentDoctor: String
    val appointmentChair: String
    val appointmentDuration: String
    val appointmentConflictAlert: String
    val statusScheduled: String
    val statusConfirmed: String
    val statusInProgress: String
    val statusCompleted: String
    val statusCancelled: String
    val statusNoShow: String

    // Financial & Billing
    val billingHeader: String
    val tabInvoices: String
    val tabPayments: String
    val tabExpenses: String
    val tabFinancialSummary: String
    val btnNewInvoice: String
    val btnRecordPayment: String
    val btnAddExpense: String
    val invoiceNumber: String
    val invoicePatient: String
    val invoiceRemaining: String
    val paymentMethod: String
    val paymentCash: String
    val paymentVisa: String
    val paymentInstapay: String
    val paymentValu: String
    val statusPaid: String
    val statusPartial: String
    val statusOverdue: String
    val statusDraft: String

    // Dental Chart & Procedures
    val dentalChartHeader: String
    val adultArch: String
    val pediatricArch: String
    val toothLabel: String
    val surfaceMesial: String
    val surfaceOcclusal: String
    val surfaceDistal: String
    val surfaceBuccal: String
    val surfaceLingual: String
    val toothDecay: String
    val toothFilled: String
    val toothCrown: String
    val toothRootCanal: String
    val toothMissing: String
    val toothImplant: String
    val toothHealthy: String
    val addProcedureToPlan: String
    val procedureFee: String

    // Dental Labs & Prosthetics
    val labsHeader: String
    val labsSubtitle: String
    val labOrdersTitle: String
    val btnCreateLabOrder: String
    val btnLabDirectory: String
    val labOrderStatus: String
    val restorationType: String
    val labTurnaroundDays: String
    val labStatusSent: String
    val labStatusInFab: String
    val labStatusReady: String
    val labStatusDelivered: String
    val labStatusFitted: String
    val labStatusRemake: String

    // Insurance & Claims
    val insuranceHeader: String
    val insuranceSubtitle: String
    val insuranceClaimsTitle: String
    val btnCreateClaim: String
    val btnInsuranceDirectory: String
    val claimPatientShare: String
    val claimInsurerShare: String
    val claimStatusSubmitted: String
    val claimStatusUnderReview: String
    val claimStatusApproved: String
    val claimStatusRejected: String

    // Automations & Engagement
    val automationsHeader: String
    val automationsSubtitle: String
    val tabAutomationRules: String
    val tabDispatchedLogs: String
    val activeRulesBadge: String
    val dispatchedTodayBadge: String
    val todayCostBadge: String
    val btnTestTrigger: String
    val btnCustomizeTemplates: String
    val templateEnglishLabel: String
    val templateArabicLabel: String
    val mergeTagsHelpTitle: String
    val mergeTagsDescription: String

    // Settings & Preferences
    val languageSetting: String
    val languageEnglish: String
    val languageArabic: String
}
