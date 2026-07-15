package com.malayanquest.app.ui.screens.errand

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.*
import com.malayanquest.app.util.limited
import com.malayanquest.app.util.optionalLengthError
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

    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    CampusCard { ErrandCore(errand) }

    SectionCard("Rate Helper", Icons.Filled.Star) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "$i Star",
                    tint = if (i <= score) WarningAmber else TextSecondary.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { score = i }
                        .padding(8.dp)
                )
            }
        }

        CampusTextField("Feedback", feedback, { feedback = limited(it, 500) }, multiline = true, leadingIcon = Icons.Filled.Info)
    }

    CampusButton("Submit Rating") {
        if (score == 0) {
            onShowToast("Please select a star rating first.")
            return@CampusButton
        }

        optionalLengthError(feedback, "Feedback", 500)?.let {
            onShowToast(it)
            return@CampusButton
        }

        api.post("submit_rating.php", JSONObject().apply {
            put("errand_id", errand.optInt("errand_id"))
            put("rated_user_id", errand.optInt("selected_helper_id"))
            put("rated_by_user_id", userId)
            put("rating_score", score)
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
