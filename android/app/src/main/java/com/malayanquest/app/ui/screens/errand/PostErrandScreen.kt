package com.malayanquest.app.ui.screens.errand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.DangerRed
import com.malayanquest.app.util.categories
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
    var deadline by remember { mutableStateOf("2026-07-15 13:00:00") }
    var reward by remember { mutableStateOf("") }
    var rewardNote by remember { mutableStateOf("") }

    InfoPanel("Describe a small, safe, school-related errand.", Icons.Filled.Security, "Campus-only request")
    SectionCard("Errand Details", Icons.Filled.Assignment) {
        CampusTextField("Title", title, { title = it }, leadingIcon = Icons.Filled.Assignment)
        CampusTextField("Description", description, { description = it }, multiline = true, leadingIcon = Icons.Filled.Info)
        CategoryDropdown(category, categories) { category = it }
    }
    SectionCard("Locations", Icons.Filled.LocationOn) {
        CampusTextField("Pickup location", pickup, { pickup = it }, leadingIcon = Icons.Filled.LocationOn)
        CampusTextField("Drop-off location", dropoff, { dropoff = it }, leadingIcon = Icons.Filled.LocalShipping)
        InfoPanel("Room codes must use R or E and rooms 101-113, 201-213, 301-313, 401-413, or 501-513. Examples: R101, E413.", Icons.Filled.Info, "MCL room format")
    }
    SectionCard("Time and Reward", Icons.Filled.Schedule) {
        CampusTextField("Deadline: 2026-07-15 13:00:00", deadline, { deadline = it }, leadingIcon = Icons.Filled.CalendarMonth)
        CampusTextField("Reward amount, optional", reward, { reward = it }, KeyboardType.Decimal, leadingIcon = Icons.Filled.Payments)
        CampusTextField("Reward note, optional", rewardNote, { rewardNote = it }, leadingIcon = Icons.Filled.Info)
    }
    InfoPanel("Do not post errands involving confidential documents, prohibited items, exams, IDs, medicine, or unsafe tasks.", Icons.Filled.Report, "Safety Reminder", DangerRed)
    CampusButton("Submit Errand") {
        validateRoomLocation("Pickup", pickup)?.let {
            onShowToast(it)
            return@CampusButton
        }
        validateRoomLocation("Drop-off", dropoff)?.let {
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

private fun validateRoomLocation(label: String, location: String): String? {
    val roomWithPrefix = Regex("\\b([REre])[- ]?(\\d{3})\\b")
    val bareRoomNumber = Regex("\\b([1-5][0-9]{2})\\b")
    val normalized = location.trim()

    roomWithPrefix.findAll(normalized).forEach { match ->
        val roomNumber = match.groupValues[2].toIntOrNull() ?: return "$label room code is invalid."
        if (!isValidMclRoomNumber(roomNumber)) {
            return "$label room must be from R101-R113, E101-E113, up to R501-R513 or E501-E513."
        }
    }

    val withoutPrefixedRooms = roomWithPrefix.replace(normalized, "")
    if (bareRoomNumber.containsMatchIn(withoutPrefixedRooms)) {
        return "$label room code must start with R or E, example R101 or E413."
    }

    return null
}

private fun isValidMclRoomNumber(roomNumber: Int): Boolean {
    val floor = roomNumber / 100
    val room = roomNumber % 100
    return floor in 1..5 && room in 1..13
}
