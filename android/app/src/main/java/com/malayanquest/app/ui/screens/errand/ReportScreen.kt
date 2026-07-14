package com.malayanquest.app.ui.screens.errand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    var selectedErrand by remember(errand?.optInt("errand_id") ?: 0) { mutableStateOf(errand) }
    var keyword by remember { mutableStateOf("") }
    var appliedKeyword by remember { mutableStateOf("") }
    var localRefreshKey by remember { mutableIntStateOf(0) }
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

    InfoPanel("Browse recent errands or search for a specific errand, then submit the report to admin records.", Icons.Filled.Security, "Report Errand", DangerRed)

    if (selectedErrand == null) {
        SectionCard("Find Errand", Icons.Filled.Search) {
            CampusTextField("Search by title, category, or location", keyword, { keyword = it }, leadingIcon = Icons.Filled.Search)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampusButton("Search", modifier = Modifier.weight(1f)) {
                    appliedKeyword = keyword.trim()
                    localRefreshKey++
                }
                CampusButton("Recent", primary = false, modifier = Modifier.weight(1f)) {
                    keyword = ""
                    appliedKeyword = ""
                    localRefreshKey++
                }
            }
        }
        SectionTitle(if (appliedKeyword.isBlank()) "Recent Errands" else "Search Results")
        RemoteList(
            api = api,
            endpoint = "get_reportable_errands.php",
            params = mapOf(
                "user_id" to userId.toString(),
                "keyword" to appliedKeyword
            ),
            emptyText = "No matching errands found.",
            refreshKey = localRefreshKey
        ) { item ->
            if (item.optInt("requester_id") != userId) {
                CampusCard {
                    ErrandCore(item)
                    CampusButton("Report This Errand", primary = false) {
                        selectedErrand = item
                    }
                }
            }
        }
    } else {
        val isOwnErrand = selectedErrand!!.optInt("requester_id") == userId

        CampusCard {
            ErrandCore(selectedErrand!!)
            CampusButton(if (isOwnErrand) "Browse Other Errands" else "Choose Different Errand", primary = false) {
                selectedErrand = null
            }
        }

        if (isOwnErrand) {
            InfoPanel("You cannot report an errand that you requested.", Icons.Filled.Info, "Action Unavailable", DangerRed)
        } else {
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
                    put("errand_id", selectedErrand!!.optInt("errand_id"))
                    put("reported_by_user_id", userId)
                    put("reason", reason.trim())
                    put("details", details.trim())
                }) { response ->
                    onShowToast(response.optString("message"))
                    if (response.optBoolean("success")) onSuccess()
                }
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
