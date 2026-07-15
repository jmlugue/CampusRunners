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

    /*
     * The Boolean callback returns whether sending
     * the message succeeded.
     */
    onSendMessage: (
        receiverId: Int,
        message: String,
        onResult: (Boolean) -> Unit
    ) -> Unit,

    onNavigateBack: () -> Unit
) {
    if (errand == null) {
        EmptyState("No errand selected.")

        CampusButton(
            text = "Back",
            primary = false
        ) {
            onNavigateBack()
        }

        return
    }

    val errandId = errand.optInt("errand_id")
    val requesterId = errand.optInt("requester_id")
    val helperId = errand.optInt("selected_helper_id")

    /*
     * When the current user is the requester,
     * the selected helper receives the message.
     *
     * When the current user is the helper,
     * the requester receives the message.
     */
    val receiverId = if (userId == requesterId) {
        helperId
    } else {
        requesterId
    }

    /*
     * Using errandId as the remember key prevents a
     * previous errand's message from appearing in another chat.
     */
    var message by remember(errandId) {
        mutableStateOf("")
    }

    var isSending by remember {
        mutableStateOf(false)
    }

    var sendError by remember {
        mutableStateOf("")
    }

    /*
     * Show a warning instead of silently trying
     * to send to receiver ID 0.
     */
    if (receiverId <= 0) {
        InfoPanel(
            "Chat is unavailable because no valid helper " +
                    "is assigned to this errand."
        )
    }

    RemoteList(
        api = api,
        endpoint = "get_messages.php",
        params = mapOf(
            "errand_id" to errandId.toString(),
            "user_id" to userId.toString()
        ),
        emptyText = "No messages yet.",
        refreshKey = refreshKey
    ) { item ->

        ChatBubble(
            sender = item.optString("sender_name"),
            message = item.optString("message_text"),
            mine = item.optInt("sender_id") == userId
        )
    }

    if (sendError.isNotBlank()) {
        InfoPanel(sendError)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            CampusTextField(
                label = "Message",
                value = message,
                onValueChange = {
                    message = it

                    // Remove the previous error when typing again.
                    if (sendError.isNotBlank()) {
                        sendError = ""
                    }
                },
                multiline = true
            )
        }

        CampusButton(
            text = if (isSending) {
                "Sending..."
            } else {
                "Send"
            },
            modifier = Modifier.width(110.dp)
        ) {
            when {
                receiverId <= 0 -> {
                    sendError =
                        "Cannot send because no valid receiver is assigned."
                }

                message.isBlank() -> {
                    sendError = "Enter a message first."
                }

                isSending -> {
                    // Ignore repeated taps while sending.
                }

                else -> {
                    isSending = true
                    sendError = ""

                    onSendMessage(
                        receiverId,
                        message.trim()
                    ) { success ->

                        isSending = false

                        if (success) {
                            // Clear only after successful sending.
                            message = ""
                        } else {
                            /*
                             * Keep the original text so the user
                             * can try sending it again.
                             */
                            sendError =
                                "Message was not sent. You can try again."
                        }
                    }
                }
            }
        }
    }

    CampusButton(
        text = "Back",
        primary = false
    ) {
        onNavigateBack()
    }
}

@Composable
fun ChatBubble(
    sender: String,
    message: String,
    mine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (mine) PrimaryBlue else Color.White
                )
                .border(
                    width = 1.dp,
                    color = if (mine) PrimaryBlue else BorderSoft,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = if (mine) "You" else sender,
                color = if (mine) {
                    Color.White
                } else {
                    TextSecondary
                },
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Text(
                text = message,
                color = if (mine) {
                    Color.White
                } else {
                    TextPrimary
                },
                fontSize = 14.sp
            )
        }
    }
}

