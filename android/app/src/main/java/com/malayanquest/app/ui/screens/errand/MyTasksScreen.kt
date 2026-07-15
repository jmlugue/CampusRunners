package com.malayanquest.app.ui.screens.errand

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun MyTasksScreen(
    api: ApiClient,
    userId: Int,
    refreshKey: Int,
    onNavigate: (Screen, JSONObject?) -> Unit,
    onUpdateStatus: (JSONObject, String) -> Unit,
    onPostStatus: (String, JSONObject, String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("As Requester") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TabStrip(
            tabs = listOf("As Requester", "As Helper"),
            selected = selectedTab,
            onSelected = { selectedTab = it }
        )

        if (selectedTab == "As Requester") {
            RemoteList(
                api = api,
                endpoint = "get_my_posted_errands.php",
                params = mapOf("user_id" to userId.toString()),
                emptyText = "You haven't requested any errands.",
                refreshKey = refreshKey
            ) { item ->
                if (!isArchived(item.optString("status"))) {
                    ErrandCard(
                        errand = item,
                        applyMode = false,
                        userId = userId,
                        onViewDetails = { onNavigate(Screen.ErrandDetails, it) },
                        onApply = {},
                        onViewApplicants = { onNavigate(Screen.Applicants, it) },
                        onMessages = { onNavigate(Screen.Chat, it) },
                        onConfirmCompletion = { onNavigate(Screen.Completion, it) },
                        onRateHelper = { onNavigate(Screen.Rating, it) },
                        onCancelErrand = { onNavigate(Screen.CancelErrand, it) },
                        onReportErrand = { onNavigate(Screen.Report, it) },
                        onStatusTracker = { onNavigate(Screen.Status, it) }
                    )
                }
            }
        } else {
            RemoteList(
                api = api,
                endpoint = "get_my_helper_errands.php",
                params = mapOf("user_id" to userId.toString()),
                emptyText = "You aren't doing any errands right now.",
                refreshKey = refreshKey
            ) { errand ->
                if (!isArchived(errand.optString("status"))) {
                    ErrandCard(
                        errand = errand,
                        applyMode = false,
                        userId = userId,
                        onViewDetails = { onNavigate(Screen.ErrandDetails, it) },
                        onApply = {},
                        onViewApplicants = {},
                        onMessages = { onNavigate(Screen.Chat, it) },
                        onConfirmCompletion = {},
                        onRateHelper = {},
                        onCancelErrand = { onNavigate(Screen.CancelErrand, it) },
                        onReportErrand = { onNavigate(Screen.Report, it) },
                        onStatusTracker = { onNavigate(Screen.Status, it) },
                        onUpdateStatus = onUpdateStatus,
                        onPostStatus = onPostStatus
                    )
                }
            }
        }
    }
}

private fun isArchived(status: String?): Boolean {
    val s = status ?: ""
    return s.contains("Closed") || s.contains("Cancel") || s.contains("Removed")
}