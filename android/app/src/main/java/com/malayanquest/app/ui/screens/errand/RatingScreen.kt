package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun RatingScreen(
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
    var score by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }

    CampusCard { ErrandCore(errand) }
    SectionCard("Rate Helper", Icons.Filled.Star) {
        RatingRow(score.ifBlank { "0" })
        CampusTextField("Rating 1-5", score, { score = it }, KeyboardType.Number, leadingIcon = Icons.Filled.Star)
        CampusTextField("Feedback", feedback, { feedback = it }, multiline = true, leadingIcon = Icons.Filled.Info)
    }
    CampusButton("Submit Rating") {
        api.post("submit_rating.php", JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put("rated_user_id", errand.optInt("selected_helper_id"))
            put("rated_by_user_id", userId)
            put("rating_score", score.toIntOrNull() ?: 0)
            put("feedback", feedback.trim())
        }) { response ->
            onShowToast(response.optString("message"))
            if (response.optBoolean("success")) {
                onSuccess()
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
