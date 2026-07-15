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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.theme.*
import org.json.JSONObject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*

@Composable
fun ErrandCard(
    errand: JSONObject,
    applyMode: Boolean,
    userId: Int,
    minimalDisplay: Boolean = false,
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

    // UPDATED: Pre-calculate if any buttons will actually render at the bottom
    val hasBottomActions = when {
        isRequester -> status in listOf("Open", "Has Applicants", "Assigned", "Accepted", "In Progress", "Completed by Helper", "Confirmed by Requester")
        isHelper -> status in listOf("Assigned", "Accepted", "In Progress", "Completed by Helper", "Confirmed by Requester")
        errand.optString("application_status") == "pending" -> true
        applyMode -> true
        else -> false
    }

    CampusCard {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                ErrandCore(errand, minimalDisplay)
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
                    if (isRequester && status in listOf("Open", "Has Applicants", "Assigned", "Accepted", "In Progress")) {
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

        // UPDATED: Only draw the divider and action block if there are buttons to show
        if (hasBottomActions) {
            HorizontalDivider(color = BorderSoft)

            when {
                isRequester -> {
                    when (status) {
                        "Open", "Has Applicants" -> CampusButton("View Applicants") { onViewApplicants(errand) }
                        "Assigned" -> {
                            Button(
                                onClick = { /* Disabled, does nothing */ },
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = BorderSoft,
                                    disabledContentColor = TextSecondary
                                )
                            ) {
                                Text("Waiting for Helper to Accept", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        "Accepted", "In Progress" -> CampusButton("Open Chat", primary = false) { onMessages(errand) }
                        "Completed by Helper" -> {
                            CampusButton("Confirm Completion") { onConfirmCompletion(errand) }
                            CampusButton("Open Chat", primary = false) { onMessages(errand) }
                        }
                        "Confirmed by Requester" -> CampusButton("Rate Helper") { onRateHelper(errand) }
                    }
                }
                isHelper -> {
                    when (status) {
                        "Assigned" -> CampusButton("Accept Task") { onPostStatus?.invoke("accept_assigned_errand.php", errand, "helper_id", "Accepted") }
                        "Accepted" -> {
                            CampusButton("Mark In Progress") { onUpdateStatus?.invoke(errand, "In Progress") }
                            CampusButton("Open Chat", primary = false) { onMessages(errand) }
                        }
                        "In Progress" -> {
                            CampusButton("Mark Complete") { onUpdateStatus?.invoke(errand, "Completed by Helper") }
                            CampusButton("Open Chat", primary = false) { onMessages(errand) }
                        }
                        "Completed by Helper" -> CampusButton("Open Chat", primary = false) { onMessages(errand) }
                        "Confirmed by Requester" -> {
                            Button(
                                onClick = { /* Disabled, does nothing */ },
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = BorderSoft,
                                    disabledContentColor = TextSecondary
                                )
                            ) {
                                Text("Waiting for Rating", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                errand.optString("application_status") == "pending" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { /* Disabled, does nothing */ },
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = BorderSoft,
                                disabledContentColor = TextSecondary
                            )
                        ) {
                            Text("Applied (Waiting)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        CampusButton("Cancel Application", primary = false) { onCancelErrand(errand) }
                    }
                }
                applyMode -> {
                    CampusButton("Apply as Helper") { onApply(errand) }
                }
            }
        }
    }
}

@Composable
fun RequesterActions(
    errand: JSONObject,
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

    if (status == "Assigned") {
        Button(
            onClick = { /* Disabled, does nothing */ },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = BorderSoft,
                disabledContentColor = TextSecondary
            )
        ) {
            Text("Waiting for Helper to Accept", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (status in listOf("Accepted", "In Progress", "Completed by Helper")) {
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

    if (status == "Confirmed by Requester") {
        Button(
            onClick = { /* Disabled, does nothing */ },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = BorderSoft,
                disabledContentColor = TextSecondary
            )
        ) {
            Text("Waiting for Rating", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (status in listOf("Accepted", "In Progress", "Completed by Helper")) {
        CampusButton("Open Chat", primary = false) { onMessages(errand) }
    }
    if (errand.optInt("selected_helper_id") == userId || errand.optString("application_status") == "selected") {
        CampusButton("Cancel/Withdraw", primary = false) { onCancel(errand) }
    }
    CampusButton("Status Tracker", primary = false) { onStatusTracker(errand) }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun ErrandCore(errand: JSONObject, minimalDisplay: Boolean = false) {
    val title = errand.optString("title", "Untitled")
    val description = errand.optString("description")
    val category = errand.optString("category", "Other School-Related Errand")
    val status = errand.optString("status", "Open")

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorForStatus(status).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    categoryVector(category),
                    contentDescription = category,
                    tint = colorForStatus(status),
                    modifier = Modifier.size(23.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                if (description.isNotBlank() && description != "null") {
                    Text(
                        description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Badge(status, colorForStatus(status))
            Badge(category, SecondaryBlue)
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (!minimalDisplay) {
                DetailRow("Pickup", errand.optString("pickup_location"))
                DetailRow("Drop-off", errand.optString("dropoff_location"))
                DetailRow("Deadline", errand.optString("deadline"))
            }
            DetailRow("Reward", rewardText(errand))
        }
    }
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

        if (applicant.optString("status") == "pending") {
            CampusButton("Select Helper") { onSelectHelper(applicant, errandId) }
        }
    }
}

@Composable
fun AdminRecordCard(item: JSONObject, title: String, clickable: Boolean = true, onView: (JSONObject, String) -> Unit) {
    val headline = recordHeadline(item, title)
    val subtitle = recordSubtitle(item)
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
                Text(headline, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = TextSecondary, fontSize = 13.sp)
                }
            }
            recordStatus(item)?.let { Badge(it, colorForStatus(it)) }
        }
        StructuredRecord(item, setOf("full_name", "title"))
        if (clickable) {
            CampusButton("View") { onView(item, title) }
        }
    }
}

@Composable
fun StructuredRecord(item: JSONObject, additionalHidden: Set<String> = emptySet()) {
    val hidden = setOf("password_hash") + additionalHidden
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

fun rewardText(errand: JSONObject): String {
    val amount = errand.optString("reward_amount", "")
    val note = errand.optString("reward_note", "")
    return if (amount.isBlank() || amount == "null") {
        note.ifBlank { "None" }
    } else {
        "PHP $amount" + if (note.isBlank() || note == "null") "" else " - $note"
    }
}

fun categoryVector(category: String?): ImageVector {
    return when {
        category == null -> Icons.AutoMirrored.Filled.Assignment
        category.contains("Food", ignoreCase = true) -> Icons.Filled.Storefront
        category.contains("Printing", ignoreCase = true) -> Icons.AutoMirrored.Filled.Assignment
        category.contains("Bookstore", ignoreCase = true) || category.contains("Bluebook", ignoreCase = true) -> Icons.Filled.Storefront
        category.contains("Delivery", ignoreCase = true) -> Icons.Filled.LocalShipping
        else -> Icons.AutoMirrored.Filled.Assignment
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

fun recordSubtitle(item: JSONObject): String {
    return when {
        item.has("school_email") -> item.optString("school_email")
        item.has("requester_name") -> "Requested by ${item.optString("requester_name")}"
        item.has("reported_by_name") -> "Reported by ${item.optString("reported_by_name")}"
        item.has("category") -> item.optString("category")
        item.has("report_type") -> item.optString("report_type").replace("_", " ")
        else -> ""
    }
}

fun recordStatus(item: JSONObject): String? {
    return when {
        item.optString("account_status") == "deactivated" -> "deactivated"
        item.optString("verification_status") == "restricted" -> "restricted"
        item.has("verification_status") -> item.optString("verification_status")
        item.has("account_status") -> item.optString("account_status")
        item.has("status") -> item.optString("status")
        item.has("moderation_status") -> item.optString("moderation_status")
        item.has("report_status") -> item.optString("report_status")
        else -> null
    }?.takeIf { it.isNotBlank() && it != "null" }
}