package com.example.dentooth.feature.admin.inventory.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryCategory
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryItem
import com.example.dentooth.feature.admin.inventory.domain.model.StockAdjustmentReason
import com.example.dentooth.feature.admin.inventory.ui.components.AddInventoryItemDialog
import com.example.dentooth.feature.admin.inventory.ui.components.RestockDialog

class InventoryScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<InventoryViewModel>()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                DentoothTopBar(
                    title = "Inventory & Supplies",
                    onBackClick = { navigator.pop() }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openAddItemDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.items.isLoading) {
                    DentoothLoadingState(message = "Loading inventory...")
                } else {
                    val itemsList = state.items.dataOrNull() ?: emptyList()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Low stock alert warning banner if any items are low
                        if (state.lowStockItems.isNotEmpty()) {
                            item(key = "low-stock-banner") {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFF3E0),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = "Alert",
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(28.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${state.lowStockItems.size} Consumables Require Restocking",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFBF360C)
                                            )
                                            Text(
                                                text = "Critical items: " + state.lowStockItems.take(2).joinToString { it.name },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFFE65100),
                                                maxLines = 1
                                            )
                                        }

                                        FilledTonalButton(
                                            onClick = { viewModel.toggleOnlyLowStock() },
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = Color(0xFFFFE0B2),
                                                contentColor = Color(0xFFE65100)
                                            ),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(if (state.onlyLowStockFilter) "Show All" else "Filter", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }

                        // Search Field
                        item(key = "search-input") {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Search consumables, materials, suppliers...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Category Filter Chips
                        item(key = "category-chips") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = state.selectedCategory == null,
                                    onClick = { viewModel.setSelectedCategory(null) },
                                    label = { Text("All Categories (${state.allItems.size})") }
                                )

                                InventoryCategory.entries.forEach { cat ->
                                    FilterChip(
                                        selected = state.selectedCategory == cat,
                                        onClick = {
                                            viewModel.setSelectedCategory(if (state.selectedCategory == cat) null else cat)
                                        },
                                        label = { Text(cat.label) }
                                    )
                                }
                            }
                        }

                        if (itemsList.isEmpty()) {
                            item(key = "empty-list") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No inventory items found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(itemsList, key = { it.id }) { item ->
                                InventoryItemCard(
                                    item = item,
                                    onRestock = { viewModel.openRestockDialog(item) },
                                    onUseOne = { viewModel.adjustStock(item.id, -1, StockAdjustmentReason.PROCEDURE_USAGE) },
                                    onAddOne = { viewModel.adjustStock(item.id, 1, StockAdjustmentReason.RESTOCK) }
                                )
                            }
                        }
                    }
                }
            }

            // Restock Dialog
            state.itemToRestock?.let { item ->
                RestockDialog(
                    item = item,
                    onDismiss = { viewModel.closeRestockDialog() },
                    onRestock = { qty, cost -> viewModel.restockItem(item.id, qty, cost) }
                )
            }

            // Add Item Dialog
            if (state.isAddItemOpen) {
                AddInventoryItemDialog(
                    onDismiss = { viewModel.closeAddItemDialog() },
                    onAdd = { name, category, stock, minStock, uom, cost, supplier, phone, notes ->
                        viewModel.addItem(name, category, stock, minStock, uom, cost, supplier, phone, notes)
                    }
                )
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    onRestock: () -> Unit,
    onUseOne: () -> Unit,
    onAddOne: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge + Stock Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = item.category.composeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = item.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = item.category.composeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = item.status.composeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = item.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = item.status.composeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item Name & Supplier
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Supplier: ${item.supplierName} • Last restock: ${item.lastRestockedDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Stock Counts & Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${item.currentStock}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = item.status.composeColor
                        )
                        Text(
                            text = " / ${item.minStockLevel} min ${item.unitOfMeasure}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        )
                    }

                    Text(
                        text = "Unit: ${item.unitCost.toInt()} EGP • Total: ${item.totalValue.toInt()} EGP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Quick Increment/Decrement + Restock CTA
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onUseOne,
                        enabled = item.currentStock > 0,
                        modifier = Modifier
                            .height(34.dp)
                            .padding(end = 4.dp)
                    ) {
                        Text("-1 Use", style = MaterialTheme.typography.labelSmall)
                    }

                    FilledTonalButton(
                        onClick = onRestock,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Restock", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
