package com.malayanquest.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.TextPrimary
import com.malayanquest.app.ui.theme.WarningAmber
import org.json.JSONObject

@Composable
fun UserRatingsScreen(
    api: ApiClient,
    userId: Int,
    onNavigateBack: () -> Unit
) {
    RemoteList(
        api = api,
        endpoint = "get_user_ratings.php",
        params = mapOf("user_id" to userId.toString()),
        emptyText = "No ratings yet."
    ) { item ->
        FeedbackCard(item)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}

@Composable
fun FeedbackCard(item: JSONObject) {
    CampusCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(WarningAmber.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Star, contentDescription = "Feedback", tint = WarningAmber, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Rating ${item.optString("rating_score", item.optString("score", "0"))}", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                RatingRow(item.optString("rating_score", "0"))
            }
        }
        DetailRow("Feedback", item.optString("feedback", "No feedback text."))
        DetailRow("Errand", item.optString("title", item.optString("errand_title", "Related errand")))
    }
}
