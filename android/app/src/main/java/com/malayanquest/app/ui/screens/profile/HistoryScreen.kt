package com.malayanquest.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
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
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.*
import org.json.JSONObject

@Composable
fun HistoryScreen(
    api: ApiClient,
    userId: Int,
    onNavigate: (Screen) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("All") }
    TabStrip(
        tabs = listOf("All", "Posted", "As Helper"),
        selected = selectedTab,
        onSelected = { selectedTab = it }
    )
    RemoteList(
        api = api,
        endpoint = "get_user_history.php",
        params = mapOf("user_id" to userId.toString()),
        emptyText = "No history records yet."
    ) { item ->
        HistoryCard(item)
    }
    CampusButton("Ratings") { onNavigate(Screen.UserRatings) }
    CampusButton("Back", primary = false) { onNavigateBack() }
}

@Composable
fun HistoryCard(item: JSONObject) {
    CampusCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SuccessGreen.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.History, contentDescription = "History", tint = SuccessGreen, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.optString("title", "History Record"), color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(item.optString("created_at", item.optString("updated_at", "Recent activity")), color = TextSecondary, fontSize = 12.sp)
            }
            Badge(item.optString("status", "Recorded"), colorForStatus(item.optString("status")))
        }
        StructuredRecord(item)
    }
}
