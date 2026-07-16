package com.malayanquest.app.ui.screens.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.WarningAmber
import org.json.JSONObject

@Composable
fun AdminRecordDetailsScreen(
    item: JSONObject?,
    title: String,
    onUpdateUserStatus: (Int, String, String) -> Unit,
    onRemoveErrand: (Int) -> Unit,
    onResolveReport: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (item == null) {
        EmptyState("No record selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    AdminRecordCard(item, title, clickable = false, onView = { _, _ -> })
    if (item.has("user_id")) {
        val verificationStatus = item.optString("verification_status")
        val accountStatus = item.optString("account_status")
        val isVerified = verificationStatus == "verified"
        val isRestricted = verificationStatus == "restricted"
        val isActive = accountStatus == "active"
        val isDeactivated = accountStatus == "deactivated"

        InfoPanel("Admin actions change account access and should only be used for verified moderation reasons.", Icons.Filled.Security, "Admin Actions", WarningAmber)
        CampusButton("Verify Student", enabled = !isVerified && !isDeactivated) { onUpdateUserStatus(item.optInt("user_id"), "", "verified") }
        CampusButton("Reactivate User", primary = false, enabled = !isActive || isRestricted) { onUpdateUserStatus(item.optInt("user_id"), "active", "verified") }
        ConfirmDangerButton("Restrict User", "Restrict this user?", "The account will be marked restricted for prototype moderation.", enabled = !isRestricted && isActive) { onUpdateUserStatus(item.optInt("user_id"), "", "restricted") }
        ConfirmDangerButton("Deactivate User", "Deactivate this user?", "The account will no longer be active until restored.", enabled = !isDeactivated) { onUpdateUserStatus(item.optInt("user_id"), "deactivated", "") }
    }
    if (item.has("errand_id")) {
        ConfirmDangerButton("Remove Errand", "Remove this errand?", "This action should only be used for inappropriate or unsafe errands.") { onRemoveErrand(item.optInt("errand_id")) }
    }
    if (item.has("report_id")) {
        CampusButton("Mark Report Resolved") { onResolveReport(item.optInt("report_id")) }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
