package com.malayanquest.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.theme.*
import org.json.JSONObject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun ErrandCard(
    errand: JSONObject,
    applyMode: Boolean,
    userId: Int,
    onViewDetails: (JSONObject) -> Unit,
    onApply: (JSONObject) -> Unit,
    onViewApplicants: (JSONObject) -> Unit,
    onMessages: (JSONObject) -> Unit,
    onConfirmCompletion: (JSONObject) -> Unit,
    onRateHelper: (JSONObject) -> Unit,
    onCancelErrand: (JSONObject) -> Unit,
    onReportErrand: (JSONObject) -> Unit,
    onStatusTracker: (JSONObject) -> Unit,
    onUpdateStatus: ((JSONObject, String) -> Unit)? = null,
    onPostStatus: ((String, JSONObject, String, String) -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    val status = errand.optString("status")
    val requesterId = errand.optInt("requester_id")
    val selectedHelperId = errand.optInt("selected_helper_id")
    val isRequester = userId == requesterId
    val isHelper = userId == selectedHelperId || errand.optString("application_status") == "selected"

    CampusCard {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                ErrandCore(errand)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Actions", tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View Details") },
                        onClick = { showMenu = false; onViewDetails(errand) },
                        leadingIcon = { Icon(Icons.Filled.Info, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Status Tracker") },
                        onClick = { showMenu = false; onStatusTracker(errand) },
                        leadingIcon = { Icon(Icons.Filled.History, null) }
                    )
                    if (status in listOf("Open", "Has Applicants", "Assigned", "Accepted", "In Progress")) {
                        DropdownMenuItem(
                            text = { Text("Cancel Errand") },
                            onClick = { showMenu = false; onCancelErrand(errand) },
                            leadingIcon = { Icon(Icons.Filled.Cancel, null, tint = DangerRed) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Report Problem") },
                        onClick = { showMenu = false; onReportErrand(errand) },
                        leadingIcon = { Icon(Icons.Filled.Flag, null, tint = WarningAmber) }
                    )
                }
            }
        }
        
        Divider(color = BorderSoft)

        // Contextual Primary Action
        when {
            applyMode -> {
                CampusButton("Apply as Helper") { onApply(errand) }
            }
            isRequester -> {
                when (status) {
                    "Open", "Has Applicants" -> CampusButton("View Applicants") { onViewApplicants(errand) }
                    "Completed by Helper" -> CampusButton("Confirm Completion") { onConfirmCompletion(errand) }
                    "Confirmed by Requester" -> CampusButton("Rate Helper") { onRateHelper(errand) }
                    else -> if (selectedHelperId > 0) CampusButton("Open Chat", primary = false) { onMessages(errand) }
                }
            }
            isHelper -> {
                when (status) {
                    "Assigned" -> CampusButton("Accept Task") { onPostStatus?.invoke("accept_assigned_errand.php", errand, "helper_id", "Accepted") }
                    "Accepted" -> CampusButton("Mark In Progress") { onUpdateStatus?.invoke(errand, "In Progress") }
                    "In Progress" -> CampusButton("Mark Complete") { onUpdateStatus?.invoke(errand, "Completed by Helper") }
                    else -> CampusButton("Open Chat", primary = false) { onMessages(errand) }
                }
            }
        }
    }
}

@Composable
fun RequesterActions(
    errand: JSONObject,
    userId: Int,
    onViewApplicants: (JSONObject) -> Unit,
    onMessages: (JSONObject) -> Unit,
    onConfirmCompletion: (JSONObject) -> Unit,
    onRateHelper: (JSONObject) -> Unit,
    onCancelErrand: (JSONObject) -> Unit
) {
    val status = errand.optString("status")
    if (status == "Has Applicants" || status == "Open") {
        CampusButton("View Applicants") { onViewApplicants(errand) }
    }
    if (errand.optInt("selected_helper_id") > 0) {
        CampusButton("Open Chat", primary = false) { onMessages(errand) }
    }
    if (status == "Completed by Helper") {
        CampusButton("Confirm Completion") { onConfirmCompletion(errand) }
    }
    if (status == "Confirmed by Requester") {
        CampusButton("Rate Helper") { onRateHelper(errand) }
    }
    if (status in listOf("Open", "Has Applicants", "Assigned", "Accepted", "In Progress")) {
        CampusButton("Cancel Errand", primary = false) { onCancelErrand(errand) }
    }
}

@Composable
fun HelperActions(
    errand: JSONObject,
    userId: Int,
    onUpdateStatus: (JSONObject, String) -> Unit,
    onPostStatus: (String, JSONObject, String, String) -> Unit,
    onMessages: (JSONObject) -> Unit,
    onCancel: (JSONObject) -> Unit,
    onStatusTracker: (JSONObject) -> Unit
) {
    val status = errand.optString("status")
    if (errand.optString("application_status") == "selected" && status == "Assigned") {
        CampusButton("Accept Task") { onPostStatus("accept_assigned_errand.php", errand, "helper_id", "Accepted") }
    }
    if (status == "Accepted") {
        CampusButton("Mark In Progress") { onUpdateStatus(errand, "In Progress") }
    }
    if (status == "In Progress") {
        CampusButton("Mark Complete") { onUpdateStatus(errand, "Completed by Helper") }
    }
    if (errand.optInt("selected_helper_id") == userId || errand.optString("application_status") == "selected") {
        CampusButton("Open Chat", primary = false) { onMessages(errand) }
        CampusButton("Cancel/Withdraw", primary = false) { onCancel(errand) }
    }
    CampusButton("Status Tracker", primary = false) { onStatusTracker(errand) }
}

@Composable
fun ErrandCore(errand: JSONObject) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colorForStatus(errand.optString("status")).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(categoryVector(errand.optString("category")), contentDescription = errand.optString("category"), tint = colorForStatus(errand.optString("status")), modifier = Modifier.size(23.dp))
        }
        Text(
            errand.optString("title", "Untitled"),
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Badge(errand.optString("status", "Open"), colorForStatus(errand.optString("status")))
    }
    Badge("${categoryIcon(errand.optString("category"))} ${errand.optString("category")}", SecondaryBlue)
    DetailRow("Pickup", errand.optString("pickup_location"))
    DetailRow("Drop-off", errand.optString("dropoff_location"))
    DetailRow("Deadline", errand.optString("deadline"))
    DetailRow("Reward", rewardText(errand))
}

@Composable
fun ApplicantCard(
    applicant: JSONObject,
    errandId: Int,
    onHelperProfile: (JSONObject) -> Unit,
    onSelectHelper: (JSONObject, Int) -> Unit
) {
    CampusCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserAvatar(applicant.optString("helper_name"), size = 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(applicant.optString("helper_name"), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                RatingRow(applicant.optString("average_rating", "0.00"))
            }
            Badge(applicant.optString("status", "pending"), colorForStatus(applicant.optString("status")))
        }
        DetailRow("Completed", applicant.optString("completed_errands", "0") + " errands")
        DetailRow("Offer", applicant.optString("offer_note"))
        DetailRow("Estimate", applicant.optString("estimated_completion_time"))
        CampusButton("Helper Profile", primary = false) { onHelperProfile(applicant) }
        if (applicant.optString("status") == "pending") {
            CampusButton("Select Helper") { onSelectHelper(applicant, errandId) }
        }
    }
}

@Composable
fun AdminRecordCard(item: JSONObject, title: String, clickable: Boolean = true, onView: (JSONObject, String) -> Unit) {
    CampusCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor(title).copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(actionIcon(title), contentDescription = title, tint = iconColor(title), modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(recordHeadline(item, title), color = TextSecondary, fontSize = 13.sp)
            }
            recordStatus(item)?.let { Badge(it, colorForStatus(it)) }
        }
        StructuredRecord(item)
        if (clickable) {
            CampusButton("View") { onView(item, title) }
        }
    }
}

@Composable
fun StructuredRecord(item: JSONObject) {
    val hidden = setOf("password_hash")
    val priority = listOf(
        "full_name", "role", "verification_status", "account_status",
        "title", "category", "status", "moderation_status",
        "reason", "report_status", "rating_score", "feedback",
        "requester_name", "helper_name", "created_at", "deadline"
    )
    val names = item.names() ?: return
    val keys = mutableListOf<String>()
    priority.forEach { if (item.has(it) && it !in hidden) keys.add(it) }
    for (i in 0 until names.length()) {
        val key = names.optString(i)
        if (key !in hidden && key !in keys && keys.size < 8) keys.add(key)
    }
    keys.take(8).forEach { key ->
        DetailRow(key.replace("_", " "), item.optString(key))
    }
}

// Helper functions for Errand Components
fun rewardText(errand: JSONObject): String {
    val amount = errand.optString("reward_amount", "")
    val note = errand.optString("reward_note", "")
    return if (amount.isBlank() || amount == "null") {
        note.ifBlank { "None" }
    } else {
        "PHP $amount" + if (note.isBlank() || note == "null") "" else " - $note"
    }
}

fun categoryIcon(category: String?): String {
    return when {
        category == null -> "[ ]"
        category.contains("Food", ignoreCase = true) -> "[Food]"
        category.contains("Printing", ignoreCase = true) -> "[Print]"
        category.contains("Document", ignoreCase = true) -> "[Doc]"
        category.contains("Bookstore", ignoreCase = true) || category.contains("Bluebook", ignoreCase = true) -> "[Book]"
        category.contains("Delivery", ignoreCase = true) -> "[Move]"
        else -> "[Task]"
    }
}

fun categoryVector(category: String?): ImageVector {
    return when {
        category == null -> Icons.Filled.Assignment
        category.contains("Food", ignoreCase = true) -> Icons.Filled.Storefront
        category.contains("Printing", ignoreCase = true) -> Icons.Filled.Assignment
        category.contains("Bookstore", ignoreCase = true) || category.contains("Bluebook", ignoreCase = true) -> Icons.Filled.Storefront
        category.contains("Delivery", ignoreCase = true) -> Icons.Filled.LocalShipping
        else -> Icons.Filled.Assignment
    }
}

fun recordHeadline(item: JSONObject, title: String): String {
    return when {
        item.has("full_name") -> item.optString("full_name")
        item.has("title") -> item.optString("title")
        item.has("reason") -> item.optString("reason")
        item.has("feedback") -> item.optString("feedback")
        else -> title
    }.ifBlank { title }
}

fun recordStatus(item: JSONObject): String? {
    return when {
        item.has("verification_status") -> item.optString("verification_status")
        item.has("account_status") -> item.optString("account_status")
        item.has("status") -> item.optString("status")
        item.has("moderation_status") -> item.optString("moderation_status")
        item.has("report_status") -> item.optString("report_status")
        else -> null
    }?.takeIf { it.isNotBlank() && it != "null" }
}
