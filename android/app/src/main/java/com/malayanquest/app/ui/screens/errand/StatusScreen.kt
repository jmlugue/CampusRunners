package com.malayanquest.app.ui.screens.errand

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.*
import org.json.JSONObject

@Composable
fun StatusScreen(
    errand: JSONObject?,
    userId: Int,
    onRequesterActions: @Composable (JSONObject) -> Unit,
    onHelperActions: @Composable (JSONObject) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    CampusCard { ErrandCore(errand) }
    StatusTracker(errand.optString("status"))
    CampusCard {
        Text("Current status", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Badge(errand.optString("status", "Open"), colorForStatus(errand.optString("status")))
        Text("Use the available action buttons below to move the errand through the prototype status flow.", color = TextSecondary)
    }
    if (errand.optInt("requester_id") == userId) {
        onRequesterActions(errand)
    } else {
        CampusCard { onHelperActions(errand) }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
