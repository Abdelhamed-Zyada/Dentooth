package com.example.dentooth.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * DentoBrain Design System Color Tokens
 * Rooted in Neural Intelligence (Navy/Lavender) & Clinical Precision (Teal/White).
 */
object DentoothColors {
    // Brand Colors - DentoBrain Identity
    val NeuralNavy = Color(0xFF0A2540)
    val SynapseTeal = Color(0xFF00B4D8)
    val CortexLavender = Color(0xFF7B6CF6)
    val ClinicalWhite = Color(0xFFF8FAFB)
    val PulseCoral = Color(0xFFFF6B6B)
    val RecallGreen = Color(0xFF2DD4BF)
    val FabricationAmber = Color(0xFFFBBF24)
    val SurfaceSlate = Color(0xFF1E293B)

    // Primary Palette (Neural Navy)
    val Primary = NeuralNavy
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFD6E4F0)
    val OnPrimaryContainer = Color(0xFF051728)

    // Secondary Palette (Synapse Teal)
    val Secondary = SynapseTeal
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFD0F4FC)
    val OnSecondaryContainer = Color(0xFF003642)

    // Tertiary Palette (Cortex Lavender)
    val Tertiary = CortexLavender
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFECE9FE)
    val OnTertiaryContainer = Color(0xFF261D63)

    // Error Palette (Pulse Coral)
    val Error = PulseCoral
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFE8E8)
    val OnErrorContainer = Color(0xFF5A000A)

    // Neutral Surfaces & Backgrounds (Clinical White & Slate)
    val Background = ClinicalWhite
    val OnBackground = Color(0xFF0A1828)
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF0A1828)
    val SurfaceVariant = Color(0xFFEEF2F6)
    val OnSurfaceVariant = Color(0xFF475569)
    val Outline = Color(0xFF94A3B8)
    val OutlineVariant = Color(0xFFCBD5E1)

    // Dark Mode Palette
    val PrimaryDark = Color(0xFF38BDF8)
    val OnPrimaryDark = Color(0xFF082F49)
    val PrimaryContainerDark = Color(0xFF0C4A6E)
    val OnPrimaryContainerDark = Color(0xFFBAE6FD)

    val SecondaryDark = SynapseTeal
    val OnSecondaryDark = Color(0xFF003642)

    val TertiaryDark = Color(0xFFA78BFA)
    val OnTertiaryDark = Color(0xFF2E1065)

    val BackgroundDark = Color(0xFF0B1320)
    val OnBackgroundDark = Color(0xFFF1F5F9)
    val SurfaceDark = Color(0xFF131F33)
    val OnSurfaceDark = Color(0xFFF1F5F9)
    val SurfaceVariantDark = SurfaceSlate
    val OnSurfaceVariantDark = Color(0xFF94A3B8)

    // Semantic Appointment Status Colors
    val StatusScheduled = SynapseTeal
    val StatusConfirmed = RecallGreen
    val StatusInProgress = FabricationAmber
    val StatusCompleted = Color(0xFF10B981)
    val StatusCancelled = PulseCoral
    val StatusNoShow = Color(0xFF64748B)

    // Semantic Financial Colors
    val InvoicePaid = Color(0xFF10B981)
    val InvoicePartial = FabricationAmber
    val InvoiceOverdue = PulseCoral
    val InvoiceDraft = Color(0xFF64748B)

    // Dental Chart Tooth Colors
    val ToothHealthy = Color(0xFFFFFFFF)
    val ToothHealthyBorder = Color(0xFF94A3B8)
    val ToothCaries = PulseCoral
    val ToothFilled = SynapseTeal
    val ToothCrown = FabricationAmber
    val ToothRootCanal = CortexLavender
    val ToothMissing = Color(0xFFCBD5E1)
    val ToothImplant = RecallGreen
    val ToothSelected = Color(0xFF38BDF8)
}

/** Alias to canonical brand name */
val DentoBrainColors = DentoothColors
