package com.malayanquest.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.*
import org.json.JSONObject

@Composable
fun ProfileScreen(
    api: ApiClient,
    userId: Int,
    fullName: String,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    RemoteObject(
        api = api,
        endpoint = "get_user_profile.php",
        params = mapOf("user_id" to userId.toString()),
        emptyText = "Profile unavailable."
    ) { data ->
        ProfileHeader(data, fullName)
        SectionCard("Account Info", Icons.Filled.Security) {
            SecureAccountRow("School email", data.optString("school_email"), Icons.Filled.Email)
            SecureAccountRow("Student number", data.optString("student_number"), Icons.Filled.Badge)
        }
    }
    
    SectionTitle("Settings & Tools")
    
    MenuRow("Edit Profile", "Update your display name", Icons.Filled.Person) { onNavigate(Screen.EditProfile) }
    MenuRow("History", "Review completed and cancelled errands", Icons.Filled.History) { onNavigate(Screen.History) }
    MenuRow("Report a Problem", "Send a safety report to admin", Icons.Filled.Flag) { onNavigate(Screen.Report) }
    MenuRow("Privacy", "Your data is kept private", Icons.Filled.Lock) { }
    
    Spacer(Modifier.height(12.dp))
    DangerButton("Logout") { onLogout() }
}

@Composable
fun ProfileHeader(data: JSONObject, fallbackFullName: String) {
    val fullName = data.optString("full_name", fallbackFullName)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryBlue)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(initials(fullName), color = PrimaryBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Text(fullName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Badge("Verified Student", Color.White, fill = Color(0xFF0A63D8), border = Color.White)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ProfileStat("Completed", data.optString("completed_errands", "0"), Modifier.weight(1f))
            ProfileStat("Rating", data.optString("average_rating", "0.00"), Modifier.weight(1f))
            ProfileStat("Account", data.optString("account_status", "active"), Modifier.weight(1f))
        }
    }
}

@Composable
fun SecureAccountRow(label: String, value: String?, icon: ImageVector) {
    var visible by remember(label, value) { mutableStateOf(false) }
    val displayValue = if (visible) {
        displayAccountValue(value)
    } else {
        maskAccountValue(value)
    }

    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEAF3FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = SecondaryBlue, modifier = Modifier.size(18.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(label.uppercase(), color = SecondaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(displayValue, color = TextPrimary, fontSize = 15.sp)
        }

        IconButton(onClick = { visible = !visible }, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = if (visible) "Hide $label" else "Show $label",
                tint = SecondaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun displayAccountValue(value: String?): String {
    return if (value.isNullOrBlank() || value == "null") "Not specified" else value
}

private fun maskAccountValue(value: String?): String {
    val text = displayAccountValue(value)
    if (text == "Not specified") return text

    return if (text.contains("@")) {
        val parts = text.split("@", limit = 2)
        "${maskText(parts[0])}@${maskDomain(parts[1])}"
    } else {
        maskText(text)
    }
}

private fun maskText(value: String): String {
    if (value.length <= 2) return "*".repeat(value.length.coerceAtLeast(1))
    return value.first() + "*".repeat((value.length - 2).coerceAtLeast(1)) + value.last()
}

private fun maskDomain(domain: String): String {
    val dotIndex = domain.lastIndexOf('.')
    if (dotIndex <= 0) return maskText(domain)
    return maskText(domain.substring(0, dotIndex)) + domain.substring(dotIndex)
}

@Composable
fun MenuRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, action: () -> Unit) {
    CampusCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { action() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = TextSecondary, fontSize = 12.sp)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = "Open", tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
