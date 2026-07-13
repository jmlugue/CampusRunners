package com.malayanquest.app.ui.screens.errand

import androidx.compose.runtime.Composable
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun ApplicantsScreen(
    api: ApiClient,
    userId: Int,
    errand: JSONObject?,
    onNavigate: (Screen, JSONObject?) -> Unit,
    onSelectHelper: (JSONObject, Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    RemoteList(
        api = api,
        endpoint = "get_errand_applicants.php",
        params = mapOf("errand_id" to errand.optInt("errand_id").toString(), "requester_id" to userId.toString()),
        emptyText = "No applicants yet."
    ) { applicant ->
        ApplicantCard(
            applicant = applicant,
            errandId = errand.optInt("errand_id"),
            onHelperProfile = { onNavigate(Screen.HelperProfile, it) },
            onSelectHelper = onSelectHelper
        )
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
