package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.SuccessGreen
import org.json.JSONObject

@Composable
fun CompletionScreen(
    errand: JSONObject?,
    onConfirmCompletion: (JSONObject) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    InfoPanel("Confirm only after the helper has completed the agreed errand.", Icons.Filled.CheckCircle, "Completion confirmation", SuccessGreen)
    CampusCard { ErrandCore(errand) }
    StatusTracker(errand.optString("status"))
    CampusButton("Confirm Completion") {
        onConfirmCompletion(errand)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
