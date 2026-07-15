package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.DangerRed
import com.malayanquest.app.util.categories
import com.malayanquest.app.util.deadlineError
import com.malayanquest.app.util.defaultDeadlineText
import com.malayanquest.app.util.limited
import com.malayanquest.app.util.optionalLengthError
import com.malayanquest.app.util.requiredLengthError
import com.malayanquest.app.util.rewardAmountError
import com.malayanquest.app.util.roomLocationError
import org.json.JSONObject

@Composable
fun PostErrandScreen(
    api: ApiClient,
    userId: Int,
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var pickup by remember { mutableStateOf("") }
    var dropoff by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(defaultDeadlineText()) }
    var reward by remember { mutableStateOf("") }
    var rewardNote by remember { mutableStateOf("") }

    InfoPanel("Describe a small, safe, school-related errand.", Icons.Filled.Security, "Campus-only request")
    SectionCard("Errand Details", Icons.AutoMirrored.Filled.Assignment) {
        CampusTextField("Title", title, { title = limited(it, 120) }, leadingIcon = Icons.AutoMirrored.Filled.Assignment)
        CampusTextField("Description", description, { description = limited(it, 1000) }, multiline = true, leadingIcon = Icons.Filled.Info)
        CategoryDropdown(category, categories) { category = it }
    }
    SectionCard("Locations", Icons.Filled.LocationOn) {
        CampusTextField("Pickup location", pickup, { pickup = limited(it, 150) }, leadingIcon = Icons.Filled.LocationOn)
        CampusTextField("Drop-off location", dropoff, { dropoff = limited(it, 150) }, leadingIcon = Icons.Filled.LocalShipping)
        InfoPanel("Room codes must use R or E and rooms 101-113, 201-213, 301-313, 401-413, or 501-513. Examples: R101, E413.", Icons.Filled.Info, "MCL room format")
    }
    SectionCard("Time and Reward", Icons.Filled.Schedule) {
        CampusTextField("Deadline: YYYY-MM-DD HH:MM:SS", deadline, { deadline = it }, leadingIcon = Icons.Filled.CalendarMonth)
        CampusTextField("Reward amount, optional", reward, { reward = limited(it, 8) }, KeyboardType.Decimal, leadingIcon = Icons.Filled.Payments)
        CampusTextField("Reward note, optional", rewardNote, { rewardNote = limited(it, 180) }, leadingIcon = Icons.Filled.Info)
    }
    InfoPanel("Do not post errands involving confidential documents, prohibited items, exams, IDs, medicine, or unsafe tasks.", Icons.Filled.Report, "Safety Reminder", DangerRed)
    CampusButton("Submit Errand") {
        listOfNotNull(
            requiredLengthError(title, "Title", 5, 120),
            requiredLengthError(description, "Description", 15, 1000),
            requiredLengthError(pickup, "Pickup location", 3, 150),
            requiredLengthError(dropoff, "Drop-off location", 3, 150),
            roomLocationError("Pickup", pickup),
            roomLocationError("Drop-off", dropoff),
            deadlineError(deadline),
            rewardAmountError(reward),
            optionalLengthError(rewardNote, "Reward note", 180)
        ).firstOrNull()?.let {
            onShowToast(it)
            return@CampusButton
        }
        val body = JSONObject().apply {
            put("requester_id", userId)
            put("title", title.trim())
            put("description", description.trim())
            put("category", category)
            put("pickup_location", pickup.trim())
            put("dropoff_location", dropoff.trim())
            put("deadline", deadline.trim())
            put("reward_note", rewardNote.trim())
            if (reward.trim().isNotEmpty()) put("reward_amount", reward.trim().toDoubleOrNull() ?: 0.0)
        }
        api.post("create_errand.php", body) { response ->
            onShowToast(response.optString("message"))
            if (response.optBoolean("success")) {
                onSuccess()
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
