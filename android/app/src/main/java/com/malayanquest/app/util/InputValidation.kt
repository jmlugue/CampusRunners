package com.malayanquest.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val fullNamePattern = Regex("""^[\p{L}]+(?:[ '-][\p{L}]+)*$""")
private val roomWithPrefix = Regex("""\b([REre])[- ]?(\d{3})\b""")
private val bareRoomNumber = Regex("""\b([1-5][0-9]{2})\b""")

fun limited(value: String, max: Int): String = value.take(max)

fun defaultDeadlineText(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(calendar.time)
}

fun fullNameError(name: String): String? {
    val trimmed = name.trim()
    return when {
        trimmed.length < 2 -> "Full name must contain at least 2 characters."
        trimmed.length > 100 -> "Full name must not exceed 100 characters."
        !fullNamePattern.matches(trimmed) -> "Full name may only use letters, spaces, apostrophes, and hyphens."
        else -> null
    }
}

fun requiredLengthError(value: String, label: String, min: Int, max: Int): String? {
    val trimmed = value.trim()
    return when {
        trimmed.length < min -> "$label must contain at least $min characters."
        trimmed.length > max -> "$label must not exceed $max characters."
        else -> null
    }
}

fun optionalLengthError(value: String, label: String, max: Int): String? {
    return if (value.trim().length > max) {
        "$label must not exceed $max characters."
    } else {
        null
    }
}

fun deadlineError(value: String): String? {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
        isLenient = false
    }
    val date = try {
        format.parse(value.trim())
    } catch (_: Exception) {
        null
    } ?: return "Deadline must use YYYY-MM-DD HH:MM:SS format."

    val now = Date()
    if (!date.after(now)) {
        return "Deadline must be in the future."
    }

    val max = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 30)
    }.time
    if (date.after(max)) {
        return "Deadline must be within the next 30 days."
    }

    return null
}

fun rewardAmountError(value: String): String? {
    val trimmed = value.trim()
    if (trimmed.isBlank()) {
        return null
    }

    val amount = trimmed.toDoubleOrNull()
        ?: return "Reward amount must be a number."

    return if (amount < 0.0 || amount > 1000.0) {
        "Reward amount must be between 0 and 1000."
    } else {
        null
    }
}

fun roomLocationError(label: String, location: String): String? {
    val normalized = location.trim()

    roomWithPrefix.findAll(normalized).forEach { match ->
        val roomNumber = match.groupValues[2].toIntOrNull()
            ?: return "$label room code is invalid."
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
