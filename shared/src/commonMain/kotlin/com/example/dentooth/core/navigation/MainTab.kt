package com.example.dentooth.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.dentooth.feature.billing.ui.BillingScreen
import com.example.dentooth.feature.calendar.ui.CalendarScreen
import com.example.dentooth.feature.dashboard.ui.DashboardScreen
import com.example.dentooth.feature.patient.ui.list.PatientListScreen

object DashboardTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Dashboard",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(DashboardScreen())
    }
}

object PatientsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember {
                TabOptions(
                    index = 1u,
                    title = "Patients",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(PatientListScreen())
    }
}

object CalendarTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.DateRange)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Calendar",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(CalendarScreen())
    }
}

object BillingTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ShoppingCart)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Billing",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(BillingScreen())
    }
}
