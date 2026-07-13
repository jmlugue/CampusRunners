package com.malayanquest.app.ui.screens.errand

import androidx.compose.runtime.Composable
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.screens.profile.HelperPreviewPanel
import org.json.JSONObject

@Composable
fun HelperProfileScreen(
    applicant: JSONObject?,
    onNavigateBack: () -> Unit
) {
    if (applicant == null) {
        EmptyState("No helper selected.")
    } else {
        HelperPreviewPanel(
            "Public helper card",
            applicant.optString("helper_name"),
            "Rating: ${applicant.optString("average_rating", "No rating yet")} • Completed errands: ${applicant.optString("completed_errands", "0")}",
            "Offer: ${applicant.optString("offer_note")}\nEstimate: ${applicant.optString("estimated_completion_time")}"
        )
        InfoPanel("Privacy rule: student numbers and school email addresses are not displayed on public helper cards.")
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
