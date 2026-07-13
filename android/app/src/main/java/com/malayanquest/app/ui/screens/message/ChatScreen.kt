package com.malayanquest.app.ui.screens.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.*
import org.json.JSONObject

@Composable
fun ChatScreen(
    api: ApiClient,
    userId: Int,
    errand: JSONObject?,
    refreshKey: Int,
    onSendMessage: (Int, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")
        CampusButton("Back", primary = false) { onNavigateBack() }
        return
    }
    val requesterId = errand.optInt("requester_id")
    val helperId = errand.optInt("selected_helper_id")
    val receiverId = if (userId == requesterId) helperId else requesterId
    var message by remember { mutableStateOf("") }

    RemoteList(
        api = api,
        endpoint = "get_messages.php",
        params = mapOf("errand_id" to errand.optInt("errand_id").toString(), "user_id" to userId.toString()),
        emptyText = "No messages yet.",
        refreshKey = refreshKey
    ) { item ->
        ChatBubble(
            sender = item.optString("sender_name"),
            message = item.optString("message_text"),
            mine = item.optInt("sender_id") == userId
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            CampusTextField("Message", message, { message = it }, multiline = true)
        }
        CampusButton("Send", modifier = Modifier.width(92.dp)) {
            onSendMessage(receiverId, message)
            message = ""
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}

@Composable
fun ChatBubble(sender: String, message: String, mine: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (mine) PrimaryBlue else Color.White)
                .border(1.dp, if (mine) PrimaryBlue else BorderSoft, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(if (mine) "You" else sender, color = if (mine) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(message, color = if (mine) Color.White else TextPrimary, fontSize = 14.sp)
        }
    }
}
