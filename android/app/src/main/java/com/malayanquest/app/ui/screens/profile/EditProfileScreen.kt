package com.malayanquest.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.SecondaryBlue
import com.malayanquest.app.ui.theme.TextPrimary
import com.malayanquest.app.ui.theme.TextSecondary
import org.json.JSONObject

@Composable
fun EditProfileScreen(
    api: ApiClient,
    userId: Int,
    currentFullName: String,
    onSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentFullName) }
    HelperPreviewPanel("Editable profile", currentFullName, "Verified Student", "Only safe profile fields are editable in this prototype.")
    SectionCard("Display Information", Icons.Filled.Person) {
        CampusTextField("Full name", name, { name = it }, leadingIcon = Icons.Filled.Person)
        Text("School email and student number stay private verification records.", color = TextSecondary, fontSize = 12.sp)
    }
    CampusButton("Save Profile") {
        api.post("update_user_profile.php", JSONObject().apply {
            put("user_id", userId)
            put("full_name", name.trim())
        }) { response ->
            onShowToast(response.optString("message"))
            if (response.optBoolean("success")) {
                onSuccess(name.trim())
            }
        }
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}

@Composable
fun HelperPreviewPanel(eyebrow: String, name: String, stats: String, note: String) {
    CampusCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserAvatar(name, size = 56.dp)
            Column(modifier = Modifier.weight(1f)) {
                Badge(eyebrow, SecondaryBlue)
                Text(name.ifBlank { "Student Helper" }, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(stats, color = TextSecondary, fontSize = 13.sp)
            }
        }
        InfoPanel(note, Icons.Filled.Info, "Helper details")
    }
}
