package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.util.categories
import org.json.JSONObject

@Composable
fun BrowseErrandsScreen(
    api: ApiClient,
    userId: Int,
    refreshKey: Int,
    onNavigate: (Screen, JSONObject?) -> Unit
) {
    var keyword by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All Categories") }
    var appliedKeyword by remember { mutableStateOf("") }
    var appliedCategory by remember { mutableStateOf("") }
    var localRefreshKey by remember { mutableIntStateOf(refreshKey) }

    LaunchedEffect(refreshKey) {
        localRefreshKey = refreshKey
    }

    SectionTitle("Errand Feed")
    CampusTextField("Search errands or locations", keyword, { keyword = it }, leadingIcon = Icons.Filled.Search)

    CategoryDropdown(category, categories, includeAll = true) { category = it }

    FilterChipRow(
        // Included "Others" to match the full simplified list
        chips = listOf("All", "Food", "Printing", "Bluebook", "Delivery", "Supplies", "Others"),
        selected = if (category == "All Categories" || category.isBlank()) "All" else category,
        onSelected = { selectedChip ->
            // No mapping needed anymore! The chip text exactly matches the category text.
            category = if (selectedChip == "All") "All Categories" else selectedChip
            appliedCategory = if (category == "All Categories") "" else category
            appliedKeyword = keyword.trim()
            localRefreshKey++
        }
    )

    CampusButton("Apply Filters") {
        appliedKeyword = keyword.trim()
        appliedCategory = if (category == "All Categories") "" else category
        localRefreshKey++
    }

    RemoteList(
        api = api,
        endpoint = "get_available_errands.php",
        params = mapOf("user_id" to userId.toString(), "keyword" to appliedKeyword, "category" to appliedCategory),
        emptyText = "No available errands yet.",
        refreshKey = localRefreshKey
    ) { item ->
        val appStatus = item.optString("application_status")
        val isRequester = item.optInt("requester_id") == userId
        if (appStatus != "pending" && appStatus != "selected" && !isRequester) {
            ErrandCard(
                errand = item,
                applyMode = true,
                userId = userId,
                minimalDisplay = true,
                onViewDetails = { onNavigate(Screen.ErrandDetails, it) },
                onApply = { onNavigate(Screen.Apply, it) },
                onViewApplicants = {},
                onMessages = {},
                onConfirmCompletion = {},
                onRateHelper = {},
                onCancelErrand = { onNavigate(Screen.CancelErrand, it) },
                onReportErrand = { onNavigate(Screen.Report, it) },
                onStatusTracker = { onNavigate(Screen.Status, it) }
            )
        }
    }
}