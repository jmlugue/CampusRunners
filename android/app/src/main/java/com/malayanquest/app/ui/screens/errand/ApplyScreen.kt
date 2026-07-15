package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.*
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.screens.profile.HelperPreviewPanel
import org.json.JSONObject

@Composable
fun ApplyScreen(
    api: ApiClient,
    userId: Int,
    fullName: String,
    errand: JSONObject?,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    var offer by remember { mutableStateOf("") }
    var estimate by remember { mutableStateOf("") }

    CampusCard { ErrandCore(errand) }
    HelperPreviewPanel(
        "Helper Profile",
        fullName,
        "Rating and completed errands are shown as trust indicators.",
        "Offer note and estimated completion time are public to this requester only."
    )
    SectionCard("Helper Offer", Icons.Filled.PersonSearch) {
        CampusTextField("Offer note, example: I can do this before 12:30 PM.", offer, { offer = it }, multiline = true, leadingIcon = Icons.Filled.Info)
        CampusTextField("Estimated time, example: 20 minutes", estimate, { estimate = it }, leadingIcon = Icons.Filled.Schedule)
    }
    InfoPanel("Apply only if you can complete the errand safely and on time.", Icons.Filled.Security, "Trust reminder")
    CampusButton("Submit Application") {
        api.post("apply_to_errand.php", JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put("helper_id", userId)
            put("offer_note", offer.trim())
            put("estimated_completion_time", estimate.trim())
        }) { response ->
            onShowToast(response.optString("message"))
            if (response.optBoolean("success")) {
                onSuccess()
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
