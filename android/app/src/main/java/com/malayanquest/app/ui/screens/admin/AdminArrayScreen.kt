package com.malayanquest.app.ui.screens.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun AdminArrayScreen(
    api: ApiClient,
    adminId: Int,
    endpoint: String,
    title: String,
    onViewRecord: (JSONObject, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val defaultChip = if (endpoint == "admin_get_reports.php") "Pending" else "All"
    var selectedChip by remember(endpoint, title) { mutableStateOf(defaultChip) }
    var keyword by remember(endpoint, title) { mutableStateOf("") }

    when (endpoint) {
        "admin_get_users.php" -> {
            CampusTextField(
                "Search name, email, or student number",
                keyword,
                { keyword = it },
                leadingIcon = Icons.Filled.Search
            )

            FilterChipRow(
                chips = listOf("All", "Pending", "Verified", "Restricted", "Rejected", "Deactivated"),
                selected = selectedChip,
                onSelected = { selectedChip = it }
            )
        }
        "admin_get_errands.php" -> FilterChipRow(
            chips = if (title == "Ratings and Feedback") {
                listOf("All", "Rated", "Closed")
            } else {
                listOf("All", "Open", "Active", "Completed", "Cancelled", "Reported", "Flagged")
            },
            selected = selectedChip,
            onSelected = { selectedChip = it }
        )
        "admin_get_reports.php" -> FilterChipRow(
            chips = listOf("Pending", "Resolved"),
            selected = selectedChip,
            onSelected = { selectedChip = it }
        )
    }

    val params = when (endpoint) {
        "admin_get_users.php" -> mapOf(
            "admin_id" to adminId.toString(),
            "keyword" to keyword.trim(),
            "filter" to selectedChip
        )
        "admin_get_errands.php", "admin_get_reports.php" -> mapOf(
            "admin_id" to adminId.toString(),
            "filter" to selectedChip
        )
        else -> mapOf("admin_id" to adminId.toString())
    }

    RemoteList(
        api = api,
        endpoint = endpoint,
        params = params,
        emptyText = "No records."
    ) { item ->
        AdminRecordCard(item, title, onView = onViewRecord)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
