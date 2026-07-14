package com.malayanquest.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.TextPrimary
import com.malayanquest.app.ui.theme.TextSecondary
import com.malayanquest.app.ui.theme.WarningAmber
import org.json.JSONObject

@Composable
fun AdminRatingsScreen(
    api: ApiClient,
    adminId: Int,
    onNavigateBack: () -> Unit
) {
    var keyword by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    CampusTextField(
        "Search user, errand, or feedback",
        keyword,
        { keyword = it },
        leadingIcon = Icons.Filled.Search
    )
    FilterChipRow(
        chips = listOf("All", "5 Stars", "4 Stars", "3 or Below"),
        selected = selectedFilter,
        onSelected = { selectedFilter = it }
    )
    RemoteList(
        api = api,
        endpoint = "admin_get_ratings.php",
        params = mapOf(
            "admin_id" to adminId.toString(),
            "keyword" to keyword.trim(),
            "filter" to selectedFilter
        ),
        emptyText = "No ratings match this filter."
    ) { item ->
        AdminRatingCard(item)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}

@Composable
private fun AdminRatingCard(item: JSONObject) {
    CampusCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(WarningAmber.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = WarningAmber, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.optString("rated_user_name", "Student"), color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Rated by ${item.optString("rated_by_name", "Requester")}", color = TextSecondary, fontSize = 12.sp)
            }
            RatingRow(item.optString("rating_score", "0"))
        }
        DetailRow("Feedback", item.optString("feedback", "No written feedback."))
        DetailRow("Errand", item.optString("errand_title", "Related errand"))
        DetailRow("Date", item.optString("created_at"))
    }
}
