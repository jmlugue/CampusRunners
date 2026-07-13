package com.malayanquest.app.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun AdminDashboardScreen(
    api: ApiClient,
    userId: Int,
    onOpenAdminArray: (String, String) -> Unit,
    onNavigateToRatings: () -> Unit,
    onSwitchToStudent: () -> Unit,
    onLogout: () -> Unit
) {
    InfoPanel("Monitor Malayan Quest activity and moderate unsafe records.", Icons.Filled.Security, "Admin Dashboard")
    RemoteObject(
        api = api,
        endpoint = "admin_dashboard.php",
        params = mapOf("admin_id" to userId.toString()),
        emptyText = "Dashboard unavailable."
    ) { data ->
        SummaryGrid(
            listOf(
                "Total Users" to data.optInt("total_users").toString(),
                "Verified" to data.optInt("verified_users").toString(),
                "Open" to data.optInt("open_errands").toString(),
                "Active" to data.optInt("active_errands").toString(),
                "Completed" to data.optInt("completed_errands").toString(),
                "Cancelled" to data.optInt("cancelled_errands").toString(),
                "Reported" to data.optInt("reported_errands").toString(),
                "Flagged" to data.optInt("flagged_errands").toString()
            )
        )
    }
    SectionTitle("Admin Tools")
    ActionGrid(
        listOf(
            "Manage Users" to "Search, restrict, deactivate",
            "Manage Errands" to "View and remove records",
            "Reports" to "Review and resolve",
            "Flagged Errands" to "Moderation queue",
            "Ratings" to "Feedback review",
            "Moderation" to "Rule-based logs"
        ),
        listOf(
            { onOpenAdminArray("admin_get_users.php", "Manage Users") },
            { onOpenAdminArray("admin_get_errands.php", "Manage Errands") },
            { onOpenAdminArray("admin_get_reports.php", "Reports") },
            { onOpenAdminArray("admin_get_flagged_errands.php", "Flagged Errands") },
            { onNavigateToRatings() },
            { onOpenAdminArray("admin_get_flagged_errands.php", "Moderation Logs") }
        )
    )
    CampusButton("Student Dashboard", primary = false) { onSwitchToStudent() }
    CampusButton("Logout", primary = false) { onLogout() }
}
