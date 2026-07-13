package com.malayanquest.app.ui.screens.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.malayanquest.app.ApiClient
import com.malayanquest.app.ui.components.*
import org.json.JSONObject

@Composable
fun RegisterScreen(
    api: ApiClient,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AppMark()
    AuthTitle("Create Student Account", "Use your MMCL student details to join Malayan Quest.")
    InfoPanel("Use your school email and student number. Student numbers and school emails are stored for verification and are not shown publicly.", Icons.Filled.Info, "Private verification")

    CampusTextField("Full name", name, { name = it }, leadingIcon = Icons.Filled.Person)

    CampusTextField("School email, example: student@mcl.edu.ph", email, { email = it }, KeyboardType.Email, leadingIcon = Icons.Filled.Email)

    CampusTextField(
        "Student number",
        studentNumber,
        { newValue ->
            studentNumber = newValue.filter { it.isDigit() }.take(10)
        },
        keyboardType = KeyboardType.Number,
        leadingIcon = Icons.Filled.Badge
    )

    CampusTextField("Password", password, { password = it }, KeyboardType.Password, password = true, leadingIcon = Icons.Filled.Lock)
    CampusTextField("Confirm password", confirm, { confirm = it }, KeyboardType.Password, password = true, leadingIcon = Icons.Filled.CheckCircle)

    InfoPanel("I agree to use Malayan Quest only for safe, school-related errands.", Icons.Filled.Security, "Safety agreement")

    CampusButton("Register") {
        if (studentNumber.length != 10) {
            onShowToast("Student number must be exactly 10 digits.")
            return@CampusButton
        }

        if (password != confirm) {
            onShowToast("Passwords do not match.")
            return@CampusButton
        }

        api.post("register.php", JSONObject().apply {
            put("full_name", name.trim())
            put("school_email", email.trim())
            put("student_number", studentNumber.trim())
            put("password", password)
        }) { response ->
            onShowToast(response.optString("message"))
            if (response.optBoolean("success")) onRegisterSuccess()
        }
    }
    CampusButton("Back to login", primary = false) { onNavigateBack() }
}