package com.malayanquest.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun HistoryScreen(
    api: ApiClient,
    userId: Int,
    onNavigate: (Screen, JSONObject?) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("All") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TabStrip(
            tabs = listOf("All", "Posted", "As Helper"),
            selected = selectedTab,
            onSelected = { selectedTab = it }
        )

        RemoteList(
            api = api,
            endpoint = "get_user_history.php",
            params = mapOf("user_id" to userId.toString()),
            emptyText = "No history records yet."
        ) { item ->

            val requesterId = item.optInt("requester_id")
            val helperId = item.optInt("selected_helper_id")

            val showItem = when (selectedTab) {
                "Posted" -> requesterId == userId
                "As Helper" -> helperId == userId
                else -> true
            }

            if (showItem) {
                ErrandCard(
                    errand = item,
                    applyMode = false,
                    userId = userId,
                    minimalDisplay = true,
                    onViewDetails = { onNavigate(Screen.ErrandDetails, it) },
                    onApply = {},
                    onViewApplicants = {},
                    onMessages = { onNavigate(Screen.Chat, it) },
                    onConfirmCompletion = {},
                    onRateHelper = {},
                    onCancelErrand = {},
                    onReportErrand = { onNavigate(Screen.Report, it) },
                    onStatusTracker = { onNavigate(Screen.Status, it) }
                )
            }
        }

        CampusButton("Ratings") { onNavigate(Screen.UserRatings, null) }
        CampusButton("Back", primary = false) { onNavigateBack() }
    }
}