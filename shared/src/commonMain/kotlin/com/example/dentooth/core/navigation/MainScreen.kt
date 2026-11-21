package com.example.dentooth.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator

import com.example.dentooth.core.localization.appStrings

class MainScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(DashboardTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        TabNavigationItem(DashboardTab)
                        TabNavigationItem(PatientsTab)
                        TabNavigationItem(CalendarTab)
                        TabNavigationItem(BillingTab)
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    CurrentTab()
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        val s = appStrings
        val localizedTitle = when (tab) {
            is DashboardTab -> s.navDashboard
            is PatientsTab -> s.navPatients
            is CalendarTab -> s.navCalendar
            is BillingTab -> s.navBilling
            else -> tab.options.title
        }

        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = {
                tab.options.icon?.let { icon ->
                    Icon(painter = icon, contentDescription = localizedTitle)
                }
            },
            label = {
                Text(
                    text = localizedTitle,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
