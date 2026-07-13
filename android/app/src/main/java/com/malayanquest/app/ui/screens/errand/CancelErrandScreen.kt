package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.DangerRed
import org.json.JSONObject

@Composable
fun CancelErrandScreen(
    api: ApiClient,
    userId: Int,
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
    var reason by remember { mutableStateOf("") }
    InfoPanel("Cancellation may affect trust and reliability.", Icons.Filled.Cancel, "Cancel carefully", DangerRed)
    CampusCard { ErrandCore(errand) }
    SectionCard("Reason", Icons.Filled.Info) {
        CampusTextField("Explain why this errand is being cancelled", reason, { reason = it }, multiline = true, leadingIcon = Icons.Filled.Info)
    }
    ConfirmDangerButton(
        text = "Cancel Errand",
        dialogTitle = "Cancel this errand?",
        dialogMessage = "This action will be recorded in the errand history.",
        confirmText = "Cancel Errand"
    ) {
        api.post("cancel_errand.php", JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put("user_id", userId)
            put("reason", reason.trim())
        }) { response ->
            onShowToast(response.optString("message"))
            if (response.optBoolean("success")) {
                onSuccess()
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
