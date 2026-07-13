package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.DangerRed
import org.json.JSONObject

@Composable
fun ReportScreen(
    api: ApiClient,
    userId: Int,
    errand: JSONObject?,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var reportedUserId by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Unsafe or prohibited request") }
    var details by remember { mutableStateOf("") }
    val reportReasons = listOf(
        "Unsafe or prohibited request",
        "Restricted area",
        "Harassment or threat",
        "Privacy concern",
        "School-rule violation",
        "Other safety concern"
    )

    InfoPanel("Reports help keep Malayan Quest safe.", Icons.Filled.Security, if (errand == null) "Report User" else "Report Errand", DangerRed)
    if (errand == null) {
        SectionCard("Report Details", Icons.Filled.Report) {
            CampusTextField("Reported user ID", reportedUserId, { reportedUserId = it }, KeyboardType.Number, leadingIcon = Icons.Filled.Person)
            GenericDropdown("Reason for reporting", reason, reportReasons) { reason = it }
            CampusTextField("Details - describe what happened", details, { details = it }, multiline = true, leadingIcon = Icons.Filled.Info)
        }
        ConfirmDangerButton(
            text = "Submit User Report",
            dialogTitle = "Submit this report?",
            dialogMessage = "This will send the report to admin records for review."
        ) {
            api.post("report_user.php", JSONObject().apply {
                put("reported_user_id", reportedUserId.toIntOrNull() ?: 0)
                put("reported_by_user_id", userId)
                put("reason", reason.trim())
                put("details", details.trim())
            }) { response ->
                onShowToast(response.optString("message"))
                if (response.optBoolean("success")) onSuccess()
            }
        }
    } else {
        CampusCard { ErrandCore(errand) }
        SectionCard("Report Details", Icons.Filled.Report) {
            GenericDropdown("Reason for reporting", reason, reportReasons) { reason = it }
            CampusTextField("Additional details - describe what happened", details, { details = it }, multiline = true, leadingIcon = Icons.Filled.Info)
        }
        ConfirmDangerButton(
            text = "Submit Report",
            dialogTitle = "Submit this report?",
            dialogMessage = "This will send the errand report to admin records for review."
        ) {
            api.post("report_errand.php", JSONObject().apply {
                put("errand_id", errand.optInt("errand_id"))
                put("reporter_id", userId)
                put("reason", (reason + if (details.isBlank()) "" else ": ${details.trim()}").trim())
            }) { response ->
                onShowToast(response.optString("message"))
                if (response.optBoolean("success")) onSuccess()
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
