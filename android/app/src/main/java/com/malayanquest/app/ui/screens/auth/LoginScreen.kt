package com.malayanquest.app.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ApiClient
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*
import com.malayanquest.app.ui.theme.PrimaryBlue
import com.malayanquest.app.ui.theme.TextSecondary
import org.json.JSONObject

@Composable
fun LoginScreen(
    api: ApiClient,
    onLoginSuccess: (JSONObject) -> Unit,
    onNavigateToRegister: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AppMark()
    AuthTitle("Welcome back!", "Sign in to continue your campus errands.")
    Badge("For MCL students only", PrimaryBlue)
    CampusTextField("School email", email, { email = it }, KeyboardType.Email, leadingIcon = Icons.Filled.Email)
    CampusTextField("Password", password, { password = it }, KeyboardType.Password, password = true, leadingIcon = Icons.Filled.Lock)
    CampusButton("Login") {
        api.post("login.php", JSONObject().apply {
            put("school_email", email.trim())
            put("password", password)
        }) { response ->
            if (response.optBoolean("success")) {
                response.optJSONObject("data")?.let {
                    onLoginSuccess(it)
                }
            } else {
                onShowToast(response.optString("message"))
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text("or", color = TextSecondary, fontSize = 12.sp)
    }
    CampusButton("Create student account", primary = false) { onNavigateToRegister() }
    InfoPanel("Demo accounts use password: password\nAdmin: admin@mcl.edu.ph\nStudent: juan.dcruz@mcl.edu.ph, maria.santos@mcl.edu.ph, carlo.reyes@mcl.edu.ph", Icons.Filled.Info, "Prototype accounts")
}
