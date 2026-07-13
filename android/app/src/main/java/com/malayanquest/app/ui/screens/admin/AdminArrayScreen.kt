package com.malayanquest.app.ui.screens.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.WarningAmber
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
    if (title == "Manage Users") {
        var keyword by remember { mutableStateOf("") }
        CampusTextField("Search by name", keyword, { keyword = it }, leadingIcon = Icons.Filled.Search)
        FilterChipRow(listOf("All", "Verified", "Restricted", "Deactivated"), "All")
        InfoPanel("The current backend returns all users. This search field is a UI placeholder until server-side filtering is added.")
    } else {
        FilterChipRow(
            when {
                title.contains("Errands") -> listOf("All", "Open", "Active", "Completed", "Cancelled", "Reported", "Flagged")
                title.contains("Reports") -> listOf("Pending", "Under Review", "Resolved", "Dismissed")
                else -> listOf("All", "Allowed", "Flagged", "Rejected")
            },
            "All"
        )
    }
    if (title == "Moderation Logs") {
        InfoPanel("The backend does not currently have a moderation log endpoint, so this screen reuses flagged errands for the prototype.", Icons.Filled.Report, "Moderation logs", WarningAmber)
    }
    RemoteList(
        api = api,
        endpoint = endpoint,
        params = mapOf("admin_id" to adminId.toString()),
        emptyText = "No records."
    ) { item ->
        AdminRecordCard(item, title, onView = onViewRecord)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
