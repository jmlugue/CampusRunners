package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun ErrandDetailsScreen(
    errand: JSONObject?,
    applyMode: Boolean,
    userId: Int,
    onApply: (JSONObject) -> Unit,
    onReport: (JSONObject) -> Unit,
    onNavigateBack: () -> Unit,
    // Requester actions
    onViewApplicants: (JSONObject) -> Unit,
    onMessages: (JSONObject) -> Unit,
    onConfirmCompletion: (JSONObject) -> Unit,
    onRateHelper: (JSONObject) -> Unit,
    onCancelErrand: (JSONObject) -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    CampusCard { ErrandCore(errand) }
    InfoPanel("Only accept if the request is safe, school-related, and limited to campus or nearby MCL locations.", Icons.Filled.Security, "Safety Note")
    SectionTitle("Status Tracker")
    StatusTracker(errand.optString("status"))
    if (applyMode) {
        CampusButton("Apply as Helper") {
            onApply(errand)
        }
    } else {
        RequesterActions(
            errand = errand,
            userId = userId,
            onViewApplicants = onViewApplicants,
            onMessages = onMessages,
            onConfirmCompletion = onConfirmCompletion,
            onRateHelper = onRateHelper,
            onCancelErrand = onCancelErrand
        )
    }
    CampusButton("Report Errand", primary = false) {
        onReport(errand)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
